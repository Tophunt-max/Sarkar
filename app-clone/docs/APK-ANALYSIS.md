# REA Analysis — Sarkari Exam Updates (APK reverse engineering)

Source APK: `sarkariexamadmin1-8-22.apk` (6,793,464 bytes, single `classes.dex`,
compileSdk 32, `android:debuggable="true"`, `usesCleartextTraffic="true"`).
Repo: https://github.com/Tophunt-max/Sarkar.git

## 1. Identity

| Field | Value |
|---|---|
| Package | `com.gsbussiness.sarkariexamupdates` |
| App name | `Gov. Job Alerts Portal` (`@string/app_name`) |
| Application class | `com.gsbussiness.sarkariexamupdates.AppInit.AppCheck` |
| Launcher | `SplashScreen` |
| Theme | `@style/Theme.SarkariExamUpdates`, all activities `portrait`, `noactionbarshow` |
| Vendors in dex | Firebase (messaging, crashlytics, remote-config/ABT), AdMob (banner, interstitial, native, app-open), Volley, Shimmer, Room, Glide-ish loader (`c.d.a.e.a`) |

Permissions: INTERNET, ACCESS_NETWORK_STATE, WAKE_LOCK, RECEIVE_BOOT_COMPLETED,
FOREGROUND_SERVICE, `com.google.android.gms.permission.AD_ID`,
`com.google.android.c2dm.permission.RECEIVE` (FCM), SET_ALARM, legacy external storage.

## 2. Native-protected endpoint table

All API paths live in `libhello-jni.so` (4 ABIs) and are exposed via `native` methods
on `SarkariHelper.ConstClass`; `System.loadLibrary("hello-jni")` in the static initializer
then assigns them to fields `f9436a … j`. Extracted with `strings` (plaintext in `.rodata`):

| Field | JNI symbol | Value |
|---|---|---|
| `f9436a` | `baseurl` | `https://gtupro.xyz/sarkarialertsdemo/` |
| `f9437b` | `getgovtjob` | `api/getgovtjob.php` |
| `f9438c` | `getcurrentaffairs` | `api/getcurrentaffairs.php` |
| `f9439d` | `getresultpost` | `api/getresultpost.php` |
| `e` | `getdailynews` | `api/getdailynews.php` |
| `f` | `categoryurl` | `api/getfilter.php` |
| `g` | `getpostbyfilter` | `api/getpostbyfilter.php` |
| `h` | `getnotification` | `api/getnotification.php` |
| `i` | `appsettings` | `api/appsettings.php` |
| `j` | `getexpiredpost` | `api/getexpiredpost.php` |

Full URLs = `baseurl + endpoint`. (Demo host returns 404 today, i.e. backend is gone;
the contract below is reconstructed from the client parsers.)

## 3. Reconstructed API contract

Every response is `{ "status": "true"|"false", "message": "..." , <payload key>: [ ... ] }`.

**Transport:** every call goes through `c.d.a.g.c.a(url, HashMap params, listener)`,
which queues `c.d.a.g.c.a` — a Volley `StringRequest` constructed with method `1`
(`Request.Method.POST`) whose `getParams()` (`q()`) returns the caller's map. So all
endpoints are **form-encoded POSTs**; the map is empty except for the two filter calls.
The response string is then parsed with `new JSONObject(result)` and `getString`.

### `POST api/appsettings.php` → key `appsettings[]`
Items: `admob_banner`, `admob_interstitial`, `admob_native`, `admob_appopen`.
Client caches each into SharedPreferences via `c.d.a.e.a.b(ctx, key, value)` and only
then boots the interstitial + 6 s delayed navigation to `StartScreen`.

### `POST api/getgovtjob.php` → key `govtjob[]`
### `POST api/getcurrentaffairs.php` → key `currentaffairs[]`
### `POST api/getresultpost.php` → key `resultpost[]`
### `POST api/getdailynews.php` → key `dailynews[]`
### `POST api/getexpiredpost.php` → key `govtjob[]`
Each item: `jobTitle`, `jobContent` (HTML), `postid`, `postPublishDate`,
`formattedDate`, and (`govtjob`/expired only) `expiredDate`.

### `POST api/getfilter.php` (field `filter`) → key `filter[]`
Item: `category_name`, `id`. `Category.T()` puts `filter = <title extra>` (`Category` /
`Profession` / `Qualification` / `Location`); the screen title is `"<title> List"`.

### `POST api/getpostbyfilter.php` (fields `labelid`, `filter`) → key `govtjob[]`
`labelid` = `filter[].id`, `filter` = the group title. `CategoryContent` receives the
extras `name`, `id`, `filter` and titles itself `"<name> Job List"`.

### `POST api/getnotification.php` → key `notification[]`
Item: `maintitle`, `secondtitle`, `publishdate`.

## 4. Screens (from `res/layout`)

1. **SplashScreen** — full-bleed logo, fetches appsettings, interstitial, 6000 ms → StartScreen.
2. **StartScreen** — branding, native ad block, "Find jobs start here", "Share App", `app_name` bar, "Privacy policy" link.
3. **MainActivity** — dark-blue (`#00175f`) action bar with Ashoka-chakra logo + notification bell;
   grid: Find All Gov. Jobs, Current Affairs / Daily News Alerts, Exam Results (2×2 cards with
   coloured rounded icon chips #f6a533, #e79d35, #d59135, #bd8434), native ad, then stacked
   rows: Find Job Category / Profession / Qualification / Location wise (#33457f, #354d9b, #3554b7, #3459d3).
4. **AllGovjob / Currentaffairs / Dailynews / Examresults / ExpiredGovjob** — list screens:
   action bar, Shimmer placeholder list, RecyclerView of `job_list_item` cards
   (heading 15sp poppins-medium, 10sp light timestamp, 3-line description, chevron),
   SwipeRefreshLayout (`@color/txt_back`), empty state `txt_noData`, bottom banner ad.
   Surevy: "View expired posts" link on AllGovjob → ExpiredGovjob.
5. **Content screens** (`AllgovjobContent`, `ExamresultsContent`, `DailynewsContent`,
   `CurrentAffairsdetail`) — action bar "Job Details", white card with heading, timestamp +
   expired date row, WebView rendering `jobContent` HTML, bottom banner ad.
6. **Category** — search view + list of `category_name` chips → CategoryContent.
7. **CategoryContent** — filtered job list → content screen.
8. **Notification** — list of `maintitle` / `secondtitle` / `publishdate`.

Design tokens: `primary #00175f`, `one #f6a533`, `two #e79d35`, `three #d59135`,
`four #bd8434`, `five #33457f`, `six #354d9b`, `seven #3554b7`, `eight #3459d3`,
`back #f8f8f8`, `txtlight #acb6c0`, card radius 15dp (grid) / 5dp (list), font Poppins.

## 5. Push messaging

`MyFirebaseMessagingService` subscribes to topic `sarkariexamalerts`; the data payload is
JSON `{title, message, image, action_destination, notificationid}` where
`action_destination ∈ {currentaffairs, govtjob, dailynews, examresultpost, …}` chooses
the target activity. `AppOpenManager` implements AdMob app-open ad lifecycle.

## 6. Findings (security)

* `debuggable=true` shipped, cleartext traffic allowed, secrets/endpoints hidden in a
  trivially dump-able `.so` (no obfuscation) — no real protection.
* No auth/TLS pinning; all endpoints are unauthenticated GETs, no rate limiting visible.
* Admob unit IDs are placeholder/test values (`ca-app-pub-3940256099942544~3347511713`).
* Backend demo host is dead → the app cannot function as installed (white-list/`status:false` path).

## 7. Clone scope

Reimplemented cleanly (no third-party source) as:
* `backend/` — PHP 8 + MySQL, or a dependency-free Node.js server, reproducing the
  exact contract above, plus the admin API.
* `admin/` — responsive web admin panel (posts CRUD for the 4 feeds with filter labels,
  filter values, notifications, AdMob settings). Pushes are sent from the Firebase console.
* `app/` — Android app (Java + Gradle) with the same screens, navigation, colours, ad
  placements and API calls. The backend root is the Gradle property `apiBase`.

## 8. REA analysis run (2026-11-10)

The APK was re-analysed with [REA](https://github.com/morluto/rea) 6.8.0
(`rea-agents` npm package) to verify the clone against the original's behaviour.

| Step | REA command | Engine |
|---|---|---|
| Manifest, permissions, components | `rea inspect-android-package` | JADX headless MCP 0.7.1 |
| Signing | `rea inspect-apk-signature` | built-in |
| Resources / strings | `rea decode-android-resources` | Apktool 3.0.3 |
| App class inventory | `rea search-android-classes … com.gsbussiness` (22 classes) | JADX |
| Every method of 135 app classes (incl. inner + `c.d.a.*` helpers), 560 methods | `rea inspect-android-class`, `rea inspect-android-method` | JADX |
| `libhello-jni.so` exports and strings | `rea inspect-native-exports`, `rea search --pattern api/` | Ghidra 12.1.4 |

APK SHA-256 `4aad7af82bc95b9e3acb679aebf48eea1c79d51afe03b0a1528dd14f64764edb`;
`libhello-jni.so` (arm64) SHA-256 `266368bb23ac2917583806d894565089129b83bd9553df07f0bddd8cea36a73c`,
10 `Java_…_ConstClass_*` exports. Ghidra string search returned the base URL at
`0x100cfc` and the nine `api/*.php` paths at `0x100d22`–`0x100dd0`, matching §2.

Behaviour confirmed from the decompiled methods, and how the clone now matches:

| Original (REA evidence) | Clone before | Clone now |
|---|---|---|
| All calls are POST form requests (`c.d.a.g.c.a`, method `1`, `getParams()`) | GET with query string | POST form (`Repo.post`) |
| `Category.T()` sends `filter=<group>` | sent `?filter=`, but both backends read `title` | backends read `filter` |
| `CategoryContent.N()` sends `labelid`, `filter`; lists jobs for that label | backends returned all jobs (no post↔label link) | `post_labels` table / `labels` array; admin editor picks labels |
| `MainActivity` → `Category` with extra `title` | `FilterActivity` missing from manifest (crash) | declared |
| `SplashScreen`: settings → interstitial → `postDelayed(6000)` → `StartScreen` | 1.5 s, skipped Start after first run | same flow; Start shown every launch |
| `StartScreen`: native ad, `ACTION_VIEW(@string/privacy)`, share text, double-back exit (2000 ms) | privacy was a Toast; no ad | implemented (own `privacy_url`) |
| Interstitial after every home tap and on opening a govt-job post; banner on list/detail; native on Start/Home; `AppOpenManager` on `ON_START` | ads not wired | `ads/Ads.java`, `ads/AppOpenManager.java` |
| Detail: `WebView.loadDataWithBaseURL`, JS hides first `<h1>`, shows `publishdate`; govt jobs show `"Last Date: "` and an alarm button (`ACTION_SET_ALARM`, `EXTRA_MESSAGE` = title) | TextView, no reminder | WebView + reminder button |
| `MyFirebaseMessagingService`: topic subscribe, payload `{title, message, image, action_destination, notificationid}` → `Currentaffairs` / `AllGovjob` / `Dailynews` / `Examresults` / `MainActivity` | not implemented | `push/MessagingService.java` (optional, needs own `google-services.json`) |
| Base URL + endpoints from `libhello-jni.so` | — | plain Gradle property; app appends `api/<name>.php` |

The original's Firebase project, Google API key, AdMob ids and privacy URL (visible in
`res/values/strings.xml`) belong to the original publisher and are deliberately **not**
reused in the clone.
