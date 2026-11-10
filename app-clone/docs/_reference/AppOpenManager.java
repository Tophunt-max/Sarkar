package com.rozgarone.app.AppInit;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import b.p.g;
import b.p.j;
import b.p.s;
import b.p.t;
import c.c.b.b.a.f;
import c.c.b.b.a.l;
import c.c.b.b.a.m;
import c.c.b.b.a.w.a;

/* loaded from: classes.dex */
public class AppOpenManager implements j, Application.ActivityLifecycleCallbacks {
    public static boolean o = false;
    public c.c.b.b.a.w.a k = null;
    public a.AbstractC0081a l;
    public final AppCheck m;
    public Activity n;

    public void m() {
        if (!o && l()) {
            Log.d("AppOpenManager", "Will show ad.");
            l fullScreenContentCallback = new a();
            this.k.b(fullScreenContentCallback);
            this.k.c(this.n);
            return;
        }
        Log.d("AppOpenManager", "Can not show ad.");
        j();
    }

    public class a extends l {
        public a() {
        }

        @Override // c.c.b.b.a.l
        public void b() {
            AppOpenManager.this.k = null;
            boolean unused = AppOpenManager.o = false;
            AppOpenManager.this.j();
        }

        @Override // c.c.b.b.a.l
        public void c(c.c.b.b.a.a adError) {
        }

        @Override // c.c.b.b.a.l
        public void e() {
            boolean unused = AppOpenManager.o = true;
        }
    }

    public AppOpenManager(AppCheck myApplication) {
        this.m = myApplication;
        myApplication.registerActivityLifecycleCallbacks(this);
        ((t) t.k()).a().a(this);
    }

    public void j() {
        if (l()) {
            return;
        }
        this.l = new b();
        f request = k();
        c.c.b.b.a.w.a.a(this.m, c.d.a.e.a.a(this.n, "admob_appopen"), request, 1, this.l);
    }

    public class b extends a.AbstractC0081a {
        public b() {
        }

        @Override // c.c.b.b.a.d
        /* renamed from: c, reason: merged with bridge method [inline-methods] */
        public void b(c.c.b.b.a.w.a ad) {
            AppOpenManager.this.k = ad;
        }

        @Override // c.c.b.b.a.d
        public void a(m loadAdError) {
        }
    }

    public final f k() {
        return new f.a().c();
    }

    public boolean l() {
        return this.k != null;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        this.n = activity;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        this.n = activity;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        this.n = null;
    }

    @s(g.a.ON_START)
    public void onStart() {
        m();
        Log.d("AppOpenManager", "onStart");
    }
}
