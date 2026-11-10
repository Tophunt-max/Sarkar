package com.gsbussiness.sarkariexamupdates;

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
import c.d.a.d.d;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.gsbussiness.sarkariexamupdates.SarkariHelper.ConstClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CategoryContent extends b.b.b.c implements d.e {
    public ImageView A;
    public c.d.a.g.c B;
    public TextView C;
    public TextView D;
    public ShimmerFrameLayout E;
    public SwipeRefreshLayout F;
    public Intent G;
    public String H;
    public String I;
    public String J;
    public c.c.b.b.a.a0.a K;
    public i L;
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
            CategoryContent.this.K = interstitialAd;
            interstitialAd.b(new C0145a(this));
        }

        /* renamed from: com.gsbussiness.sarkariexamupdates.CategoryContent$a$a, reason: collision with other inner class name */
        public class C0145a extends l {
            public C0145a(a this$1) {
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
        c.c.b.b.a.a0.a aVar = this.K;
        if (aVar != null) {
            aVar.d(this);
        }
    }

    public void M() {
        this.L = new i(this);
        RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.adMobView);
        this.L.setAdUnitId(c.d.a.e.a.a(getApplicationContext(), "admob_banner"));
        adContainer.addView(this.L);
        W();
    }

    public final void W() {
        f adRequest = new f.a().c();
        g adSize = U();
        this.L.setAdSize(adSize);
        this.L.b(adRequest);
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
        setContentView(R.layout.activity_category_content);
        getWindow().setFlags(1024, 1024);
        this.G = getIntent();
        this.x = this;
        M();
        N();
        this.H = this.G.getStringExtra("name");
        this.I = this.G.getStringExtra("id");
        this.J = this.G.getStringExtra("filter");
        this.A = (ImageView) findViewById(R.id.backbutton);
        TextView textView = (TextView) findViewById(R.id.titlebar);
        this.D = textView;
        textView.setText(this.H + " Job List");
        this.A.setOnClickListener(new b());
        this.B = new c.d.a.g.c(this.x);
        this.E = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.y = (RecyclerView) findViewById(R.id.recyc_ipo);
        this.C = (TextView) findViewById(R.id.txt_noData);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.F = swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeResources(R.color.txt_back);
        this.E = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.F.setOnRefreshListener(new c());
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
            CategoryContent.this.onBackPressed();
        }
    }

    public class c implements SwipeRefreshLayout.i {
        public c() {
        }

        @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.i
        public void a() {
            CategoryContent.this.V();
        }
    }

    public final void V() {
        this.z.clear();
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("labelid", this.I);
            map.put("filter", this.J);
            this.B.a(ConstClass.f9436a + ConstClass.g, map, new d());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class d implements c.d.a.g.d {
        public d() {
        }

        @Override // c.d.a.g.d
        public void a(String result) {
            Log.e("dta", result);
            try {
                if (!result.isEmpty()) {
                    CategoryContent.this.z.clear();
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getString("status").equalsIgnoreCase("true")) {
                        ConstClass.a(CategoryContent.this.x, jsonObject.getString("message"));
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("govtjob");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            c.d.a.f.b model = new c.d.a.f.b();
                            model.j(object.getString("jobTitle"));
                            model.i(object.getString("jobContent"));
                            model.l(object.getString("postid"));
                            model.k(object.getString("postPublishDate"));
                            model.h(object.getString("formattedDate"));
                            model.g(object.getString("expiredDate"));
                            CategoryContent.this.z.add(model);
                        }
                        CategoryContent.this.X();
                        CategoryContent.this.E.d();
                        CategoryContent.this.E.setVisibility(8);
                    }
                    return;
                }
                ConstClass.a(CategoryContent.this.x, "response is null");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override // c.d.a.g.d
        public void b(t error) {
            ConstClass.a(CategoryContent.this.x, "" + error);
        }
    }

    @Override // c.d.a.d.d.e
    public void i(String title, String api, int pos) {
        Intent i = new Intent(this.x, (Class<?>) AllgovjobContent.class);
        i.putExtra("slugid", this.z.get(pos).f());
        i.putExtra("content", this.z.get(pos).c());
        i.putExtra("title", this.z.get(pos).d());
        i.putExtra("publishdate", this.z.get(pos).e());
        i.putExtra("expiredate", this.z.get(pos).a());
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
        c.d.a.d.d adapter = new c.d.a.d.d(this.x, this.z, this);
        this.y.setAdapter(adapter);
        adapter.h();
        this.F.setRefreshing(false);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // b.m.b.e, android.app.Activity
    public void onResume() {
        super.onResume();
        this.E.c();
    }

    @Override // b.m.b.e, android.app.Activity
    public void onPause() {
        this.E.d();
        super.onPause();
    }
}
