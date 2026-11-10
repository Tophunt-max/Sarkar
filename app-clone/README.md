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

1. **Splash** — logo on the navy brand colour, prefetches ad settings.
2. **Intro / Start** — "Find jobs start here", "Share App", "Privacy policy". Shown once.
3. **Home** — 4 feed tiles (Find All Gov. Jobs, Current Affairs, Daily News Alerts,
   Exam Results), a native-ad slot, and 4 filter rows (Category, Profession,
   Qualification, Location), plus a notification bell.
4. **List** — title, publish date, 3-line preview; "View Expired" toggle on the govt-jobs feed; pull to refresh; loading shimmer; empty state.
5. **Detail** — full HTML-rendered body, publish date, last date, share button.
6. **Filter values** — searchable list of values within one filter group.
7. **Notifications** — announcement list behind the bell.

Colours, spacing and the launcher/section icons were taken from the original APK, so the
app looks like the original rather than a generic template.

## 2. Data model & API contract

The APK's endpoints were read out of its native library (`libhello-jni.so`) and matched
against every screen's parsing code. They are preserved exactly:

| Endpoint | Returns |
|----------|---------|
| `api/appsettings.php` | `{status, appsettings:[{admob_banner, admob_interstitial, admob_native, admob_appopen}]}` |
| `api/getgovtjob.php` | `{status, govtjob:[post...]}` |
| `api/getcurrentaffairs.php` | `{status, currentaffairs:[post...]}` |
| `api/getresultpost.php` | `{status, resultpost:[post...]}` |
| `api/getdailynews.php` | `{status, dailynews:[post...]}` |
| `api/getexpiredpost.php` | `{status, govtjob:[post...]}` (archive) |
| `api/getfilter.php?filter=Category` | `{status, filter:[{id, category_name}]}` |
| `api/getpostbyfilter.php?filter=Category&labelid=3` | `{status, govtjob:[post...]}` |
| `api/getnotification.php` | `{status, notification:[{maintitle, secondtitle, publishdate}]}` |

A `post` is:

```json
{
  "postid": "SRK00001",
  "jobTitle": "SSC CGL 2024 — 17,727 Posts",
  "jobContent": "<b>HTML</b> body …",
  "postPublishDate": "2026-11-08",
  "formattedDate": "08 Nov 2026",
  "expiredDate": "",
  "isExpired": 0
}
```

Filter groups are exactly the four the app uses: `Category`, `Profession`,
`Qualification`, `Location`.

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

# 2. point the app at your backend (10.0.2.2 = host machine from the emulator)
./gradlew assembleDebug -PapiBase=http://10.0.2.2:8000/api/

# APK -> app/build/outputs/apk/debug/app-debug.apk
```

The base URL defaults to `http://10.0.2.2:8000/api/` (emulator → localhost). For a phone,
use your computer's LAN IP, e.g. `-PapiBase=http://192.168.1.20:8000/api/`.

In Android Studio simply **Open** the repo root and press Run. In VS Code, install the
"Extension Pack for Java" and "Android" extensions, then use the Gradle tasks.

### AdMob

`app/src/main/res/values/strings.xml` holds the app id and the backend supplies the ad
unit ids (`appsettings.php`). Both are Google's **test** ids, so ads show as test ads —
replace them with your own to monetise.

## 5. Admin panel

| Page | What you can do |
|------|-----------------|
| Sign in | session-based login (`admin_users` table / `db.json`) |
| Dashboard | counts per feed, active vs expired, recent posts |
| Posts | create / edit / delete per feed, set publish + expiry date, HTML body, trash & restore |
| Filter labels | manage Category / Profession / Qualification / Location values |
| Notifications | publish announcements for the bell screen |
| Settings | AdMob unit ids served by `appsettings.php` |

All write calls require the session cookie and a CSRF token.

## 6. What is and is not included

Included: all screens, navigation, list/detail rendering, HTML body support, filters with
search, expired archive, notifications, pull-to-refresh, shimmer loading, empty/offline
states, share intents, AdMob banner hooks and the complete admin panel + API.

Not carried over from the original: Firebase Cloud Messaging push (the original used
Firebase; this clone serves announcements through the API instead) and the obfuscated
native/AdMob SDK plumbing. Google Play sdk is wired but push notifications need your own
Firebase project if you want them.

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
