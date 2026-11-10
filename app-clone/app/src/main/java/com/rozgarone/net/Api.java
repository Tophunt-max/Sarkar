package com.rozgarone.net;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Thin wrapper around a single Volley request queue plus the backend base URL.
 *
 * <p>Endpoint contract (identical to the original app):
 * <pre>
 *   GET api/appsettings.php          {status, appsettings:[{admob_banner,...}]}
 *   GET api/getgovtjob.php           {status, govtjob:[{postid,jobTitle,jobContent,...}]}
 *   GET api/getcurrentaffairs.php    {status, currentaffairs:[...]}
 *   GET api/getresultpost.php        {status, resultpost:[...]}
 *   GET api/getdailynews.php         {status, dailynews:[...]}
 *   GET api/getexpiredpost.php       {status, govtjob:[...]}
 *   GET api/getfilter.php?filter=X   {status, filter:[{id,category_name}]}
 *   GET api/getpostbyfilter.php?filter=X&labelid=N  {status, govtjob:[...]}
 *   GET api/getnotification.php      {status, notification:[{maintitle,secondtitle,publishdate}]}
 * </pre>
 */
public class Api {

    private final RequestQueue queue;
    private final String baseUrl;

    public Api(@NonNull Context context, @NonNull String baseUrl) {
        this.queue = Volley.newRequestQueue(context.getApplicationContext());
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    public String base() {
        return baseUrl;
    }

    public String url(String endpoint) {
        return baseUrl + endpoint;
    }

    public RequestQueue queue() {
        return queue;
    }

    public <T> void add(Request<T> request) {
        queue.add(request);
    }

    public void cancelAll(Object tag) {
        queue.cancelAll(tag);
    }
}
