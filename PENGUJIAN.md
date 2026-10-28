# Pengujian BelajarBermain TV

Kolom **Status** hanya berisi dua nilai: ✅ *Sudah dijalankan* (dengan lingkungan yang disebut) atau ⬜ *Belum — rekomendasi untuk Anda*.

## A. Yang SUDAH dijalankan (di lingkungan pengembangan, bukan di TV)
Lingkungan: Chromium headless (Playwright) memuat situs lokal dengan `?tv=1`, remote disimulasikan dengan keyboard.

| # | Pengujian | Hasil |
|---|---|---|
| 1 | Layout 1366×768 dan 1024×576 (Beranda, Bermain, Belajar, Profil): header tidak meluap, kartu 3×2 / 4×2 muat, banner "Mulai Cerita" tampil | ✅ lolos setelah perbaikan header (dilihat lewat screenshot) |
| 2 | Fokus awal otomatis di tombol "Mulai Belajar" saat Beranda terbuka | ✅ |
| 3 | Panah atas/bawah/kiri/kanan: hero → banner → kartu → menu atas → menu Belajar | ✅ |
| 4 | Enter membuka menu, membuka permainan ("Ganjil/Genap"), menekan "Mulai Main", menjawab soal | ✅ |
| 5 | Escape (= Back): permainan → daftar Belajar (fokus kembali ke kartu yang dipilih) → Beranda; di Beranda `__tvBack()` mengembalikan `false` (aplikasi Android yang menangani keluar) | ✅ |
| 6 | Dialog Pengaturan Suara: fokus masuk ke dialog, panah tidak keluar dari dialog, Escape menutup, fokus kembali ke tombol suara | ✅ |
| 7 | Labirin: panah menggerakkan dino (posisi 0 → 8), fokus tidak berpindah | ✅ |
| 8 | Tautan luar di footer tidak bisa difokus dengan D-pad | ✅ |
| 9 | Deteksi TV: User-Agent berisi `BelajarBermainTV` → kelas `tv tvd` aktif, tombol Pasang/Layar Penuh/Mode TV/Foto disembunyikan; HP (390×844) dan PC (1280×800) **tanpa** kelas TV dan tanpa error JavaScript | ✅ |
| 10 | Sintaks `tv.js` (`node --check`), semua XML Android terbentuk benar (parse) | ✅ |

## B. BELUM dijalankan — wajib Anda lakukan di TV
| # | Pengujian | Cara / Hasil yang diharapkan | Status |
|---|---|---|---|
| 1 | **APK berhasil dibangun** (kode Kotlin & Gradle belum pernah dikompilasi) | Cara A/B/C di README; bila error, kirim log | ⬜ |
| 2 | APK terpasang di Xiaomi TV A 32 tanpa error "tidak kompatibel" | `adb install -r` sukses | ⬜ |
| 3 | Ikon + banner BelajarBermain muncul di layar utama/baris Aplikasi Google TV | Terlihat jelas, tidak terpotong | ⬜ |
| 4 | Aplikasi terbuka layar penuh tanpa address bar, tanpa kilatan putih | Layar muat biru lalu Beranda | ⬜ |
| 5 | Tampilan di 1366×768 nyata: huruf terbaca dari sofa, tidak terpotong overscan, kartu tidak meluap | Sesuaikan `tv.css` bila perlu | ⬜ |
| 6 | D-pad, **OK (DPAD_CENTER)**, Back, Play/Pause di remote Xiaomi (Bluetooth/IR) | OK memilih; Play/Pause mengubah musik | ⬜ |
| 7 | Back: dialog → permainan → daftar → Beranda → "tekan sekali lagi" → keluar | Sesuai urutan | ⬜ |
| 8 | Audio: musik latar loop & efek benar/salah/selesai terdengar; mulai setelah tombol pertama ditekan | Tidak ada autoplay berisik sebelum interaksi | ⬜ |
| 9 | Video | Situs saat ini **tidak punya pemutar video**; tidak ada yang diuji | — |
| 10 | Koneksi lambat (mis. hotspot terbatas): layar muat tampil lalu halaman muncul | Tidak layar putih | ⬜ |
| 11 | Internet putus **sebelum** pertama dibuka: layar "Oops! Tidak bisa terhubung" + tombol Coba Lagi/Keluar yang bisa dipilih remote | Coba Lagi berhasil setelah internet kembali | ⬜ |
| 12 | Internet putus **setelah** pernah dibuka online: situs tampil dari cache Service Worker (offline) | Permainan tetap jalan; lencana "Offline" | ⬜ |
| 13 | Pembaruan versi: unggah versi baru ke GitHub Pages (naikkan `bba-v22` → `bba-v23`), buka aplikasi 2 kali | Konten baru terlihat | ⬜ |
| 14 | Tombol "Mulai Cerita" → Kisah3Dino: terbuka, dapat dikendalikan remote, Back kembali ke BelajarBermain | Bukan bagian proyek ini; cek kegunaannya | ⬜ |
| 15 | Permainan lain dengan remote: Mewarnai, Menghubungkan Garis, Puzzle Petualangan (seret-dan-lepas), Memori, Ingat Urutan | Dapat diselesaikan hanya dengan D-pad + OK; bila tidak, catat permainan mana | ⬜ |
| 16 | Stabilitas memori: main 20–30 menit berpindah-pindah permainan | Tidak lag/keluar sendiri (RAM 1,5 GB) | ⬜ |
| 17 | Versi Android System WebView di TV | Dianjurkan Chrome ≥ 111 | ⬜ |
| 18 | Resolusi 720p vs 1080p (ubah di pengaturan TV bila tersedia) | Tata letak tetap rapi | ⬜ |
| 19 | Instal ulang / peluncuran ulang / restart TV | Aplikasi terbuka normal; progres anak tetap ada | ⬜ |
| 20 | Google TV vs Android TV versi lain (bila punya perangkat lain) | Ikon & navigasi sama | ⬜ |
