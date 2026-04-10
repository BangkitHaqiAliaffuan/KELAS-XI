# Three.js Orbit Scene Switch Demo

Demo ini menampilkan:
- Kamera orbit (`OrbitControls`) pada model `city_orbit`
- Tanda seru (`!`) di atas salah satu gedung yang bisa diklik
- Saat tanda seru diklik, POV berpindah dari `city_orbit` ke model `Apartment`

## Struktur file
- `src/main.js` — scene, orbit camera, marker klik, dan perpindahan view
- `index.html` — entry halaman

## Jalankan
```bash
npm install
npm run dev
```

Lalu buka URL lokal dari Vite (biasanya `http://localhost:5173`).

## Kontrol
- Klik + drag mouse: orbit kamera
- Scroll mouse: zoom in/out
- Klik tanda seru di atas gedung: pindah POV ke `Apartment`
- Klik tombol `Back to City`: kembali ke POV `city_orbit`

## Aset yang dipakai
- `assets/city_orbit/source/Project.glb`
- `assets/Apartment/Apartment 2.glb`
