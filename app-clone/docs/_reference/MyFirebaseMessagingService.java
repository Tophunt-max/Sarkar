package com.gsbussiness.sarkariexamupdates.AppInit;

import android.content.Intent;
import android.util.Log;
import c.c.d.a0.t0;
import c.d.a.c.a;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.gsbussiness.sarkariexamupdates.AllGovjob;
import com.gsbussiness.sarkariexamupdates.Currentaffairs;
import com.gsbussiness.sarkariexamupdates.Dailynews;
import com.gsbussiness.sarkariexamupdates.Examresults;
import com.gsbussiness.sarkariexamupdates.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void s(String s) {
        super.s(s);
        FirebaseMessaging.f().E("sarkariexamalerts");
        v();
    }

    public final void v() {
    }

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void q(t0 remoteMessage) {
        if (remoteMessage.s().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.s());
                w(json);
            } catch (Exception e) {
                Log.v(":::MyFirebaseMsgService", "Exception1: " + e.getMessage());
            }
        }
    }

    public final void w(JSONObject json) {
        Intent intent;
        try {
            String title = json.getString("title");
            String message = json.getString("message");
            json.getString("image");
            String action_destination = json.getString("action_destination");
            String notificationid = json.getString("notificationid");
            Log.d(":::MyFirebaseMsgService", "sendPushNotification:" + json.toString());
            a mNotificationManager = new a(getApplicationContext());
            if (action_destination.equals("currentaffairs")) {
                intent = new Intent(getApplicationContext(), (Class<?>) Currentaffairs.class);
                intent.putExtra("notificationid", notificationid);
            } else if (action_destination.equals("govtjob")) {
                intent = new Intent(getApplicationContext(), (Class<?>) AllGovjob.class);
                intent.putExtra("notificationid", notificationid);
            } else if (action_destination.equals("dailynews")) {
                intent = new Intent(getApplicationContext(), (Class<?>) Dailynews.class);
                intent.putExtra("notificationid", notificationid);
            } else if (action_destination.equals("examresultpost")) {
                intent = new Intent(getApplicationContext(), (Class<?>) Examresults.class);
            } else {
                intent = new Intent(getApplicationContext(), (Class<?>) MainActivity.class);
            }
            mNotificationManager.c(title, message, intent, getApplicationContext());
        } catch (JSONException e) {
            Log.v(":::MyFirebaseMsgService", "Json Exception1: " + e.getMessage());
        } catch (Exception e2) {
            Log.v(":::MyFirebaseMsgService", "Exception11: " + e2.getMessage());
        }
    }
}
