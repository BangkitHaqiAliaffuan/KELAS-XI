<?php

use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return view('welcome');
});

Route::get('/payment/return', function () {
        $orderId = request()->query('orderId');
        $cartCheckoutId = request()->query('cartCheckoutId');

        $scheme = trim((string) config('app.mobile_deeplink_scheme', 'trashcare://payment/success'));
        if ($scheme === '') {
                $scheme = 'trashcare://payment/success';
        }

        $query = [];
        if (!empty($orderId)) {
                $query['orderId'] = $orderId;
        }
        if (!empty($cartCheckoutId)) {
                $query['cartCheckoutId'] = $cartCheckoutId;
        }

        $deepLink = $scheme . (!empty($query) ? ('?' . http_build_query($query)) : '');
        $escapedDeepLink = e($deepLink);

        return response(<<<HTML
<!doctype html>
<html lang="id">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Pembayaran Berhasil</title>
    <style>
        :root { color-scheme: light; }
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f4f6f8;
            color: #1f2937;
        }
        .wrap {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            box-sizing: border-box;
        }
        .card {
            width: 100%;
            max-width: 440px;
            background: #ffffff;
            border-radius: 14px;
            padding: 24px;
            box-shadow: 0 10px 24px rgba(0, 0, 0, .08);
            text-align: center;
        }
        .badge {
            width: 64px;
            height: 64px;
            margin: 0 auto 14px;
            border-radius: 999px;
            background: #dcfce7;
            color: #15803d;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 34px;
            font-weight: 700;
        }
        h1 {
            margin: 0 0 10px;
            font-size: 24px;
            color: #15803d;
        }
        p {
            margin: 0;
            line-height: 1.6;
            color: #374151;
        }
        .hint {
            margin-top: 14px;
            padding: 12px;
            border-radius: 10px;
            background: #eff6ff;
            color: #1d4ed8;
            font-size: 14px;
        }
        .actions {
            margin-top: 18px;
            display: grid;
            gap: 10px;
        }
        .btn {
            appearance: none;
            border: 0;
            border-radius: 10px;
            padding: 12px 14px;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
        }
        .btn-primary {
            background: #2563eb;
            color: #ffffff;
        }
        .btn-secondary {
            background: #e5e7eb;
            color: #111827;
        }
    </style>
</head>
<body>
    <div class="wrap">
        <main class="card">
            <div class="badge">✓</div>
            <h1>Pembayaran Berhasil</h1>
            <p>Terima kasih, pembayaran kamu sudah kami terima.</p>
            <p style="margin-top:8px;"><strong>Kamu sekarang bisa menutup halaman web ini</strong> dan kembali ke aplikasi.</p>
            <div class="hint">Jika aplikasi belum terbuka otomatis, tekan tombol <em>Kembali ke Aplikasi</em> di bawah.</div>
            <div class="actions">
                <button class="btn btn-primary" id="backToApp">Kembali ke Aplikasi</button>
                <button class="btn btn-secondary" id="closePage">Tutup Halaman</button>
            </div>
        </main>
    </div>
    <script>
        (function () {
            var deepLink = "{$escapedDeepLink}";
            var backButton = document.getElementById('backToApp');
            var closeButton = document.getElementById('closePage');

            if (backButton) {
                backButton.addEventListener('click', function () {
                    window.location.href = deepLink;
                });
            }

            if (closeButton) {
                closeButton.addEventListener('click', function () {
                    window.close();
                });
            }
        })();
    </script>
</body>
</html>
HTML, 200)->header('Content-Type', 'text/html; charset=UTF-8');
})->name('payment.return');
