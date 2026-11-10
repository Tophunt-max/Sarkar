# Sarkar — APK + Full Clone

This repo holds the original APK and a complete, editable re-implementation of it.

| Path | What it is |
|------|------------|
| `sarkariexamadmin1-8-22.apk` | The original APK (reference only) |
| [`app-clone/`](./app-clone) | Full clone: Android app + admin panel + backend, as editable source |

## The clone (`app-clone/`)

| Folder | What it is |
|--------|------------|
| `app-clone/app` | Android app — **Java**, Gradle project. Open in Android Studio / VS Code. |
| `app-clone/admin` | Admin panel — sign in, manage posts, filters, notifications, settings. |
| `app-clone/backend` | REST API — same endpoints as the original app (PHP+MySQL or Node.js). |
| `app-clone/docs` | Analysis of the original APK + decompiled reference sources. |

### Quick start

```bash
# 1. backend + admin panel
cd app-clone/backend
node server.js                     # http://localhost:8000
#   Admin: http://localhost:8000/admin/  →  admin / admin123

# 2. Android app
cd app-clone
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
./gradlew assembleDebug -PapiBase=http://10.0.2.2:8000/api/
#   APK -> app/build/outputs/apk/debug/app-debug.apk
```

Screens reproduced: Splash, Intro, Home (4 feeds + 4 filters), List (expired
archive + pull-to-refresh), Detail (HTML body + share), Filter values (search),
Notifications — with the original colours and icons taken from the APK.

See [`app-clone/README.md`](./app-clone/README.md) for the full API contract,
data model and admin documentation.
