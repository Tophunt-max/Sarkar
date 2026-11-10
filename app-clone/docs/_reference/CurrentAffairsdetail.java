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
import b.b.b.c;
import c.c.b.b.a.f;
import c.c.b.b.a.g;
import c.c.b.b.a.i;
import com.facebook.shimmer.ShimmerFrameLayout;

/* loaded from: classes.dex */
public class CurrentAffairsdetail extends c {
    public ShimmerFrameLayout A;
    public Context B;
    public Intent C;
    public WebView D;
    public TextView E;
    public i F;
    public ImageView x;
    public TextView y;
    public TextView z;

    public void M() {
        this.F = new i(this);
        RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.adMobView);
        this.F.setAdUnitId(c.d.a.e.a.a(this.B, "admob_banner"));
        adContainer.addView(this.F);
        O();
    }

    public final void O() {
        f adRequest = new f.a().c();
        g adSize = N();
        this.F.setAdSize(adSize);
        this.F.b(adRequest);
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
        this.B = this;
        new c.d.a.g.c(this);
        this.C = getIntent();
        M();
        this.C.getStringExtra("slugid");
        this.A = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.x = (ImageView) findViewById(R.id.backbutton);
        TextView textView = (TextView) findViewById(R.id.titlebar);
        this.E = textView;
        textView.setText("Details");
        this.y = (TextView) findViewById(R.id.heading);
        this.z = (TextView) findViewById(R.id.time);
        this.D = (WebView) findViewById(R.id.descrption);
        this.x.setOnClickListener(new a());
        this.D.getSettings().setJavaScriptEnabled(true);
        this.D.getSettings().setGeolocationEnabled(true);
        this.D.setSoundEffectsEnabled(true);
        this.D.setWebViewClient(new WebViewClient());
        this.y.setText(this.C.getStringExtra("title"));
        this.z.setText(this.C.getStringExtra("publishdate"));
        this.D.setWebViewClient(new b());
        this.D.loadDataWithBaseURL(null, this.C.getStringExtra("content"), "text/html; charset=utf-8", "UTF-8", null);
        this.A.d();
        this.A.setVisibility(8);
    }

    public class a implements View.OnClickListener {
        public a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            CurrentAffairsdetail.this.onBackPressed();
        }
    }

    public class b extends WebViewClient {
        public b() {
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            CurrentAffairsdetail.this.D.loadUrl("javascript:(function() { document.getElementsByTagName('h1')[0].style.display = 'none'; })()");
        }
    }

    @Override // b.m.b.e, android.app.Activity
    public void onResume() {
        super.onResume();
        this.A.c();
    }

    @Override // b.m.b.e, android.app.Activity
    public void onPause() {
        this.A.d();
        super.onPause();
    }
}
