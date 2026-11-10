package com.rozgarone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import c.a.a.t;
import c.c.b.b.a.f;
import c.c.b.b.a.l;
import c.c.b.b.a.m;
import c.c.b.b.a.o;
import c.c.b.b.a.r;
import c.d.a.d.d;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.SarkariHelper.ConstClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ExpiredGovjob extends b.b.b.c implements d.e {
    public ImageView A;
    public c.d.a.g.c B;
    public TextView C;
    public TextView D;
    public ShimmerFrameLayout E;
    public SwipeRefreshLayout F;
    public TextView G;
    public c.c.b.b.a.a0.a H;
    public Context x;
    public RecyclerView y;
    public ArrayList<c.d.a.f.b> z = new ArrayList<>();

    public void M() {
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
            ExpiredGovjob.this.H = interstitialAd;
            interstitialAd.b(new C0149a(this));
        }

        /* renamed from: com.rozgarone.ExpiredGovjob$a$a, reason: collision with other inner class name */
        public class C0149a extends l {
            public C0149a(a this$1) {
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
        c.c.b.b.a.a0.a aVar = this.H;
        if (aVar != null) {
            aVar.d(this);
        }
    }

    @Override // b.m.b.e, androidx.activity.ComponentActivity, b.i.b.f, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_govjob);
        getWindow().setFlags(1024, 1024);
        this.x = this;
        TextView textView = (TextView) findViewById(R.id.viewexpired);
        this.G = textView;
        textView.setText("View Active");
        M();
        this.A = (ImageView) findViewById(R.id.backbutton);
        this.D = (TextView) findViewById(R.id.titlebar);
        this.A.setOnClickListener(new b());
        this.D.setText("Expired Job List");
        this.B = new c.d.a.g.c(this.x);
        this.G.setOnClickListener(new c());
        this.E = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.y = (RecyclerView) findViewById(R.id.recyc_ipo);
        this.C = (TextView) findViewById(R.id.txt_noData);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.F = swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeResources(R.color.txt_back);
        this.E = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.F.setOnRefreshListener(new d());
        if (ConstClass.c(this.x)) {
            if (this.z.size() == 0) {
                T();
                return;
            } else {
                U();
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
            ExpiredGovjob.this.onBackPressed();
        }
    }

    public class c implements View.OnClickListener {
        public c() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ExpiredGovjob.this.startActivity(new Intent(ExpiredGovjob.this, (Class<?>) AllGovjob.class));
        }
    }

    public class d implements SwipeRefreshLayout.i {
        public d() {
        }

        @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.i
        public void a() {
            ExpiredGovjob.this.T();
        }
    }

    public final void T() {
        this.z.clear();
        try {
            HashMap<String, String> map = new HashMap<>();
            this.B.a(ConstClass.f9436a + ConstClass.j, map, new e());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public class e implements c.d.a.g.d {
        public e() {
        }

        @Override // c.d.a.g.d
        public void a(String result) {
            Log.e("dta", result);
            try {
                if (!result.isEmpty()) {
                    ExpiredGovjob.this.z.clear();
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getString("status").equalsIgnoreCase("true")) {
                        ConstClass.a(ExpiredGovjob.this.x, jsonObject.getString("message"));
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
                            ExpiredGovjob.this.z.add(model);
                        }
                        ExpiredGovjob.this.U();
                        ExpiredGovjob.this.E.d();
                        ExpiredGovjob.this.E.setVisibility(8);
                    }
                    return;
                }
                ConstClass.a(ExpiredGovjob.this.x, "response is null");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override // c.d.a.g.d
        public void b(t error) {
            ConstClass.a(ExpiredGovjob.this.x, "" + error);
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
        N();
    }

    public final void U() {
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
