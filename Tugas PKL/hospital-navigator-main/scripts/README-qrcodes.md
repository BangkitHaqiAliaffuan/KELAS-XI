# QR Code Generator

Script: `scripts/generate-qrcodes.ts`

Generator ini membuat PNG QR untuk:
- Semua ruangan (`QR-R-<ROOM_ID>`) dari `roomInfoBySvgId`
- Semua titik QR anchor dari `QR_ANCHOR_REGISTRY`

## Perintah Utama

```bash
npm run qr:generate
```

## Variasi

```bash
npm run qr:generate:rooms
npm run qr:generate:anchors
npm run qr:generate -- --clean
npm run qr:generate -- --size 768 --margin 1
npm run qr:generate -- --outDir public/images/qr-floor1
```

## Opsi CLI

- `--include <rooms|anchors|all>`: pilih jenis QR yang digenerate (default `all`)
- `--outDir <path>`: lokasi output (default `public/images/qr`)
- `--size <number>`: ukuran PNG dalam px (default `512`)
- `--margin <number>`: margin QR (default `2`)
- `--clean`: hapus semua PNG lama di folder output sebelum generate

## Output

Di folder output akan dibuat:
- `room_<ROOM_ID>.png`
- `anchor_<QR_ID>.png`
- `index.json` (metadata lengkap hasil generate)
