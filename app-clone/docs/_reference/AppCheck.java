package com.rozgarone.AppInit;

import android.app.Application;
import android.content.Context;
import c.c.d.c0.e;
import c.c.d.h;
import c.c.d.q.c;
import com.firebase.client.Firebase;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AppCheck extends Application {
    static {
        AppCheck.class.getSimpleName();
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
        h.o(this);
        e.a();
        if (!((ArrayList) h.i()).isEmpty()) {
            c.b().e(true);
        }
        new AppOpenManager(this);
    }

    @Override // android.content.ContextWrapper
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
