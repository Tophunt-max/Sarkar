package com.gsbussiness.sarkariexamupdates.AppInit;

import b.p.f;
import b.p.g;
import b.p.k;
import b.p.p;

/* loaded from: classes.dex */
public class AppOpenManager_LifecycleAdapter implements f {

    /* renamed from: a, reason: collision with root package name */
    public final AppOpenManager f9409a;

    public AppOpenManager_LifecycleAdapter(AppOpenManager receiver) {
        this.f9409a = receiver;
    }

    @Override // b.p.f
    public void a(k owner, g.a event, boolean onAny, p logger) {
        boolean hasLogger = logger != null;
        if (!onAny && event == g.a.ON_START) {
            if (!hasLogger || logger.a("onStart", 1)) {
                this.f9409a.onStart();
            }
        }
    }
}
