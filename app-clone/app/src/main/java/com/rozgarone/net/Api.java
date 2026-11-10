package com.rozgarone.net;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Thin wrapper around a single Volley request queue plus the backend base URL.
 *
 * <p>Endpoint contract (identical to the original app). Like the original, every call is
 * an HTTP POST with form-encoded parameters (REA: {@code c.d.a.g.c.a} is a Volley
 * {@code StringRequest} built with method 1 = POST whose {@code getParams()} returns the
 * caller's map).
 * <pre>
 *   POST api/appsettings.php                       {status, appsettings:[{admob_banner,...}]}
 *   POST api/getgovtjob.php                        {status, govtjob:[{postid,jobTitle,...}]}
 *   POST api/getcurrentaffairs.php                 {status, currentaffairs:[...]}
 *   POST api/getresultpost.php                     {status, resultpost:[...]}
 *   POST api/getdailynews.php                      {status, dailynews:[...]}
 *   POST api/getexpiredpost.php                    {status, govtjob:[...]}
 *   POST api/getfilter.php       filter=X          {status, filter:[{id,category_name}]}
 *   POST api/getpostbyfilter.php filter=X&labelid=N {status, govtjob:[...]}
 *   POST api/getnotification.php                   {status, notification:[{maintitle,...}]}
 * </pre>
 */
public class Api {

    private final RequestQueue queue;
    private final String baseUrl;

    public Api(@NonNull Context context, @NonNull String baseUrl) {
        this.queue = Volley.newRequestQueue(context.getApplicationContext());
        String b = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        // Accept a base that already points at api/ so both ".../" and ".../api/" work.
        if (b.endsWith("/api/")) b = b.substring(0, b.length() - "api/".length());
        this.baseUrl = b;
    }

    public String base() {
        return baseUrl;
    }

    public String url(String endpoint) {
        return baseUrl + (endpoint.startsWith("/") ? endpoint.substring(1) : endpoint);
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
