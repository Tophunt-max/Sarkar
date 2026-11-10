package com.gsbussiness.sarkariexamupdates;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import c.c.b.b.a.f;
import c.c.b.b.a.g;
import c.c.b.b.a.i;
import com.facebook.shimmer.ShimmerFrameLayout;

/* loaded from: classes.dex */
public class AllgovjobContent extends b.b.b.c {
    public TextView A;
    public ShimmerFrameLayout B;
    public Context C;
    public Intent D;
    public WebView E;
    public ImageView F;
    public String G;
    public i H;
    public ImageView x;
    public TextView y;
    public TextView z;

    public void M() {
        this.H = new i(this);
        RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.adMobView);
        this.H.setAdUnitId(c.d.a.e.a.a(this.C, "admob_banner"));
        adContainer.addView(this.H);
        O();
    }

    public final void O() {
        f adRequest = new f.a().c();
        g adSize = N();
        this.H.setAdSize(adSize);
        this.H.b(adRequest);
    }

    public final g N() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return g.a(this, adWidth);
    }

    @Override // b.m.b.e, androidx.activity.ComponentActivity, b.i.b.f, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allgovjob_content);
        getWindow().setFlags(1024, 1024);
        this.C = this;
        M();
        new c.d.a.g.c(this.C);
        Intent intent = getIntent();
        this.D = intent;
        intent.getStringExtra("slugid");
        this.G = this.D.getStringExtra("expiredate");
        this.B = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.x = (ImageView) findViewById(R.id.backbutton);
        this.y = (TextView) findViewById(R.id.heading);
        this.z = (TextView) findViewById(R.id.time);
        this.A = (TextView) findViewById(R.id.lastdate);
        this.F = (ImageView) findViewById(R.id.alarm);
        this.E = (WebView) findViewById(R.id.descrption);
        this.x.setOnClickListener(new a());
        this.F.setVisibility(0);
        this.F.setOnClickListener(new b());
        this.E.getSettings().setJavaScriptEnabled(true);
        this.E.getSettings().setAppCacheEnabled(true);
        this.E.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getPath());
        this.E.getSettings().setCacheMode(-1);
        this.E.setWebViewClient(new WebViewClient());
        this.y.setText(this.D.getStringExtra("title"));
        this.z.setText(this.D.getStringExtra("publishdate"));
        this.A.setText("Last Date: " + this.G);
        this.E.setWebViewClient(new c());
        this.E.loadDataWithBaseURL(null, this.D.getStringExtra("content"), "text/html; charset=utf-8", "UTF-8", null);
        this.B.d();
        this.B.setVisibility(8);
    }

    public class a implements View.OnClickListener {
        public a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            AllgovjobContent.this.onBackPressed();
        }
    }

    public class b implements View.OnClickListener {
        public b() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent i = new Intent("android.intent.action.SET_ALARM");
            i.putExtra("android.intent.extra.alarm.MESSAGE", AllgovjobContent.this.y.getText().toString());
            i.putExtra("android.intent.extra.alarm.DAYS", AllgovjobContent.this.G);
            AllgovjobContent.this.startActivity(i);
        }
    }

    public class c extends WebViewClient {
        public c() {
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            AllgovjobContent.this.E.loadUrl("javascript:(function() { document.getElementsByTagName('h1')[0].style.display = 'none'; })()");
        }
    }

    @Override // b.m.b.e, android.app.Activity
    public void onResume() {
        super.onResume();
        this.B.c();
    }

    @Override // b.m.b.e, android.app.Activity
    public void onPause() {
        this.B.d();
        super.onPause();
    }
}
