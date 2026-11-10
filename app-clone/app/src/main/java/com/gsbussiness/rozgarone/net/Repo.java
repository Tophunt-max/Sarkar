package com.rozgarone.app.net;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rozgarone.app.App;
import com.rozgarone.app.models.FilterItem;
import com.rozgarone.app.models.Notice;
import com.rozgarone.app.models.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Fetches and parses every backend feed. */
public class Repo {

    public interface PostsCallback {
        void onSuccess(List<Post> posts);

        void onError(String message);
    }

    public interface FiltersCallback {
        void onSuccess(List<FilterItem> items);

        void onError(String message);
    }

    public interface NoticesCallback {
        void onSuccess(List<Notice> items);

        void onError(String message);
    }

    public interface SettingsCallback {
        void onSuccess(AppSettings settings);

        void onError(String message);
    }

    // Feed keys used by the public endpoints.
    public static final String GOVT_JOB = "govtjob";
    public static final String CURRENT_AFFAIRS = "currentaffairs";
    public static final String RESULT_POST = "resultpost";
    public static final String DAILY_NEWS = "dailynews";

    private static final String TAG = "repo";

    private final Api api;

    public Repo(@NonNull Context context) {
        this.api = ((App) context.getApplicationContext()).api();
    }

    // ------------------------------------------------------------- endpoints

    public void settings(SettingsCallback cb) {
        get("api/appsettings.php", root -> {
            AppSettings s = new AppSettings();
            JSONArray arr = root.optJSONArray("appsettings");
            if (arr != null && arr.length() > 0) {
                JSONObject o = arr.optJSONObject(0);
                if (o != null) {
                    s.admob_banner = o.optString("admob_banner", "");
                    s.admob_interstitial = o.optString("admob_interstitial", "");
                    s.admob_native = o.optString("admob_native", "");
                    s.admob_appopen = o.optString("admob_appopen", "");
                }
            }
            cb.onSuccess(s);
        }, cb::onError);
    }

    /** Ordinary feed: govtjob | currentaffairs | resultpost | dailynews. */
    public void feed(String feedKey, PostsCallback cb) {
        final String endpoint = endpointFor(feedKey);
        get(endpoint, root -> cb.onSuccess(parsePosts(root, feedKey)), cb::onError);
    }

    public void expired(PostsCallback cb) {
        get("api/getexpiredpost.php", root -> cb.onSuccess(parsePosts(root, GOVT_JOB)), cb::onError);
    }

    public void filters(String group, FiltersCallback cb) {
        get("api/getfilter.php?filter=" + group, root -> {
            List<FilterItem> out = new ArrayList<>();
            JSONArray arr = root.optJSONArray("filter");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.optJSONObject(i);
                    if (o == null) continue;
                    out.add(new FilterItem(o.optString("id", ""), o.optString("category_name", "")));
                }
            }
            cb.onSuccess(out);
        }, cb::onError);
    }

    public void postsByFilter(String group, String labelId, PostsCallback cb) {
        String endpoint = "api/getpostbyfilter.php?filter=" + group + "&labelid=" + labelId;
        get(endpoint, root -> cb.onSuccess(parsePosts(root, GOVT_JOB)), cb::onError);
    }

    public void notices(NoticesCallback cb) {
        get("api/getnotification.php", root -> {
            List<Notice> out = new ArrayList<>();
            JSONArray arr = root.optJSONArray("notification");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.optJSONObject(i);
                    if (o == null) continue;
                    Notice n = new Notice();
                    n.maintitle = o.optString("maintitle", "");
                    n.secondtitle = o.optString("secondtitle", "");
                    n.publishdate = o.optString("publishdate", "");
                    out.add(n);
                }
            }
            cb.onSuccess(out);
        }, cb::onError);
    }

    // ---------------------------------------------------------------- helpers

    public static String endpointFor(String feedKey) {
        switch (feedKey == null ? "" : feedKey) {
            case CURRENT_AFFAIRS:
                return "api/getcurrentaffairs.php";
            case RESULT_POST:
                return "api/getresultpost.php";
            case DAILY_NEWS:
                return "api/getdailynews.php";
            case GOVT_JOB:
            default:
                return "api/getgovtjob.php";
        }
    }

    private static List<Post> parsePosts(JSONObject root, String feedKey) {
        List<Post> out = new ArrayList<>();
        JSONArray arr = root.optJSONArray(feedKey);
        if (arr == null) {
            // tolerate any single feed array name
            arr = root.optJSONArray(GOVT_JOB);
        }
        if (arr == null) return out;

        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;
            Post p = new Post();
            p.feed = feedKey;
            p.postid = o.optString("postid", "");
            p.jobTitle = o.optString("jobTitle", o.optString("title", ""));
            p.jobContent = o.optString("jobContent", o.optString("content", ""));
            p.postPublishDate = o.optString("postPublishDate", o.optString("publishdate", ""));
            p.formattedDate = o.optString("formattedDate", "");
            p.expiredDate = o.optString("expiredDate", o.optString("expiredate", ""));
            p.isExpired = o.optInt("isExpired", 0) == 1 || "1".equals(o.optString("isExpired"));
            out.add(p);
        }
        return out;
    }

    // ------------------------------------------------------------ json plumbing

    private interface Parser {
        void parse(JSONObject root);
    }

    private interface Fail {
        void fail(String message);
    }

    private void get(String endpoint, Parser ok, Fail fail) {
        String url = api.url(endpoint);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String status = response.optString("status", "true");
                    if ("false".equalsIgnoreCase(status)) {
                        fail.fail(response.optString("message", "Request failed"));
                        return;
                    }
                    ok.parse(response);
                },
                error -> fail.fail(messageFor(error)));
        req.setTag(TAG);
        api.add(req);
    }

    private static String messageFor(com.android.volley.VolleyError error) {
        if (error.networkResponse == null) {
            return "No Internet connection";
        }
        return "Server error (" + error.networkResponse.statusCode + ")";
    }
}
