package com.rozgarone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import c.c.b.b.a.c0.b;
import c.c.b.b.a.c0.c;
import c.c.b.b.a.e;
import c.c.b.b.a.f;
import c.c.b.b.a.m;
import c.c.b.b.a.o;
import c.c.b.b.a.r;
import c.c.b.b.a.u;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdView;
import java.util.Arrays;

/* loaded from: classes.dex */
public class MainActivity extends b.b.b.c {
    public CardView A;
    public CardView B;
    public CardView C;
    public CardView D;
    public CardView E;
    public ImageView F;
    public c.c.b.b.a.c0.b G;
    public c.c.b.b.a.a0.a H;
    public CardView x;
    public CardView y;
    public CardView z;

    public final void N(c.c.b.b.a.c0.b nativeAd, NativeAdView adView) {
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

    public void P(FrameLayout frameLayout) {
        e.a builder = new e.a(getApplication(), c.d.a.e.a.a(getApplicationContext(), "admob_native"));
        builder.c(new d(frameLayout));
        u videoOptions = new u.a().a();
        c.a aVar = new c.a();
        aVar.g(videoOptions);
        c.c.b.b.a.c0.c adOptions = aVar.a();
        builder.g(adOptions);
        builder.e(new e(this));
        c.c.b.b.a.e adLoader = builder.a();
        adLoader.a(new f.a().c());
    }

    public class d implements b.c {

        /* renamed from: a, reason: collision with root package name */
        public final /* synthetic */ FrameLayout f9431a;

        public d(FrameLayout frameLayout) {
            this.f9431a = frameLayout;
        }

        @Override // c.c.b.b.a.c0.b.c
        public void a(c.c.b.b.a.c0.b nativeAd) {
            boolean isDestroyed = MainActivity.this.isDestroyed();
            if (!isDestroyed && !MainActivity.this.isFinishing() && !MainActivity.this.isChangingConfigurations()) {
                if (MainActivity.this.G != null) {
                    MainActivity.this.G.a();
                }
                MainActivity.this.G = nativeAd;
                NativeAdView adView = (NativeAdView) MainActivity.this.getLayoutInflater().inflate(R.layout.mobnative, (ViewGroup) null);
                MainActivity.this.N(nativeAd, adView);
                this.f9431a.removeAllViews();
                this.f9431a.addView(adView);
                return;
            }
            nativeAd.a();
        }
    }

    public class e extends c.c.b.b.a.c {
        public e(MainActivity this$0) {
        }

        @Override // c.c.b.b.a.c
        public void c(m loadAdError) {
        }
    }

    public void O() {
        P((FrameLayout) findViewById(R.id.mobadslayout));
    }

    public void M() {
        c.c.b.b.a.f adRequest = new f.a().c();
        r.a aVar = new r.a();
        aVar.b(Arrays.asList("1ADAD30F02CD84CDE72190C2ABE5EB5E"));
        r configuration = aVar.a();
        o.a(configuration);
        c.c.b.b.a.a0.a.a(getApplicationContext(), c.d.a.e.a.a(getApplicationContext(), "admob_interstitial"), adRequest, new f());
    }

    public class f extends c.c.b.b.a.a0.b {
        public f() {
        }

        @Override // c.c.b.b.a.d
        /* renamed from: c, reason: merged with bridge method [inline-methods] */
        public void b(c.c.b.b.a.a0.a interstitialAd) {
            MainActivity.this.H = interstitialAd;
            interstitialAd.b(new a(this));
        }

        public class a extends c.c.b.b.a.l {
            public a(f this$1) {
            }

            @Override // c.c.b.b.a.l
            public void b() {
            }

            @Override // c.c.b.b.a.l
            public void c(c.c.b.b.a.a adError) {
            }

            @Override // c.c.b.b.a.l
            public void e() {
            }
        }

        @Override // c.c.b.b.a.d
        public void a(m loadAdError) {
        }
    }

    public final void Q() {
        c.c.b.b.a.a0.a aVar = this.H;
        if (aVar != null) {
            aVar.d(this);
        }
    }

    @Override // b.m.b.e, androidx.activity.ComponentActivity, b.i.b.f, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_main);
        M();
        O();
        this.x = (CardView) findViewById(R.id.findgovjob);
        this.y = (CardView) findViewById(R.id.currentaffairs);
        this.z = (CardView) findViewById(R.id.dailynews);
        this.A = (CardView) findViewById(R.id.examresult);
        this.B = (CardView) findViewById(R.id.category);
        this.C = (CardView) findViewById(R.id.profession);
        this.D = (CardView) findViewById(R.id.qualification);
        this.E = (CardView) findViewById(R.id.location);
        this.F = (ImageView) findViewById(R.id.notification);
        this.x.setOnClickListener(new g());
        this.y.setOnClickListener(new h());
        this.z.setOnClickListener(new i());
        this.A.setOnClickListener(new j());
        this.B.setOnClickListener(new k());
        this.C.setOnClickListener(new l());
        this.D.setOnClickListener(new a());
        this.E.setOnClickListener(new b());
        this.F.setOnClickListener(new c());
    }

    public class g implements View.OnClickListener {
        public g() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) AllGovjob.class));
            MainActivity.this.Q();
        }
    }

    public class h implements View.OnClickListener {
        public h() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Currentaffairs.class));
            MainActivity.this.Q();
        }
    }

    public class i implements View.OnClickListener {
        public i() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Dailynews.class));
            MainActivity.this.Q();
        }
    }

    public class j implements View.OnClickListener {
        public j() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Examresults.class));
            MainActivity.this.Q();
        }
    }

    public class k implements View.OnClickListener {
        public k() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Category.class).putExtra("title", "Category"));
            MainActivity.this.Q();
        }
    }

    public class l implements View.OnClickListener {
        public l() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Category.class).putExtra("title", "Profession"));
            MainActivity.this.Q();
        }
    }

    public class a implements View.OnClickListener {
        public a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Category.class).putExtra("title", "Qualification"));
            MainActivity.this.Q();
        }
    }

    public class b implements View.OnClickListener {
        public b() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Category.class).putExtra("title", "Location"));
            MainActivity.this.Q();
        }
    }

    public class c implements View.OnClickListener {
        public c() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) Notification.class));
            MainActivity.this.Q();
        }
    }
}
