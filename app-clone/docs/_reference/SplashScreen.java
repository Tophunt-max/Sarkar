package com.rozgarone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import b.b.b.c;
import c.a.a.t;
import c.c.b.b.a.f;
import c.c.b.b.a.l;
import c.c.b.b.a.m;
import c.c.b.b.a.o;
import c.c.b.b.a.r;
import c.d.a.g.d;
import com.rozgarone.SarkariHelper.ConstClass;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SplashScreen extends c {
    public c.c.b.b.a.a0.a x;
    public Context y;
    public c.d.a.g.c z;

    public void M() {
        f adRequest = new f.a().c();
        r.a aVar = new r.a();
        aVar.b(Arrays.asList("1ADAD30F02CD84CDE72190C2ABE5EB5E"));
        r configuration = aVar.a();
        o.a(configuration);
        c.c.b.b.a.a0.a.a(getApplicationContext(), c.d.a.e.a.a(this.y, "admob_interstitial"), adRequest, new a());
    }

    public class a extends c.c.b.b.a.a0.b {
        public a() {
        }

        @Override // c.c.b.b.a.d
        /* renamed from: c, reason: merged with bridge method [inline-methods] */
        public void b(c.c.b.b.a.a0.a interstitialAd) {
            SplashScreen.this.x = interstitialAd;
            interstitialAd.b(new C0150a(this));
        }

        /* renamed from: com.rozgarone.SplashScreen$a$a, reason: collision with other inner class name */
        public class C0150a extends l {
            public C0150a(a this$1) {
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

    public final void N() {
        c.c.b.b.a.a0.a aVar = this.x;
        if (aVar != null) {
            aVar.d(this);
        }
    }

    @Override // b.m.b.e, androidx.activity.ComponentActivity, b.i.b.f, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(1024, 1024);
        this.y = this;
        this.z = new c.d.a.g.c(this);
        if (ConstClass.c(this.y)) {
            Q();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet connection", 1).show();
        }
    }

    public final void Q() {
        try {
            HashMap<String, String> map = new HashMap<>();
            this.z.a(ConstClass.f9436a + ConstClass.i, map, new b());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class b implements d {
        public b() {
        }

        @Override // c.d.a.g.d
        public void a(String result) {
            Log.e("dta", result);
            try {
                if (result.isEmpty()) {
                    ConstClass.a(SplashScreen.this.y, "response is null");
                    return;
                }
                Log.e("res", result);
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.getString("status").equalsIgnoreCase("true")) {
                    ConstClass.a(SplashScreen.this.y, jsonObject.getString("message"));
                } else {
                    JSONArray jsonArray = jsonObject.getJSONArray("appsettings");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        c.d.a.e.a.b(SplashScreen.this.y, "admob_banner", object.getString("admob_banner"));
                        c.d.a.e.a.b(SplashScreen.this.y, "admob_interstitial", object.getString("admob_interstitial"));
                        c.d.a.e.a.b(SplashScreen.this.y, "admob_native", object.getString("admob_native"));
                        c.d.a.e.a.b(SplashScreen.this.y, "admob_appopen", object.getString("admob_appopen"));
                    }
                    SplashScreen.this.M();
                    new Handler().postDelayed(new a(), 6000L);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public class a implements Runnable {
            public a() {
            }

            @Override // java.lang.Runnable
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this, (Class<?>) StartScreen.class));
                SplashScreen.this.N();
                SplashScreen.this.finish();
            }
        }

        @Override // c.d.a.g.d
        public void b(t error) {
            ConstClass.a(SplashScreen.this.y, "" + error);
        }
    }
}
