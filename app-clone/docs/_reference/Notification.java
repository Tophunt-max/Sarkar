package com.rozgarone;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import c.a.a.t;
import c.d.a.d.e;
import c.d.a.g.d;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.SarkariHelper.ConstClass;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Notification extends b.b.b.c {
    public ImageView A;
    public c.d.a.g.c B;
    public TextView C;
    public ShimmerFrameLayout D;
    public SwipeRefreshLayout E;
    public Context x;
    public RecyclerView y;
    public ArrayList<c.d.a.f.b> z = new ArrayList<>();

    @Override // b.m.b.e, androidx.activity.ComponentActivity, b.i.b.f, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getWindow().setFlags(1024, 1024);
        ImageView imageView = (ImageView) findViewById(R.id.backbutton);
        this.A = imageView;
        imageView.setOnClickListener(new a());
        this.x = this;
        this.B = new c.d.a.g.c(this);
        this.D = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.y = (RecyclerView) findViewById(R.id.recyc_ipo);
        this.C = (TextView) findViewById(R.id.txt_noData);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.E = swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeResources(R.color.txt_back);
        this.D = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.E.setOnRefreshListener(new b());
        if (ConstClass.c(this.x)) {
            if (this.z.size() == 0) {
                R();
                return;
            } else {
                S();
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
            Notification.this.onBackPressed();
        }
    }

    public class b implements SwipeRefreshLayout.i {
        public b() {
        }

        @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.i
        public void a() {
            Notification.this.R();
        }
    }

    public final void R() {
        this.z.clear();
        try {
            HashMap<String, String> map = new HashMap<>();
            this.B.a(ConstClass.f9436a + ConstClass.h, map, new c());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class c implements d {
        public c() {
        }

        @Override // c.d.a.g.d
        public void a(String result) {
            Log.e("dta", result);
            try {
                if (!result.isEmpty()) {
                    Notification.this.z.clear();
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getString("status").equalsIgnoreCase("true")) {
                        ConstClass.a(Notification.this.x, jsonObject.getString("ok"));
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("notification");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            c.d.a.f.b model = new c.d.a.f.b();
                            model.j(object.getString("maintitle"));
                            model.i(object.getString("secondtitle"));
                            model.k(object.getString("publishdate"));
                            Notification.this.z.add(model);
                        }
                        Notification.this.S();
                        Notification.this.D.d();
                        Notification.this.D.setVisibility(8);
                    }
                    return;
                }
                ConstClass.a(Notification.this.x, "response is null");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override // c.d.a.g.d
        public void b(t error) {
            ConstClass.a(Notification.this.x, "" + error);
        }
    }

    public final void S() {
        if (this.z.size() == 0) {
            this.C.setVisibility(0);
            return;
        }
        this.y.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this.x);
        this.y.setLayoutManager(manager);
        e adapter = new e(this.x, this.z);
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
