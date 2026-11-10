package com.rozgarone.push;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rozgarone.R;
import com.rozgarone.net.Repo;
import com.rozgarone.ui.ListActivity;
import com.rozgarone.ui.MainActivity;

import java.util.Map;
import java.util.Random;

/**
 * Handles data messages with the same payload as the original app:
 * {@code {title, message, image, action_destination, notificationid}}. The destination
 * ({@code govtjob | currentaffairs | dailynews | examresultpost}) picks the screen that the
 * notification opens; anything else opens the home screen.
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";
    private static final String CHANNEL = "notification";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Push.subscribe();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String body = data.get("message");
        if (title == null && message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }
        if (title == null) return;
        show(this, title, body == null ? "" : body, intentFor(this, data.get("action_destination")));
    }

    static Intent intentFor(Context ctx, String destination) {
        String feed;
        int titleRes;
        if ("govtjob".equals(destination)) {
            feed = Repo.GOVT_JOB;
            titleRes = R.string.title_govtjob;
        } else if ("currentaffairs".equals(destination)) {
            feed = Repo.CURRENT_AFFAIRS;
            titleRes = R.string.title_currentaffairs;
        } else if ("dailynews".equals(destination)) {
            feed = Repo.DAILY_NEWS;
            titleRes = R.string.title_dailynews;
        } else if ("examresultpost".equals(destination)) {
            feed = Repo.RESULT_POST;
            titleRes = R.string.title_exam_results;
        } else {
            return new Intent(ctx, MainActivity.class);
        }
        return new Intent(ctx, ListActivity.class)
                .putExtra(ListActivity.EXTRA_FEED, feed)
                .putExtra(ListActivity.EXTRA_TITLE, ctx.getString(titleRes));
    }

    private static void show(Context ctx, String title, String body, Intent target) {
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && nm.getNotificationChannel(CHANNEL) == null) {
            NotificationChannel ch = new NotificationChannel(CHANNEL,
                    ctx.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(ch);
        }
        target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = TaskStackBuilder.create(ctx)
                .addNextIntentWithParentStack(target)
                .getPendingIntent(new Random().nextInt(9000) + 100,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi);

        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "POST_NOTIFICATIONS not granted; dropping notification");
            return;
        }
        NotificationManagerCompat.from(ctx).notify(new Random().nextInt(9000) + 100, b.build());
    }
}
