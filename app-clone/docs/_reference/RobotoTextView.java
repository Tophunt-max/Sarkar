package com.rozgarone.app.SarkariHelper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import b.b.g.e0;
import b.i.c.d.h;
import com.rozgarone.app.R;

/* loaded from: classes.dex */
public class RobotoTextView extends e0 {
    public Context q;

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.q = context;
        p();
    }

    public final void p() {
        Typeface regularFont = h.f(this.q, R.font.poppins_regular);
        Typeface boldFont = h.f(this.q, R.font.poppins_semibold);
        Typeface currentTypeFace = getTypeface();
        if (currentTypeFace != null && currentTypeFace.getStyle() == 1) {
            setTypeface(boldFont);
        } else {
            setTypeface(regularFont);
        }
    }

    @Override // b.b.g.e0, android.widget.TextView
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
    }

    @Override // android.widget.TextView
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }
}
