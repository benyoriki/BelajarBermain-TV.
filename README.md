# BelajarBermain — aplikasi Android TV / Google TV (WebView)

Pembungkus WebView ringan yang membuka situs **https://benyoriki.github.io/BelajarBermain/** layar penuh, dengan ikon di layar utama Google TV, navigasi remote (D-pad, OK, Back, Play/Pause), layar muat, dan layar kesalahan bila internet putus.
Situs GitHub Pages + PWA Anda **tidak diubah fungsinya**; yang bertambah hanya lapisan Mode TV (`tv.css`, `tv.js`) yang aktif otomatis di aplikasi ini.

> **Status jujur:** kode proyek ini sudah ditulis, tetapi **APK belum dibangun dan belum diuji di Xiaomi TV** (lingkungan tempat kode ini dibuat tidak bisa mengunduh Android SDK, dan tidak ada TV fisik). Semua langkah di bawah adalah prosedur yang harus Anda jalankan; hasilnya baru valid setelah Anda mencobanya. Lihat `PENGUJIAN.md`.

## Isi folder
```
BelajarBermain-AndroidTV/
├─ settings.gradle.kts, build.gradle.kts, gradle.properties
├─ gradlew, gradlew.bat, gradle/wrapper/      ← Gradle 8.11.1 (diunduh otomatis saat build pertama)
├─ .github/workflows/build-apk.yml            ← bangun APK otomatis di GitHub (tanpa Android Studio)
└─ app/
   ├─ build.gradle.kts                        ← applicationId, versi, START_URL, ALLOWED_HOSTS
   └─ src/main/
      ├─ AndroidManifest.xml                  ← LAUNCHER + LEANBACK_LAUNCHER, banner, izin INTERNET saja
      ├─ java/com/benyoriki/belajarbermain/MainActivity.kt
      └─ res/  (layout, ikon adaptif, banner 640×360, tema, string Indonesia, network_security_config)
```
Application ID: `com.benyoriki.belajarbermain` · minSdk 26 (Android 8.0) · target/compile SDK 35 · Kotlin 2.0.21 · AGP 8.7.3 · JDK 17.

## Langkah 0 — Perbarui situs lebih dulu (wajib)
Aplikasi hanya membuka situs online. Unggah file situs yang diperbarui (folder `website/` pada paket ini; yang baru/berubah: `tv.css`, `tv.js`, `index.html`, `service-worker.js`, `README.md`) ke repositori GitHub Pages Anda, commit, tunggu 1–2 menit, lalu buka situsnya sekali agar Service Worker `bba-v22` terpasang. Tanpa langkah ini aplikasi tetap terbuka, tetapi tampilan memakai layout biasa (bukan Mode TV).

## Cara A — Bangun APK di GitHub (paling mudah, tanpa instalasi)
1. Buat repositori baru (mis. `BelajarBermain-TV`), unggah **seluruh isi folder ini** (termasuk folder tersembunyi `.github`) ke cabang `main`.
2. Buka tab **Actions → Build APK (Android TV) → Run workflow**.
3. Setelah hijau (± 5–8 menit), buka run-nya → **Artifacts → BelajarBermain-debug-apk** → unduh zip → ambil `app-debug.apk`.
Jika build gagal, salin pesan kesalahan merah dari log; kemungkinan hanya versi plugin yang perlu disesuaikan.

## Cara B — Android Studio
1. Pasang Android Studio versi terbaru (Ladybug atau lebih baru), **File → Open** folder ini, tunggu *Gradle sync* (butuh internet; SDK 35 diunduh otomatis bila diminta).
2. **Build → Build Bundle(s)/APK(s) → Build APK(s)**. Hasil: `app/build/outputs/apk/debug/app-debug.apk`.

## Cara C — Terminal
```bash
# JDK 17 + Android SDK terpasang, ANDROID_HOME terisi, lisensi diterima (sdkmanager --licenses)
./gradlew assembleDebug          # Windows: gradlew.bat assembleDebug
```

## Memasang di Xiaomi TV A 32 (Google TV)
Nama menu dapat berbeda sedikit antar versi Google TV.
1. **Mode pengembang:** Pengaturan → Sistem → Tentang → ketuk **Build OS/Build** 7 kali.
2. **Debugging:** Pengaturan → Sistem → Opsi pengembang → aktifkan **USB debugging** (dan **Debugging jaringan/Wireless debugging** bila ada). Catat alamat IP TV (Pengaturan → Jaringan).
3. Dari komputer (satu Wi‑Fi dengan TV; perlu `adb` dari Android platform-tools):
   ```bash
   adb connect 192.168.x.x:5555        # ganti dengan IP TV; setujui dialog "Izinkan debugging" di TV
   adb install -r app-debug.apk
   adb shell monkey -p com.benyoriki.belajarbermain -c android.intent.category.LEANBACK_LAUNCHER 1   # buka aplikasi
   ```
4. **Tanpa komputer:** salin `app-debug.apk` ke flashdisk → colok ke TV → buka dengan aplikasi pengelola file (mis. *File Commander*/*X-plore* dari Play Store), izinkan **Pasang aplikasi tak dikenal** untuk aplikasi itu, lalu pasang.
5. Ikon **BelajarBermain** muncul di **Aplikasi** (kadang di baris "Aplikasi lainnya"). Jika tak terlihat, restart TV atau buka Pengaturan → Aplikasi → Lihat semua → BelajarBermain → Buka.
6. Hapus: `adb uninstall com.benyoriki.belajarbermain`.

Catatan: APK *debug* ditandatangani kunci debug otomatis — cukup untuk memasang di TV sendiri. Untuk Google Play butuh AAB bertanda tangan (di bawah).

## Cara kerja remote
| Tombol | Perilaku |
|---|---|
| D-pad | `tv.js` memindahkan fokus ke kartu/tombol terdekat sesuai arah; cincin kuning tebal menandai fokus. Di Labirin panah menggerakkan dino; di slider volume kiri/kanan mengubah nilai. |
| OK / Enter | Memilih elemen terfokus. |
| Back | Urutan: tutup dialog → batalkan formulir profil → dari permainan ke daftar → ke Beranda. Dari "Mulai Cerita" (situs Kisah3Dino) → kembali ke BelajarBermain. Di Beranda: tekan **dua kali** untuk keluar. Pada layar kesalahan: Back dua kali = keluar. |
| Play/Pause | Menyalakan/mematikan musik latar. (Belum ada pemutar video di situs ini.) |

## Keamanan & privasi
- Hanya izin `INTERNET`. Tidak ada kamera, mikrofon, lokasi, penyimpanan; permintaan izin dari halaman otomatis ditolak.
- HTTPS wajib (`usesCleartextTraffic=false`, `network_security_config`). Kesalahan sertifikat **tidak pernah diabaikan** (`handler.cancel()`).
- Navigasi dikunci ke host `benyoriki.github.io` dan `belajarbermain.online` (situs Anda mengalihkan ke domain ini). Tautan lain (mis. benyoriki.com) diblokir dengan pesan, supaya anak tidak keluar ke web bebas. Ubah di `ALLOWED_HOSTS`.
- JavaScript **diaktifkan** karena seluruh situs berbasis JavaScript; akses berkas/konten dimatikan. Inspeksi WebView (chrome://inspect) hanya di build debug.
- Progres anak tersimpan di localStorage WebView di TV ini saja (sama seperti di browser). Menghapus data aplikasi / memasang ulang = progres hilang.

## Mengubah konfigurasi
- **Alamat situs:** `app/build.gradle.kts` → `START_URL`. Catatan: `benyoriki.github.io/BelajarBermain/` saat ini dialihkan (302) ke `https://belajarbermain.online/`. Memakai alamat kanonis langsung sedikit lebih cepat dan tidak bergantung pada pengalihan; keduanya sudah diizinkan.
- **Versi:** naikkan `versionCode`/`versionName` setiap merilis APK baru.
- **Ikon/banner:** `res/drawable-nodpi/ic_launcher_foreground.png` (432×432, ikon adaptif), `res/drawable-xhdpi/tv_banner.png` (640×360 = 320×180 dp, dipakai launcher TV).

## Rilis (AAB bertanda tangan, untuk Google Play)
1. Buat kunci: `keytool -genkeypair -v -keystore belajarbermain.jks -alias belajarbermain -keyalg RSA -keysize 2048 -validity 10000` (simpan file & kata sandi dengan aman; **jangan** diunggah ke GitHub).
2. Buat `keystore.properties` di root proyek:
   ```
   storeFile=belajarbermain.jks
   storePassword=...
   keyAlias=belajarbermain
   keyPassword=...
   ```
3. `./gradlew bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`.
4. Syarat Google Play yang perlu Anda periksa sendiri di Play Console (kebijakan berubah): akun developer, target API terbaru (sudah 35), banner 320×180, tangkapan layar TV 1920×1080, uji *TV app quality*, deklarasi *Data safety* dan kebijakan **Families/Anak** karena audiens anak, serta kebijakan privasi (situs memakai penghitung pengunjung Abacus). Aplikasi ini deklarasi `leanback required=false` agar mudah diuji di HP; ubah menjadi `true` bila ingin khusus TV.

## Batasan yang diketahui (perlu dicek di perangkat)
- Versi **Android System WebView** di TV bisa lama. Situs memakai `color-mix()` (Chrome 111+); pada WebView lebih lama tampilan kartu tetap berfungsi tetapi tanpa gradasi/border berwarna. Cek: Pengaturan → Aplikasi → Android System WebView.
- Kepadatan layar TV menentukan ukuran CSS: 1366×768 bisa tampil sebagai 1366 atau ±1024 piksel CSS. `tv.css` memakai `clamp(…vw…)` untuk keduanya, tetapi tampilan akhir **harus dilihat di TV**.
- Pemetaan tombol OK (DPAD_CENTER → Enter) oleh WebView dan Play/Pause remote Xiaomi belum diuji.
- Situs **Kisah3Dino** (tombol "Mulai Cerita") adalah situs lain; kompatibilitas remote di dalamnya belum diperiksa dan bukan bagian proyek ini.
- RAM 1,5 GB: aplikasi sengaja ringan (tanpa blur, animasi dekoratif dimatikan), tetapi uji main 20–30 menit di TV nyata sebelum menyatakannya stabil.
- Layar penuh TV memangkas tepi (overscan); `tv.css` memberi margin 3% — sesuaikan `body{padding:0 3vw}` bila perlu.
