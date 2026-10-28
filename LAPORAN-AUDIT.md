# Laporan audit singkat — BelajarBermain / Buku Belajar Anak v19.2

## Struktur proyek (zip `Buku_anak_v19_2`)
Situs statis tanpa build: `index.html` + `style.css`/`modern.css` + JS klasik (`script.js` 43 KB, `bank.js`, `games2.js`, `puzzle.js`, `suara.js`, `ikon.js`, `pengunjung.js`), `manifest.json`, `service-worker.js` (cache `bba-v21`, cache-first), font lokal, `audio/` (4 MP3), gambar WebP. Total ±3 MB, 33 berkas. Tanpa library eksternal.

## Fitur yang ada
14 permainan edukasi (Belajar: 6, Bermain: 8), profil anak + poin/level/lencana/peringkat lokal (localStorage), musik latar & efek suara, mode offline, tombol donasi QRIS, penghitung pengunjung (Abacus), tombol "Mulai Cerita" → situs terpisah `benyoriki.github.io/Kisah3Dino/`.

## Temuan penting
1. **Alamat berbeda dari yang disebut.** `https://benyoriki.github.io/BelajarBermain/` mengalihkan (HTTP 302) ke `https://belajarbermain.online/`; `canonical`, `manifest.id`, Open Graph, dan penghitung pengunjung memakai domain itu. Aplikasi mengizinkan kedua host.
2. **Tidak ada menu Cerita, Video, atau Favorit di proyek ini.** Cerita ada di situs lain (Kisah3Dino), tidak ada berkas video, dan tidak ada fitur favorit. Sesuai instruksi "jangan mengganti dengan konten contoh", menu itu **tidak saya buat**; menu TV tetap Beranda · Belajar · Bermain · Peringkat · Profil + banner "Mulai Cerita".
3. **PWA tidak bisa diandalkan di TV.** Pemasangan PWA bergantung browser; Google TV pada umumnya tidak menyediakan browser dengan "Tambahkan ke layar utama". Jalur realistis: **APK Android TV dengan WebView** (dibuat). TWA tidak dipilih: butuh browser Chrome di TV + Digital Asset Links yang belum bisa diverifikasi.
4. **Navigasi remote lama kurang lengkap** (`script.js` baris 81): tidak menjangkau tautan `<a>` (banner "Mulai Cerita"), tidak membatasi fokus di dialog, tidak ada penanganan Back, tidak memulihkan fokus saat halaman di-render ulang. Diganti oleh `tv.js` (aktif hanya di TV); kode lama tidak dihapus.
5. **Manifest:** `id` bernilai domain lain (`belajarbermain.online`) sehingga tidak valid bila dibuka dari `github.io`; `orientation:"any"` sudah tepat untuk HP. Tidak diubah (mengubah `id` mengubah identitas PWA yang sudah terpasang).
6. **Service Worker:** cache-first dengan nama versi; pembaruan = ganti `V`. Sudah benar secara umum; dinaikkan ke `bba-v22` dan `tv.css`/`tv.js` ditambahkan ke daftar pra-cache. Musik latar 1,4 MB baru ter-cache setelah diputar pertama kali (sengaja, hemat data).
7. **Kompatibilitas WebView lama:** 7 pemakaian `color-mix()` di `style.css` (Chrome 111+), satu dengan `@supports` fallback. Degradasi anggun, tetapi cek versi WebView TV.
8. **Performa:** sudah ringan (tanpa blur, tanpa library). Di TV ditambah: kilau banner dimatikan, hanya `transform` yang dianimasikan, kursor disembunyikan. Tidak ada video; tidak ada gambar tambahan yang dimuat.
9. **Tautan keluar:** footer (`benyoriki.com`), banner cerita (situs lain). Di aplikasi: tautan non-allowlist diblokir; tautan footer tak bisa difokus remote.
10. **Foto profil** memakai `<input type=file>`: di WebView TV tidak berfungsi (tanpa `onShowFileChooser`), jadi tombol kamera disembunyikan di TV.

## Perubahan yang dibuat pada situs
`tv.css` (baru), `tv.js` (baru), `index.html` (+2 tag & 1 skrip deteksi TV di `<head>`), `service-worker.js` (cache v22), `README.md` (catatan v19.3). Berkas lain tidak disentuh; HP/PC tidak berubah (diuji).
