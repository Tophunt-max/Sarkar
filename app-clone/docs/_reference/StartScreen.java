package com.gsbussiness.sarkariexamupdates;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import c.c.b.b.a.c0.b;
import c.c.b.b.a.c0.c;
import c.c.b.b.a.e;
import c.c.b.b.a.f;
import c.c.b.b.a.m;
import c.c.b.b.a.u;
import c.c.b.b.l.i;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gsbussiness.sarkariexamupdates.StartScreen;

/* loaded from: classes.dex */
public class StartScreen extends b.b.b.c {
    public TextView A;
    public c.c.b.b.a.c0.b B;
    public boolean x = false;
    public RelativeLayout y;
    public RelativeLayout z;

    public final void M(c.c.b.b.a.c0.b nativeAd, NativeAdView adView) {
        MediaView mediaView = (MediaView) adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.d());
        if (nativeAd.b() == null) {
            adView.getBodyView().setVisibility(4);
        } else {
            adView.getBodyView().setVisibility(0);
            ((TextView) adView.getBodyView()).setText(nativeAd.b());
        }
        if (nativeAd.c() == null) {
            adView.getCallToActionView().setVisibility(4);
        } else {
            adView.getCallToActionView().setVisibility(0);
            ((Button) adView.getCallToActionView()).setText(nativeAd.c());
        }
        if (nativeAd.e() == null) {
            adView.getIconView().setVisibility(8);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.e().a());
            adView.getIconView().setVisibility(0);
        }
        adView.setNativeAd(nativeAd);
    }

    public void O(FrameLayout frameLayout) {
        e.a builder = new e.a(getApplication(), c.d.a.e.a.a(getApplicationContext(), "admob_native"));
        builder.c(new a(frameLayout));
        u videoOptions = new u.a().a();
        c.a aVar = new c.a();
        aVar.g(videoOptions);
        c.c.b.b.a.c0.c adOptions = aVar.a();
        builder.g(adOptions);
        builder.e(new b(this));
        c.c.b.b.a.e adLoader = builder.a();
        adLoader.a(new f.a().c());
    }

    public class a implements b.c {

        /* renamed from: a, reason: collision with root package name */
        public final /* synthetic */ FrameLayout f9442a;

        public a(FrameLayout frameLayout) {
            this.f9442a = frameLayout;
        }

        @Override // c.c.b.b.a.c0.b.c
        public void a(c.c.b.b.a.c0.b nativeAd) {
            boolean isDestroyed = StartScreen.this.isDestroyed();
            if (!isDestroyed && !StartScreen.this.isFinishing() && !StartScreen.this.isChangingConfigurations()) {
                if (StartScreen.this.B != null) {
                    StartScreen.this.B.a();
                }
                StartScreen.this.B = nativeAd;
                NativeAdView adView = (NativeAdView) StartScreen.this.getLayoutInflater().inflate(R.layout.mobnative, (ViewGroup) null);
                StartScreen.this.M(nativeAd, adView);
                this.f9442a.removeAllViews();
                this.f9442a.addView(adView);
                return;
            }
            nativeAd.a();
        }
    }

    public class b extends c.c.b.b.a.c {
        public b(StartScreen this$0) {
        }

        @Override // c.c.b.b.a.c
        public void c(m loadAdError) {
        }
    }

    public void N() {
        O((FrameLayout) findViewById(R.id.mobadslayout));
    }

    @Override // b.m.b.e, androidx.activity.ComponentActivity, b.i.b.f, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_start_screen);
        N();
        FirebaseMessaging.f().i().b(new c.c.b.b.l.d() { // from class: c.d.a.b
            @Override // c.c.b.b.l.d
            public final void a(i iVar) {
                StartScreen.this.V(iVar);
            }
        });
        this.z = (RelativeLayout) findViewById(R.id.nextpage);
        this.y = (RelativeLayout) findViewById(R.id.sharewithfriend);
        this.A = (TextView) findViewById(R.id.privacy);
        this.z.setOnClickListener(new c());
        this.A.setOnClickListener(new d());
        this.y.setOnClickListener(new e());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: U, reason: merged with bridge method [inline-methods] */
    public /* synthetic */ void V(i task) {
        if (!task.o()) {
            c.d.a.e.a.b(getApplicationContext(), "firebase_token", "providetokenhere");
            return;
        }
        String token = (String) task.k();
        Log.e(":::token", "---" + token);
        c.d.a.e.a.b(getApplicationContext(), "firebase_token", token);
    }

    public class c implements View.OnClickListener {
        public c() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            StartScreen.this.startActivity(new Intent(StartScreen.this, (Class<?>) MainActivity.class));
        }
    }

    public class d implements View.OnClickListener {
        public d() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            StartScreen.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(StartScreen.this.getString(R.string.privacy))));
        }
    }

    public class e implements View.OnClickListener {
        public e() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            try {
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", StartScreen.this.getString(R.string.app_name));
                String shareMessage = StartScreen.this.getString(R.string.app_name) + "\nDownload now and find Sarkari jobs now get it from play store now";
                shareIntent.putExtra("android.intent.extra.TEXT", shareMessage + "https://play.google.com/store/apps/details?id=" + StartScreen.this.getPackageName());
                StartScreen.this.startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
            }
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.x) {
            moveTaskToBack(true);
            Process.killProcess(Process.myPid());
            System.exit(1);
        } else {
            this.x = true;
            Toast.makeText(this, "double tap to exit!", 0).show();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: c.d.a.a
                @Override // java.lang.Runnable
                public final void run() {
                    StartScreen.this.T();
                }
            }, 2000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: S, reason: merged with bridge method [inline-methods] */
    public /* synthetic */ void T() {
        this.x = false;
    }
}
