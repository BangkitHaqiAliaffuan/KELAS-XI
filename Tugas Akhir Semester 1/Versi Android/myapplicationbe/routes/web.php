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
</head>
<body style="font-family:Arial,sans-serif;text-align:center;padding:32px;">
    <h2>Pembayaran berhasil</h2>
    <p>Mengembalikan ke aplikasi...</p>
    <script>
        (function () {
            var deepLink = "{$escapedDeepLink}";

            function backToApp() {
                window.location.href = deepLink;
            }

            backToApp();

            setTimeout(function () {
                window.close();
            }, 300);

            setTimeout(function () {
                backToApp();
            }, 1000);
        })();
    </script>
</body>
</html>
HTML, 200)->header('Content-Type', 'text/html; charset=UTF-8');
})->name('payment.return');
