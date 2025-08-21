# Stripe Payment Testing Guide

## Langkah-langkah Test Pembayaran

### 1. **Setup Webhook (Jika Belum)**
- Masuk ke [Stripe Dashboard](https://dashboard.stripe.com/test/webhooks)
- Klik "Add endpoint"
- URL: `http://localhost:5000/api/webhooks/stripe`
- Events: pilih `payment_intent.succeeded` dan `checkout.session.completed`
- Copy webhook signing secret ke `.env` Anda

### 2. **Test via Payment Interface**
Buka file `payment-test.html` di browser untuk testing interface lengkap.

### 3. **Test Cards Stripe**
Gunakan test cards berikut:

#### ✅ **Cards yang BERHASIL** (akan trigger webhook)
- **Visa**: `4242 4242 4242 4242`
- **Visa (debit)**: `4000 0566 5566 5556`
- **Mastercard**: `5555 5555 5555 4444`
- **Amex**: `3782 8224 6310 005`

#### ❌ **Cards yang GAGAL** (untuk test error handling)
- **Generic decline**: `4000 0000 0000 0002`
- **Insufficient funds**: `4000 0000 0000 9995`
- **Lost card**: `4000 0000 0000 9987`

#### ⚠️ **Cards yang Butuh Autentikasi**
- **3D Secure**: `4000 0025 0000 3155`

### 4. **Data Test untuk Form**
- **Email**: test@example.com
- **Card Number**: Gunakan salah satu dari atas
- **Expiry**: Bulan/Tahun di masa depan (contoh: 12/25)
- **CVC**: 3 digit angka (contoh: 123)
- **Name**: Test User

### 5. **Monitoring Hasil**

#### Server Output yang Diharapkan:
```
🔔 Received Stripe webhook: payment_intent.succeeded
📧 Customer email: test@example.com
💰 Amount: 50000 (dalam cents = $500.00)
🆔 Payment Intent ID: pi_xxxxxxxxxxxxx
🎯 Course ID: course123
👤 Finding user by email: test@example.com
✅ User found: [user_id]
📝 Creating enrollment...
✅ Enrollment created successfully
```

#### Frontend Response yang Diharapkan:
```json
{
  "status": "success",
  "enrollment": {
    "userId": "user_id",
    "courseId": "course_id",
    "paymentStatus": "completed"
  }
}
```

### 6. **Troubleshooting**

#### Jika Webhook Tidak Masuk:
1. Pastikan server berjalan di `http://localhost:5000`
2. Check webhook URL di Stripe Dashboard
3. Pastikan ngrok/tunnel jika testing dari domain eksternal

#### Jika Payment Gagal:
1. Check API keys di `.env`
2. Pastikan course exists di database
3. Check server logs untuk error details

#### Jika Enrollment Tidak Terbuat:
1. Check user exists dengan email yang digunakan
2. Check course exists dengan courseId yang benar
3. Check database connection

### 7. **Manual Test Webhook**
Untuk test manual webhook tanpa pembayaran:

```bash
curl -X POST http://localhost:5000/api/payments/test-success \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "courseId": "course1",
    "amount": 50000
  }'
```

## Expected Workflow

1. **User clicks "Enroll Now"** → PaymentButton component
2. **Frontend calls** `/api/payments/create-checkout-session`
3. **Stripe Checkout opens** → User enters card details
4. **Payment succeeds** → Stripe sends webhook to `/api/webhooks/stripe`
5. **Server processes webhook** → Creates enrollment automatically
6. **User redirected** → PaymentSuccess page shows enrollment status

## Notes
- Semua pembayaran dalam mode test menggunakan Stripe sandbox
- Webhook signature verification aktif untuk keamanan
- Automatic enrollment hanya terjadi setelah payment confirmed
- Test cards tidak memerlukan real bank account
