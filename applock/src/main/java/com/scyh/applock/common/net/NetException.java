package com.scyh.applock.common.net;

public class NetException extends Exception {

	private String msg;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NetException(String string) {
		this.msg=string;
	}

	public NetException() {
	}

	@Override
	public String getMessage() {
		if(this.msg!=null&&!this.msg.equals("")){
			return this.msg;
		}else{
			return super.getMessage();
		}
	}

}
