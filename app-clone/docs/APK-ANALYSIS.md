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

### `GET api/appsettings.php` → key `appsettings[]`
Items: `admob_banner`, `admob_interstitial`, `admob_native`, `admob_appopen`.
Client caches each into SharedPreferences via `c.d.a.e.a.b(ctx, key, value)` and only
then boots the interstitial + 6 s delayed navigation to `StartScreen`.

### `GET api/getgovtjob.php` → key `govtjob[]`
### `GET api/currentaffairs.php` → key `currentaffairs[]`
### `GET api/getresultpost.php` → key `resultpost[]`
### `GET api/getdailynews.php` → key `dailynews[]`
### `GET api/getexpiredpost.php` → key `govtjob[]`
Each item: `jobTitle`, `jobContent` (HTML), `postid`, `postPublishDate`,
`formattedDate`, and (`govtjob`/expired only) `expiredDate`.

### `GET api/getfilter.php` → key `filter[]`
Item: `category_name`, `id`. The activity title (`Category` / `Profession` /
`Qualification` / `Location`, passed as Intent extra) selects which filter group
the backend returns; the title is echoed as `"<title> List"`.

### `GET api/getpostbyfilter.php` (POST-style params `labelid`, `filter`) → key `govtjob[]`
`labelid` = `filter[].id`, `filter` = the group title.

### `GET api/getnotification.php` → key `notification[]`
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
* `backend/` — PHP 8 + MySQL admin API reproducing the exact contract above.
* `admin/` — responsive web admin panel (posts CRUD for the 5 feeds, filter labels,
  notifications, AdMob settings, push).
* `app/` — Android app (Kotlin/Java + Gradle) with the same screens, navigation,
  colours and API calls, pointing at the local backend via a single `Const` file.
