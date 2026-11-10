package com.gsbussiness.sarkariexamupdates.SarkariHelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

/* loaded from: classes.dex */
public class ConstClass {

    /* renamed from: a, reason: collision with root package name */
    public static String f9436a;

    /* renamed from: b, reason: collision with root package name */
    public static String f9437b;

    /* renamed from: c, reason: collision with root package name */
    public static String f9438c;

    /* renamed from: d, reason: collision with root package name */
    public static String f9439d;
    public static String e;
    public static String f;
    public static String g;
    public static String h;
    public static String i;
    public static String j;

    public static native String appsettings();

    public static native String baseurl();

    public static native String categoryurl();

    public static native String getcurrentaffairs();

    public static native String getdailynews();

    public static native String getexpiredpost();

    public static native String getgovtjob();

    public static native String getnotification();

    public static native String getpostbyfilter();

    public static native String getresultpost();

    static {
        System.loadLibrary("hello-jni");
        f9436a = baseurl();
        f9437b = getgovtjob();
        f9438c = getcurrentaffairs();
        f9439d = getresultpost();
        e = getdailynews();
        f = categoryurl();
        g = getpostbyfilter();
        h = getnotification();
        i = appsettings();
        j = getexpiredpost();
    }

    public static boolean c(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void a(Context cntx, String message) {
        Toast.makeText(cntx, message, 1).show();
    }

    public static Spanned b(String html) {
        if (Build.VERSION.SDK_INT >= 24) {
            Spanned result = Html.fromHtml(html, 0);
            return result;
        }
        Spanned result2 = Html.fromHtml(html);
        return result2;
    }
}
