package com.scyh.applock.common.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Callable;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonNetCallable implements Callable<String> {
	/**
	 * GET：GET可以说是最常见的了，它本质就是发送一个请求来取得服务器上的某一资源。资源通过一组HTTP头和呈现数据（如HTML文本，
	 * 或者图片或者视频等）返回给客户端。GET请求中，永远不会包含呈现数据。
	 */
	public static final int HTTP_METHOD_GET = 1;
	/**
	 * POST：向服务器提交数据。这个方法用途广泛，几乎目前所有的提交操作都是靠这个完成。
	 */
	public static final int HTTP_METHOD_POST = 2;
	/**
	 * PUT：这个方法比较少见。HTML表单也不支持这个。本质上来讲，
	 * PUT和POST极为相似，都是向服务器发送数据，但它们之间有一个重要区别，PUT通常指定了资源的存放位置，而POST则没有，
	 * POST的数据存放位置由服务器自己决定。举个例子：如一个用于提交博文的URL，/addBlog。如果用PUT，则提交的URL会是像这样的”/
	 * addBlog/abc123”，其中abc123就是这个博文的地址。而如果用POST，则这个地址会在提交后由服务器告知客户端。
	 * 目前大部分博客都是这样的。显然，PUT和POST用途是不一样的。具体用哪个还取决于当前的业务场景。
	 */
	public static final int HTTP_METHOD_PUT = 3;
	/**
	 * DELETE：删除某一个资源。基本上这个也很少见，不过还是有一些地方比如amazon的S3云服务里面就用的这个方法来删除资源。
	 */
	public static final int HTTP_METHOD_DELETE = 4;
	/**
	 * HEAD：HEAD和GET本质是一样的，区别在于HEAD不含有呈现数据，而仅仅是HTTP头信息。有的人可能觉得这个方法没什么用，其实不是这样的。
	 * 想象一个业务情景：欲判断某个资源是否存在，我们通常使用GET，但这里用HEAD则意义更加明确。
	 */
	public static final int HTTP_METHOD_HEAD = 5;
	/**
	 * OPTIONS：这个方法很有趣，但极少使用。它用于获取当前URL所支持的方法。若请求成功，则它会在HTTP头中包含一个名为“Allow”的头，
	 * 值是所支持的方法，如“GET, POST”。
	 */
	public static final int HTTP_METHOD_OPTIONS = 6;
	
	public static final int HTTP_METHOD_STREAM=7;

	private int type;
	private String url;
	private String data;

	public CommonNetCallable(int type, String url, String data) {
		this.type = type;
		this.url = url;
		this.data = data;
	}

	@Override
	public String call() throws Exception {
		switch (type) {
		case HTTP_METHOD_GET:// get
			return requestGet(url);
		case HTTP_METHOD_POST:// post
			return requestPost(url, data);
		case HTTP_METHOD_PUT:// put
			return requestPut(url, data);
		case HTTP_METHOD_DELETE:// delete
			return requestDelete(url, data);
		case HTTP_METHOD_STREAM:
		default:
			throw new Exception("未知操作类型");
		}
		
	}

	public static String requestGet(String url) throws Exception  {
		String result = "";
		URL realURL = null;
		HttpURLConnection conn = null;
		BufferedReader bufReader = null;
		String line = "";
		try {
			realURL = new URL(url);
			conn = (HttpURLConnection) realURL.openConnection();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setConnectTimeout(10000);
			conn.connect();
			if (conn.getResponseCode() == 200) {
				bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			} else {
				bufReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
			}
			while ((line = bufReader.readLine()) != null) {
				result += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	private static String requestPut(String httpUrl, String dataJson) throws MalformedURLException ,ProtocolException,UnsupportedEncodingException,IOException,Exception{
		URL url;
		String result = null;
		byte[] buffer = dataJson.getBytes();

		try {
			url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");
			conn.setConnectTimeout(20 * 1000);
			conn.setReadTimeout(20 * 1000);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("Content-Length", buffer.length + "");

			OutputStream outStream = conn.getOutputStream();
			outStream.write(buffer);
			outStream.flush();
			outStream.close();
			if (conn.getResponseCode() == 200) {
				result = new String(readInputStream(conn.getInputStream()), "utf-8");
			} else {
				result = new String(readInputStream(conn.getErrorStream()), "utf-8");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

//	public static String sendStream(String httpUrl, String dataJson) {
//		URL url;
//		String result = null;
//		String requestData = "";
////		if (dataJson != null) {
////			try {
////				dataJson = Base64.encodeToString(dataJson.getBytes("utf-8"),
////						Base64.DEFAULT);
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
////			requestData += dataJson;
////		}
//		byte[] buffer =Convert.hexStringToBytes(dataJson);
//		System.err.println("--------------长度："+buffer.length);
//
//		try {
//			url = new URL(httpUrl);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod("POST");
//			conn.setConnectTimeout(20 * 1000);
//			conn.setReadTimeout(20 * 1000);
//			conn.setDoOutput(true);
//			conn.setRequestProperty("Content-Type",
//					"application/octet-stream");
//			conn.setRequestProperty("Content-Length", buffer.length + "");
//			conn.setRequestProperty("User-Agent", "MicroMessenger Client");
//			conn.setRequestProperty("Upgrade", "mmtls");
//			conn.setRequestProperty("Host", "short.weixin.qq.com");
//			conn.setRequestProperty("Cache-Control", "no-cache");
//			OutputStream outStream = conn.getOutputStream();
//			outStream.write(buffer);
//			outStream.flush();
//			outStream.close();
//			if (conn.getResponseCode() == 200) {
////				result = new String(Base64.decode(
////						readInputStream(conn.getInputStream()), Base64.DEFAULT),"utf-8");
//				result =new String(readInputStream(conn.getInputStream()),"utf-8");
//			}
//			conn.disconnect();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
	public static String requestPost(String httpUrl, String dataJson) throws Exception {
		URL url;
		String result = null;
		byte[] buffer = dataJson.getBytes();
		try {
			url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(20 * 1000);
			conn.setReadTimeout(20 * 1000);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("Content-Length", buffer.length + "");
			OutputStream outStream = conn.getOutputStream();
			outStream.write(buffer);
			outStream.flush();
			outStream.close();
			if (conn.getResponseCode() == 200) {
				result = new String(readInputStream(conn.getInputStream()), "utf-8");
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	private static String requestDelete(String httpUrl, String dataJson) throws Exception {
		URL url;
		String result = null;
		try {
			url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setConnectTimeout(20 * 1000);
			conn.setReadTimeout(20 * 1000);
			if (conn.getResponseCode() == 200) {
				result = new String(readInputStream(conn.getInputStream()), "utf-8");
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	private static byte[] readInputStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inputStream.close();
		return outSteam.toByteArray();
	}

	public static boolean isNetUsable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

}
