<div align="center">

<img src="https://github.com/Madhu2150/Parisara-Cycle/blob/main/parisara_icon.png" width="120" height="120" style="border-radius: 20px" alt="Parisara-Cycle Logo"/>

# 🚴 PARISARA-CYCLE

### *Green Commuter Guide — Android App powered by GenAI*

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)
[![Gemini AI](https://img.shields.io/badge/AI-Gemini-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=gradle)](https://gradle.org)

---

### 🌿 *Every kilometre cycled is a step towards a greener, healthier town!*

---

## ⬇️ Download App

<a href="https://firebasestorage.googleapis.com/v0/b/parisara-cycle-20865.firebasestorage.app/o/app-debug.apk?alt=media&token=65ef4ae3-00da-4dcb-a7e6-14ab459cc4ba">
  <img src="https://img.shields.io/badge/⬇️%20Download%20APK-Latest%20Release-2E7D32?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" height="50"/>
</a>

<br/>

| Version | Size | Min Android | Updated |
|---------|------|-------------|---------|
| v1.0.0  | ~15MB | Android 8.0 (API 26) | May 2026 |

</div>

---

## 📱 About The App

**Parisara-Cycle** is a community-driven cycling navigation app designed for students and working professionals in **tier-2 and tier-3 towns across India**. It transforms cycling from a necessity into a *proud, data-driven, community-backed green choice*.

### 🎯 Problems We Solve

| Problem | Solution |
|---------|----------|
| 😰 Road Safety Anxiety | Safe route mapping avoiding highways |
| 🗺️ Route Ignorance | OpenStreetMap with cycling-friendly paths |
| 🚧 No Hazard Reporting | Community pothole & danger zone pinning |
| 😔 Cycling Isolation | Real-time Buddy System for group cycling |

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 🗺️ Safe Route Map
- OpenStreetMap (OSMDroid) integration
- Hazard overlay with community pins
- Long-press to mark destinations
- Real-time location tracking

### ⚠️ Hazard Reporting
- Pin potholes, blocked paths, dangerous intersections
- Community upvoting system
- Instant Firestore sync
- Visible to all users in real-time

### 🌿 Eco-Stats Engine
- CO₂ savings: **1 km = 120g CO₂ saved**
- Daily & monthly totals
- Persistent across app restarts (Room DB)
- Visual progress gauge

</td>
<td width="50%">

### 👥 Buddy System
- Real-time location broadcasting
- Find cyclists on same route
- 10-second update interval
- Firebase Realtime Database

### 🤖 AI Safety Tips
- Powered by **Google Gemini API**
- Route safety summaries
- Hazard-specific cycling tips
- Natural language descriptions

### 👤 User Profile
- Firebase Authentication
- Profile photo upload
- Display name editing
- Green impact statistics

</td>
</tr>
</table>

---

## 🛠️ Tech Stack

```
┌─────────────────────────────────────────────────────┐
│                    PARISARA-CYCLE                    │
├─────────────────┬───────────────────────────────────┤
│ Language        │ Kotlin                             │
│ UI Framework    │ Jetpack Compose                    │
│ Architecture    │ MVVM + Repository Pattern          │
│ DI              │ Hilt (Dagger)                      │
│ Maps            │ OSMDroid (OpenStreetMap) — Free!   │
│ AI/GenAI        │ Google Gemini API                  │
│ Auth            │ Firebase Authentication            │
│ Database        │ Cloud Firestore + Realtime DB      │
│ Local Storage   │ Room Database + DataStore          │
│ File Storage    │ Firebase Storage                   │
│ Image Loading   │ Coil                               │
│ Min Android     │ API 26 (Android 8.0)               │
└─────────────────┴───────────────────────────────────┘
```

---

## 📊 CO₂ Calculation

```
Formula:  CO₂ Saved (g) = Distance (km) × 120

Examples:
  1 km  cycling  →   120g CO₂ saved
  5 km  cycling  →   600g CO₂ saved
  10 km cycling  → 1,200g CO₂ saved
  
Equivalent to avoiding car travel of same distance.
Source: Average Indian car emits ~120g CO₂/km
```

---

## 🚀 Getting Started

### Prerequisites

```
✅ Android Studio Ladybug (2024.2.1) or newer
✅ Android Device or Emulator (API 26+)
✅ Firebase Account (free)
✅ Google Gemini API Key (free tier)
✅ Git
```

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/Madhu2150/Parisara-Cycle.git
cd parisara-cycle
```

**2. Create `local.properties`**
```properties
sdk.dir=YOUR_ANDROID_SDK_PATH
GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
```

**3. Add Firebase config**
```
• Create project at console.firebase.google.com
• Enable: Authentication, Firestore, Realtime DB, Storage
• Download google-services.json
• Place in app/ folder
```

**4. Get Gemini API Key**
```
• Go to: https://aistudio.google.com/app/apikey
• Create API Key (free)
• Add to local.properties
```

**5. Build & Run**
```bash
./gradlew assembleDebug
```

---

## 🔥 Firebase Setup

```
Services Used:
├── 🔐 Authentication    → Email/Password login
├── 📄 Firestore         → Hazard pins storage
├── ⚡ Realtime Database  → Buddy live locations
├── 📦 Storage           → Profile photos
└── 📊 Crashlytics       → Crash reporting
```

### Firestore Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /hazard_pins/{pinId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update: if request.auth != null
        && request.resource.data.diff(resource.data)
            .affectedKeys().hasOnly(['upvotes']);
      allow delete: if request.auth != null
        && resource.data.reportedBy == request.auth.uid;
    }
    match /users/{userId} {
      allow read, write: if request.auth != null
        && request.auth.uid == userId;
    }
  }
}
```

---

## 📁 Project Structure

```
parisara-cycle/
├── app/src/main/java/com/parisara/cycle/
│   ├── 📂 data/
│   │   ├── local/          → Room Database (EcoStats)
│   │   ├── model/          → Data classes
│   │   ├── remote/         → Firebase, Gemini services
│   │   └── repository/     → Data repositories
│   ├── 📂 di/
│   │   └── AppModule.kt    → Hilt dependency injection
│   ├── 📂 ui/
│   │   ├── screens/
│   │   │   ├── auth/       → Login/Register
│   │   │   ├── home/       → Home dashboard
│   │   │   ├── map/        → OSMDroid safe route map
│   │   │   ├── hazard/     → Hazard reporting
│   │   │   ├── ecostats/   → CO₂ tracking
│   │   │   ├── buddy/      → Real-time buddy system
│   │   │   └── profile/    → User profile & settings
│   │   ├── navigation/     → NavGraph
│   │   └── theme/          → Material3 theme
│   └── 📂 util/
│       ├── GeoHashUtil.kt  → Location hashing
│       └── Constants.kt    → App constants
└── app/src/main/res/
    ├── mipmap-*/           → App icons (all densities)
    └── values/             → Colors, themes, strings
```

---

## 🌍 Impact Goals

<table>
<tr>
<td align="center">🌱<br/><b>Net Zero</b><br/>Carbon-neutral transport</td>
<td align="center">💪<br/><b>Public Health</b><br/>Daily physical activity</td>
<td align="center">🛡️<br/><b>Road Safety</b><br/>Community hazard data</td>
</tr>
<tr>
<td align="center">👥<br/><b>Community</b><br/>Buddy cycling culture</td>
<td align="center">📊<br/><b>Data Policy</b><br/>Municipal planning data</td>
<td align="center">🏙️<br/><b>Smart Cities</b><br/>Tier-2/3 town mobility</td>
</tr>
</table>

---

## ✅ Success Criteria

| ID | Criteria | Status |
|----|----------|--------|
| SC-01 | Eco-Stats persist & show Monthly Total | ✅ Done |
| SC-02 | Map allows pinning danger zones | ✅ Done |
| SC-03 | UI renders correctly on 5-inch 720p device | ✅ Done |
| SC-04 | Route avoids high-speed highways | ✅ Done |
| SC-05 | Buddy System shows live markers | ✅ Done |
| SC-06 | GenAI summary loads within 5 seconds | ✅ Done |
| SC-07 | Background service < 5% battery/hour | ✅ Done |

---

## 📦 How to Download & Install APK

```
Method 1: GitHub Releases (Recommended)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Go to Releases tab on this page
2. Click latest release
3. Download Parisara-Cycle-v1.0.apk
4. On your Android phone:
   Settings → Security → Unknown Sources → ON
5. Open the downloaded APK
6. Click Install
7. Open Parisara-Cycle ✅

Method 2: Build from Source
━━━━━━━━━━━━━━━━━━━━━━━━━━
git clone https://github.com/Madhu2150/Parisara-Cycle.git
# Add local.properties and google-services.json
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

```
MIT License

Copyright (c) 2026 Parisara-Cycle

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software to use, copy, modify, merge,
publish, distribute, sublicense, and/or sell copies of the Software.
```

---

<div align="center">

**⭐ Star this repo if you found it useful!**

[![GitHub stars](https://img.shields.io/github/stars/Bruce025/parisara-cycle?style=social)](https://github.com/Madhu2150/Parisara-Cycle/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/Bruce025/parisara-cycle?style=social)](https://github.com/Madhu2150/Parisara-Cycle/network/forkers)
[![GitHub issues](https://img.shields.io/github/issues/Bruce025/parisara-cycle)](https://github.com/Madhu2150/Parisara-Cycle/issues)

---

*🌿 Built for a greener India*

**[⬆ Back to Top](#-parisara-cycle)**

</div>
