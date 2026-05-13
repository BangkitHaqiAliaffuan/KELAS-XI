Berikut ini adalah Bug bug yang terjadi saat membuat fitur baru

BUG 1
CEK KHUSUS END POINT YANG DEKAT AREA LIFT / TANGGA UTAMA
(Bukan tangga evakuasi karena itu sudah benar)



Coba test dari lantai 1 menuju Lobby Lantai 2.
Sepertinya di akses lift dan tangga utama masih ada bug.

Masalahnya: setelah user keluar dari lift atau tangga utama, step pertama yang terdeteksi malah step lurus koridor. Karena sistem mencari belokan pertama dari seluruh route, teks:  Belok kanan/kiri (setelah keluar dari lift/tangga)

malah pindah ke step kedua atau ke belokan yang letaknya jauh di depan.

Padahal seharusnya:

instruksi “Belok kanan/kiri (setelah keluar dari …)” muncul langsung di awal setelah keluar lift/tangga,

lalu step berikutnya baru instruksi koridor lurus.


Akibat bug sekarang: step kedua yang ada tulisan: Belok ... (setelah keluar dari ...)



malah mendefinisikan belokan yang sebenarnya berada jauh dari area lift/tangga, jadi konteksnya salah.

Yang benar alurnya:

1. Belok kanan/kiri (setelah keluar dari lift/tangga)


2. Lurus mengikuti koridor utama / melewati beberapa persimpangan


3. Baru belokan berikutnya jadi instruksi normal tanpa embel-embel “setelah keluar dari ...”

Catatan: khusus koridor QR-F1-NO11 → QR-F1-NO12 tetap biarkan lurus seperti yang sekarang karena itu memang sudah benar.


=====================================

BUG 2
Kasus Koridor Vertikal Belum Bisa Mendeteksi Persimpangan



Masih ada masalah di koridor vertikal karena step lurus belum menghitung jumlah persimpangan yang dilewati.

Khusus: 

QR-F2-NO7 → QR-F2-NO6

QR-F1-NO7 → QR-F1-NO10


Saat ini sistem hanya menampilkan instruksi lurus biasa, padahal menurut masukan orang e, instruksi lurus harus punya patokan agar user yakin masih berada di jalur yang benar.

Sekarang:

koridor horizontal sudah bisa mendeteksi persimpangan dan menghasilkan instruksi seperti:

> Lurus melewati 2 persimpangan



tetapi koridor vertikal belum bisa melakukan hal yang sama.


Jadi perlu diperbaiki agar:

koridor vertikal juga menghitung node/cabang di sepanjang jalur,

lalu menampilkan jumlah persimpangan yang dilewati seperti pada koridor horizontal.


Tujuannya supaya instruksi lurus di koridor vertikal juga punya orientasi/patokan yang jelas untuk user.

Ini membantu buat ruangan seperti R. Training, dokter spesialis, ipsrs, mbek cssd lurusnya belum bisa didefinisi patokannya misal start point lahan parkir lantai 2 dan end point di cssd. Lalu buka lantai 1 pencet pencet step e sampe nemu lurus yang kedua, sebelum belok kiri, nah saiki ini itu masih didefinisi lurus tok tanpa patokan


===============================
BUG 3 

Semua End Point yang Lewat Jalur QR-F1-NO11 → QR-F1-NO12 dengan Start Point Lahan Parkir Lantai 2



Masalahnya mirip seperti kasus nomor 1.

Untuk semua rute yang start dari Lahan Parkir Lantai 2 dan melewati jalur QR-F1-NO11 → QR-F1-NO12, sistem masih salah dalam menempatkan konteks:

> Belok kanan/kiri (setelah keluar dari ...)



Saat ini setiap ada belokan di awal route, sistem langsung menganggap itu sebagai konteks keluar dari akses vertikal, padahal sebenarnya tidak selalu begitu.

Akibatnya: instruksi:

> Belok ... (setelah keluar dari ...)



muncul pada belokan yang tidak relevan dengan area keluar lift/tangga.

Yang benar:

konteks “setelah keluar dari ...” hanya digunakan untuk orientasi awal tepat setelah user keluar dari lift/tangga,

bukan untuk semua belokan pertama pada route.


Khusus jalur QR-F1-NO11 → QR-F1-NO12:

step lurus di area itu sudah benar,

jadi jangan dipaksa diberi konteks belok keluar lift/tangga lagi.


Intinya perlu dibedakan antara:

orientasi awal setelah keluar akses vertikal,

dengan belokan normal pada jalur navigasi.