# RozgarOne — Government Job Alerts App + Admin Panel + Backend

> Fork of the *Sarkar* repo: the original APK lives at the repo root, and the
> full working re-implementation lives in [`app-clone/`](./app-clone).

**RozgarOne** is a working, editable government-job-alerts app, re-implemented from the
**Sarkari Exam Updates** Android app (originally `com.gsbussiness.sarkariexamupdates`,
distributed as `sarkariexamadmin1-8-22.apk`),
including the admin panel and the REST backend it talks to.

The original APK is a compiled native Android app whose content came from a PHP/MySQL
backend at `https://gtupro.xyz/sarkarialertsdemo/` (that server no longer responds).
This repo rebuilds the **same screens, same data model and same API contract** as
readable source code.

| Folder | What it is |
|--------|------------|
| [`app/`](./app) | Android app — **Java**, Gradle project. Open in Android Studio / VS Code. |
| [`admin/`](./admin) | Admin panel — plain HTML/CSS/JS, sign in and manage everything. |
| [`backend/`](./backend) | REST API — same endpoints as the original app, two interchangeable implementations (PHP + MySQL, or Node.js with a JSON store). |
| [`docs/`](./docs) | Analysis of the original APK, plus the decompiled reference sources. |

---

## 1. Screens reproduced

The original flow, screen by screen:

1. **Splash** — logo on the navy brand colour. Fetches `appsettings.php`, caches the ad
   unit ids, preloads an interstitial, waits 6 s (as the original does), then opens Start.
2. **Start** — native ad, "Find jobs start here", "Share App", "Privacy policy" link
   (opens `privacy_url`), double-tap back to exit. Shown on every launch, like the original.
3. **Home** — 4 feed tiles (Find All Gov. Jobs, Current Affairs, Daily News Alerts,
   Exam Results), a native ad, and 4 filter rows (Category, Profession,
   Qualification, Location), plus a notification bell. Each tap shows an interstitial.
4. **List** — title, formatted date, 3-line preview; "View Expired" toggle on the
   govt-jobs feed; pull to refresh; loading shimmer; empty state; bottom banner ad.
5. **Detail** — HTML body in a WebView (first `<h1>` hidden), publish date, bottom banner.
   Govt jobs also show "Last Date" and a reminder button that opens the clock app's
   set-alarm screen. A share button is added on top of the original.
6. **Filter values** — "&lt;group&gt; List" with search; tapping a value opens
   "&lt;value&gt; Job List" (the govt jobs tagged with that label).
7. **Notifications** — announcement list behind the bell.
8. **Push** (optional) — FCM data messages `{title, message, image, action_destination,
   notificationid}` open the matching feed. Needs your own `app/google-services.json`.

Colours, spacing and the launcher/section icons were taken from the original APK, so the
app looks like the original rather than a generic template.

## 2. Data model & API contract

The APK's endpoints were read out of its native library (`libhello-jni.so`) and matched
against every screen's parsing code. They are preserved exactly. Like the original, the
app sends every call as an HTTP **POST** with form-encoded fields; both backends also
accept GET with the same fields as query parameters.

| Endpoint | Form fields | Returns |
|----------|-------------|---------|
| `api/appsettings.php` | — | `{status, appsettings:[{admob_banner, admob_interstitial, admob_native, admob_appopen}]}` |
| `api/getgovtjob.php` | — | `{status, govtjob:[post...]}` (active jobs) |
| `api/getcurrentaffairs.php` | — | `{status, currentaffairs:[post...]}` |
| `api/getresultpost.php` | — | `{status, resultpost:[post...]}` |
| `api/getdailynews.php` | — | `{status, dailynews:[post...]}` |
| `api/getexpiredpost.php` | — | `{status, govtjob:[post...]}` (archive) |
| `api/getfilter.php` | `filter=Category` | `{status, filter:[{id, category_name}]}` |
| `api/getpostbyfilter.php` | `filter=Category&labelid=3` | `{status, govtjob:[post...]}` (active jobs tagged with label 3) |
| `api/getnotification.php` | — | `{status, notification:[{maintitle, secondtitle, publishdate}]}` |

A `post` is:

```json
{
  "postid": "SRK00001",
  "jobTitle": "SSC CGL 2024 — 17,727 Posts",
  "jobContent": "<b>HTML</b> body …",
  "postPublishDate": "2026-11-08",
  "formattedDate": "08 Nov 2026",
  "expiredDate": "28 Nov 2026"
}
```

`expiredDate` is only sent for govt-job lists.

Filter groups are exactly the four the app uses: `Category`, `Profession`,
`Qualification`, `Location`. A govt-job post can carry any number of filter labels
(table `post_labels`, set in the admin post editor); `getpostbyfilter.php` returns the
active posts carrying the requested label.

## 3. Run the backend + admin panel

### Option A — Node.js (no database, zero setup)

```bash
cd backend
node server.js          # http://localhost:8000
```

- Public API: `http://localhost:8000/api/getgovtjob.php`
- Admin panel: `http://localhost:8000/admin/` — **admin / admin123**

Data is stored in `backend/data/db.json` and seeded with demo posts on first run.

### Option B — PHP + MySQL (production)

Existing installs: re-run `schema.sql` once to add the new `post_labels` table (every
statement is `IF NOT EXISTS` / `INSERT IGNORE`, so existing data is kept). For the Node
server, delete `backend/data/db.json` to reseed demo data with labels.

The admin panel calls `/admin_api/` on the same host, so serve `admin/` and
`backend/admin_api/` from one web root (e.g. copy `admin/` into `backend/admin/`).

```bash
mysql -u root -p < backend/schema.sql      # creates db `rozgarone`, default admin
# point your web server at backend/ , or:
php -S localhost:8000 -t backend
```

Both implementations serve the identical API, so the app cannot tell them apart.

## 4. Build the Android app

Requirements: JDK 17+, Android SDK (platform 34, build-tools 34.0.0).

```bash
# 1. tell Gradle where the SDK is
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 2. point the app at your backend root (10.0.2.2 = host machine from the emulator)
./gradlew assembleDebug -PapiBase=http://10.0.2.2:8000/

# APK -> app/build/outputs/apk/debug/app-debug.apk
```

`apiBase` is the folder that contains `api/`; the app appends `api/<name>.php`, exactly
like the original's `baseurl + endpoint`. It defaults to `http://10.0.2.2:8000/`
(emulator → localhost). For a phone, use your computer's LAN IP, e.g.
`-PapiBase=http://192.168.1.20:8000/`. A value ending in `/api/` is also accepted.

Gradle 8.7 needs JDK 17–21 to run (JDK 22+ fails with "Unsupported class file major
version").

In Android Studio simply **Open** the repo root and press Run. In VS Code, install the
"Extension Pack for Java" and "Android" extensions, then use the Gradle tasks.

### AdMob

`app/src/main/res/values/strings.xml` holds the app id and the backend supplies the ad
unit ids (`appsettings.php`). Both are Google's **test** ids, so ads show as test ads —
replace them with your own to monetise. Placements match the original (code in
`app/src/main/java/com/rozgarone/ads/`):

| Placement | Where |
|-----------|-------|
| Interstitial | splash exit, every home-screen tap, opening a govt-job post |
| Native | start screen and home screen (`mobadslayout`) |
| Adaptive banner | bottom of list and detail screens (`adMobView`) |
| App open | returning to the app from the background |

An empty unit id in Settings turns that placement off.

### Push notifications (optional)

Create a Firebase project, add an Android app with package `com.rozgarone`, and copy its
`google-services.json` into `app/`. The build then applies the Google Services plugin,
the app subscribes to the FCM topic `rozgarone_alerts`, and data messages shaped like

```json
{"title": "SSC CGL 2026", "message": "Apply before 30 Nov", "image": "",
 "action_destination": "govtjob", "notificationid": "12"}
```

open the matching feed (`govtjob`, `currentaffairs`, `dailynews`, `examresultpost`; anything
else opens Home). Without the file the app builds and runs with push turned off. Sending
pushes is done from the Firebase console; the admin panel does not send them.

### Privacy policy

Set `privacy_url` in `strings.xml` to your own published policy before releasing.

## 5. Admin panel

| Page | What you can do |
|------|-----------------|
| Sign in | session-based login (`admin_users` table / `db.json`) |
| Dashboard | counts per feed, active vs expired, recent posts |
| Posts | create / edit / delete per feed, set publish + expiry date, HTML body, filter labels (govt jobs), trash & restore |
| Filter labels | manage Category / Profession / Qualification / Location values |
| Notifications | publish announcements for the bell screen |
| Settings | AdMob unit ids served by `appsettings.php` |

All write calls require the session cookie and a CSRF token.

## 6. What is and is not included

Included: all screens, navigation, WebView detail rendering, job reminders, filters with
search and real label-based job lists, expired archive, notifications, pull-to-refresh,
shimmer loading, empty/offline states, share intents, all four AdMob placements,
optional FCM push and the complete admin panel + API.

Intentional differences from the original:

* Endpoint URLs are not hidden in a native `.so`; the base URL is a Gradle property.
* No Firebase project, ad ids, privacy URL or branding from the original are reused —
  you supply your own.
* If the device is offline or settings fail to load, the splash continues after 1.5 s
  instead of stopping on the splash screen.
* The four separate list activities and four detail activities of the original are
  folded into one `ListActivity` and one `DetailActivity`.

See [`docs/APK-ANALYSIS.md`](./docs/APK-ANALYSIS.md) for how the original APK was analysed.

## 7. Push to GitHub

```bash
cd app-clone
git init && git add . && git commit -m "Gov Job Alerts clone: app + admin + backend"
git branch -M main
git remote add origin https://github.com/<you>/<repo>.git
git push -u origin main
```

`local.properties`, `app/build/` and `backend/data/db.json` are git-ignored.
