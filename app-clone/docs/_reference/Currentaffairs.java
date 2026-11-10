package com.rozgarone.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import c.a.a.t;
import c.c.b.b.a.f;
import c.c.b.b.a.g;
import c.c.b.b.a.i;
import c.c.b.b.a.l;
import c.c.b.b.a.m;
import c.c.b.b.a.o;
import c.c.b.b.a.r;
import c.d.a.d.a;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.app.SarkariHelper.ConstClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Currentaffairs extends b.b.b.c implements a.e {
    public ImageView A;
    public c.d.a.g.c B;
    public TextView C;
    public ShimmerFrameLayout D;
    public SwipeRefreshLayout E;
    public c.c.b.b.a.a0.a F;
    public i G;
    public Context x;
    public RecyclerView y;
    public ArrayList<c.d.a.f.b> z = new ArrayList<>();

    public void N() {
        f adRequest = new f.a().c();
        r.a aVar = new r.a();
        aVar.b(Arrays.asList("1ADAD30F02CD84CDE72190C2ABE5EB5E"));
        r configuration = aVar.a();
        o.a(configuration);
        c.c.b.b.a.a0.a.a(getApplicationContext(), c.d.a.e.a.a(this.x, "admob_interstitial"), adRequest, new a());
    }

    public class a extends c.c.b.b.a.a0.b {
        public a() {
        }

        @Override // c.c.b.b.a.d
        /* renamed from: c, reason: merged with bridge method [inline-methods] */
        public void b(c.c.b.b.a.a0.a interstitialAd) {
            Currentaffairs.this.F = interstitialAd;
            interstitialAd.b(new C0146a(this));
        }

        /* renamed from: com.rozgarone.app.Currentaffairs$a$a, reason: collision with other inner class name */
        public class C0146a extends l {
            public C0146a(a this$1) {
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

    public final void O() {
        c.c.b.b.a.a0.a aVar = this.F;
        if (aVar != null) {
            aVar.d(this);
        }
    }

    public void M() {
        this.G = new i(this);
        RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.adMobView);
        this.G.setAdUnitId(c.d.a.e.a.a(getApplicationContext(), "admob_banner"));
        adContainer.addView(this.G);
        W();
    }

    public final void W() {
        f adRequest = new f.a().c();
        g adSize = U();
        this.G.setAdSize(adSize);
        this.G.b(adRequest);
    }

    public final g U() {
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
        setContentView(R.layout.activity_currentaffairs);
        getWindow().setFlags(1024, 1024);
        ImageView imageView = (ImageView) findViewById(R.id.backbutton);
        this.A = imageView;
        imageView.setOnClickListener(new b());
        this.x = this;
        this.B = new c.d.a.g.c(this);
        N();
        M();
        this.D = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.y = (RecyclerView) findViewById(R.id.recyc_ipo);
        this.C = (TextView) findViewById(R.id.txt_noData);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.E = swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeResources(R.color.txt_back);
        this.D = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.E.setOnRefreshListener(new c());
        if (ConstClass.c(this.x)) {
            if (this.z.size() == 0) {
                V();
                return;
            } else {
                X();
                return;
            }
        }
        ConstClass.a(this.x, "no network available");
    }

    public class b implements View.OnClickListener {
        public b() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Currentaffairs.this.onBackPressed();
        }
    }

    public class c implements SwipeRefreshLayout.i {
        public c() {
        }

        @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.i
        public void a() {
            Currentaffairs.this.V();
        }
    }

    public final void V() {
        this.z.clear();
        try {
            HashMap<String, String> map = new HashMap<>();
            this.B.a(ConstClass.f9436a + ConstClass.f9438c, map, new d());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class d implements c.d.a.g.d {
        public d() {
        }

        @Override // c.d.a.g.d
        public void a(String result) {
            try {
                if (!result.isEmpty()) {
                    Currentaffairs.this.z.clear();
                    Log.e("res", result);
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getString("status").equalsIgnoreCase("true")) {
                        ConstClass.a(Currentaffairs.this.x, jsonObject.getString("message"));
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("currentaffairs");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            c.d.a.f.b model = new c.d.a.f.b();
                            model.j(object.getString("jobTitle"));
                            model.i(object.getString("jobContent"));
                            model.l(object.getString("postid"));
                            model.k(object.getString("postPublishDate"));
                            model.h(object.getString("formattedDate"));
                            Currentaffairs.this.z.add(model);
                        }
                        Currentaffairs.this.X();
                        Currentaffairs.this.D.d();
                        Currentaffairs.this.D.setVisibility(8);
                    }
                    return;
                }
                ConstClass.a(Currentaffairs.this.x, "response is null");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override // c.d.a.g.d
        public void b(t error) {
            ConstClass.a(Currentaffairs.this.x, "" + error);
        }
    }

    public void i(String title, String api, int pos) {
        Intent i = new Intent(this, (Class<?>) CurrentAffairsdetail.class);
        i.putExtra("slugid", this.z.get(pos).f());
        i.putExtra("content", this.z.get(pos).c());
        i.putExtra("title", this.z.get(pos).d());
        i.putExtra("publishdate", this.z.get(pos).e());
        i.setFlags(268435456);
        startActivity(i);
        O();
    }

    public final void X() {
        if (this.z.size() == 0) {
            this.C.setVisibility(0);
            return;
        }
        this.y.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this.x);
        this.y.setLayoutManager(manager);
        c.d.a.d.a adapter = new c.d.a.d.a(this.x, this.z, this);
        this.y.setAdapter(adapter);
        adapter.h();
        this.E.setRefreshing(false);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // b.m.b.e, android.app.Activity
    public void onResume() {
        super.onResume();
        this.D.c();
    }

    @Override // b.m.b.e, android.app.Activity
    public void onPause() {
        this.D.d();
        super.onPause();
    }
}
