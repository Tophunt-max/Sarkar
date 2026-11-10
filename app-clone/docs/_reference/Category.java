package com.rozgarone.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import c.a.a.t;
import c.d.a.d.f;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.app.SarkariHelper.ConstClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Category extends b.b.b.c {
    public ImageView A;
    public c.d.a.g.c B;
    public TextView C;
    public TextView D;
    public ShimmerFrameLayout E;
    public SwipeRefreshLayout F;
    public Intent G;
    public String H;
    public SearchView I;
    public f J;
    public Context x;
    public RecyclerView y;
    public ArrayList<c.d.a.f.a> z = new ArrayList<>();

    @Override // b.m.b.e, androidx.activity.ComponentActivity, b.i.b.f, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().setFlags(1024, 1024);
        this.D = (TextView) findViewById(R.id.titlebar);
        this.A = (ImageView) findViewById(R.id.backbutton);
        this.I = (SearchView) findViewById(R.id.searchview);
        this.A.setOnClickListener(new a());
        Intent intent = getIntent();
        this.G = intent;
        this.H = intent.getStringExtra("title");
        this.D.setText(this.H + " List");
        this.x = this;
        this.B = new c.d.a.g.c(this);
        this.E = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.y = (RecyclerView) findViewById(R.id.recyc_ipo);
        this.C = (TextView) findViewById(R.id.txt_noData);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.F = swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeResources(R.color.txt_back);
        this.E = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.F.setOnRefreshListener(new b());
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

    public class a implements View.OnClickListener {
        public a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Category.this.onBackPressed();
        }
    }

    public class b implements SwipeRefreshLayout.i {
        public b() {
        }

        @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.i
        public void a() {
            Category.this.T();
        }
    }

    public final void T() {
        this.z.clear();
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("filter", this.H);
            this.B.a(ConstClass.f9436a + ConstClass.f, map, new c());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class c implements c.d.a.g.d {
        public c() {
        }

        @Override // c.d.a.g.d
        public void a(String result) {
            Log.e("dta", result);
            try {
                if (!result.isEmpty()) {
                    Category.this.z.clear();
                    Log.e("res", result);
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getString("status").equalsIgnoreCase("true")) {
                        ConstClass.a(Category.this.x, jsonObject.getString("message"));
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("filter");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            c.d.a.f.a model = new c.d.a.f.a();
                            model.c(object.getString("category_name"));
                            model.d(object.getString("id"));
                            Category.this.z.add(model);
                        }
                        Category.this.U();
                        Category.this.E.d();
                        Category.this.E.setVisibility(8);
                    }
                    return;
                }
                ConstClass.a(Category.this.x, "response is null");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override // c.d.a.g.d
        public void b(t error) {
            ConstClass.a(Category.this.x, "" + error);
        }
    }

    public final void U() {
        if (this.z.size() == 0) {
            this.C.setVisibility(0);
            return;
        }
        this.y.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this.x);
        this.y.setLayoutManager(manager);
        f fVar = new f(this.x, this.z, this.H);
        this.J = fVar;
        this.y.setAdapter(fVar);
        this.J.h();
        this.F.setRefreshing(false);
        this.I.setOnQueryTextListener(new d());
    }

    public class d implements SearchView.l {
        public d() {
        }

        public boolean b(String query) {
            return false;
        }

        public boolean a(String newText) {
            Category.this.S(newText);
            return false;
        }
    }

    public final void S(String text) {
        ArrayList<c.d.a.f.a> arrayList = new ArrayList<>();
        if (!text.trim().isEmpty()) {
            Iterator<c.d.a.f.a> it = this.z.iterator();
            while (it.hasNext()) {
                c.d.a.f.a item = it.next();
                if (item.a().toLowerCase().contains(text.toLowerCase())) {
                    arrayList.add(item);
                }
            }
            if (arrayList.isEmpty()) {
                Toast.makeText(this, "No Data Found..", 0).show();
                return;
            } else {
                this.J.u(arrayList);
                return;
            }
        }
        f fVar = this.J;
        if (fVar != null) {
            fVar.u(this.z);
        }
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
