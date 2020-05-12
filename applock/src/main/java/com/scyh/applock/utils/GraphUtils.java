package com.scyh.applock.utils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.scyh.applock.AppContext;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class GraphUtils {
    public GraphUtils() {
        super();
    }

    private static Bitmap drawableToBitmap(Drawable arg8) {
        int v4 = arg8.getIntrinsicWidth();
        int v3 = arg8.getIntrinsicHeight();
        Bitmap.Config v2 = arg8.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap v0 = Bitmap.createBitmap(v4, v3, v2);
        Canvas v1 = new Canvas(v0);
        arg8.setBounds(0, 0, v4, v3);
        arg8.draw(v1);
        return v0;
    }

    public static byte[] getBitmapAsByteArray(Bitmap arg3) {
        ByteArrayOutputStream v0 = new ByteArrayOutputStream();
        arg3.compress(Bitmap.CompressFormat.PNG, 0, ((OutputStream)v0));
        return v0.toByteArray();
    }

    public static Bitmap loadBitmap(Context arg1, int arg2) {
        return GraphUtils.loadBitmap(arg1.getResources(), arg2);
    }

    public static Bitmap loadBitmap(Resources arg5, int arg6) {
        Bitmap v0;
        try {
            v0 = BitmapFactory.decodeResource(arg5, arg6);
        }
        catch(Throwable v3) {
            System.gc();
            System.gc();
            try {
                v0 = BitmapFactory.decodeResource(arg5, arg6);
            }
            catch(Throwable v4) {
                v0 = null;
            }
        }

        return v0;
    }

    public static Bitmap zoomBitmap(Bitmap arg10, int arg11, int arg12) {
        int v3 = arg10.getWidth();
        int v4 = arg10.getHeight();
        Matrix v5 = new Matrix();
        v5.postScale((((float)arg11)) / (((float)v3)), (((float)arg12)) / (((float)v4)));
        return Bitmap.createBitmap(arg10, 0, 0, v3, v4, v5, true);
    }

    public static Drawable zoomDrawable(Drawable arg10, int arg11, int arg12) {
        int v3 = arg10.getIntrinsicWidth();
        int v4 = arg10.getIntrinsicHeight();
        Bitmap v0 = GraphUtils.drawableToBitmap(arg10);
        Matrix v5 = new Matrix();
        v5.postScale((((float)arg11)) / (((float)v3)), (((float)arg12)) / (((float)v4)));
        return new BitmapDrawable(AppContext.getInstance().getResources(), Bitmap.createBitmap(v0, 0, 0, v3, v4, v5, true));
    }
}

