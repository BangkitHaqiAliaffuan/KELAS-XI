# Stripe CLI Testing Guide

## Setup Stripe CLI

### 1. **Install Stripe CLI** (Jika belum)
```bash
# Windows (via Chocolatey)
choco install stripe-cli

# Windows (via Scoop)
scoop install stripe

# Manual download dari: https://github.com/stripe/stripe-cli/releases
```

### 2. **Login ke Stripe Account**
```bash
stripe login
```
Ini akan membuka browser untuk authenticate dengan akun Stripe Anda.

### 3. **Forward Webhooks ke Local Server**
```bash
stripe listen --forward-to localhost:5000/api/webhooks/stripe
```

Output akan menunjukkan:
```
> Ready! Your webhook signing secret is whsec_xxxxxxxxxxxxx
> Listening for events on your Stripe account...
```

**PENTING**: Copy webhook signing secret (`whsec_xxxxxxxxxxxxx`) ini ke file `.env`:

## Testing dengan Stripe CLI

### 4. **Trigger Payment Events**

#### Payment Intent Events:
```bash
# Trigger payment_intent.succeeded event
stripe trigger payment_intent.succeeded

# Trigger payment_intent.created (requires_payment_method)
stripe trigger payment_intent.created

# Trigger payment_intent.requires_action (3D Secure)
stripe trigger payment_intent.requires_action

# Dengan custom data
stripe trigger payment_intent.succeeded \
  --add payment_intent:metadata[courseId]=course1 \
  --add payment_intent:metadata[userEmail]=test@example.com \
  --add payment_intent:amount=50000

# Trigger requires_payment_method dengan metadata
stripe trigger payment_intent.created \
  --add payment_intent:metadata[courseId]=course1 \
  --add payment_intent:metadata[userEmail]=test@example.com \
  --add payment_intent:amount=50000 \
  --add payment_intent:status=requires_payment_method
```

### 5. **Trigger Checkout Session Completed**
```bash
# Trigger checkout.session.completed event
stripe trigger checkout.session.completed

# Dengan custom data
stripe trigger checkout.session.completed \
  --add checkout_session:metadata[courseId]=course1 \
  --add checkout_session:customer_email=test@example.com \
  --add checkout_session:amount_total=50000
```

### 6. **Monitor Events**
Terminal pertama (`stripe listen`) akan menunjukkan:
```
2025-08-21 10:30:45  --> payment_intent.succeeded [evt_xxxxxxxxxxxxx]
2025-08-21 10:30:45  <-- [200] POST http://localhost:5000/api/webhooks/stripe [evt_xxxxxxxxxxxxx]
```

## Expected Server Response

### Payment Intent Created (requires_payment_method):
```
🆕 Payment Intent created: pi_xxxxxxxxxxxxx
🔧 Status: requires_payment_method
📧 Customer email: test@example.com
🎯 Course ID: course1
💰 Amount: 50000 cents
💳 Processing payment that requires payment method...
ℹ️ Payment Intent created but requires payment method attachment
ℹ️ Waiting for customer to provide payment details
```

### Payment Intent Requires Action:
```
⚠️ Payment requires action: pi_xxxxxxxxxxxxx
🔧 Status: requires_action
📧 Customer email: test@example.com
🎯 Course ID: course1
⚡ Next action: [action details]
ℹ️ Payment requires additional authentication or verification
```

### Payment Intent Succeeded:
```
🔔 Received Stripe webhook: payment_intent.succeeded
📧 Customer email: test@example.com
💰 Amount: 50000 (dalam cents = $500.00)
🆔 Payment Intent ID: pi_xxxxxxxxxxxxx
🎯 Course ID: course1
👤 Finding user by email: test@example.com
✅ User found: [user_id]
📝 Creating enrollment...
✅ Enrollment created successfully
```

## Advanced Testing Commands

### Test Specific Payment Amounts
```bash
# Test dengan amount berbeda
stripe trigger payment_intent.succeeded \
  --add payment_intent:amount=99900 \
  --add payment_intent:metadata[courseId]=course2
```

### Test Different Customer Emails
```bash
# Test dengan email berbeda
stripe trigger payment_intent.succeeded \
  --add payment_intent:metadata[userEmail]=student@example.com \
  --add payment_intent:metadata[courseId]=course1
```

### Test Multiple Events
```bash
# Trigger multiple events sekaligus
stripe trigger payment_intent.succeeded payment_intent.payment_failed
```

## Troubleshooting

### Jika Webhook Tidak Diterima:
1. **Check server berjalan**: Pastikan server di `localhost:5000`
2. **Check endpoint**: Pastikan route `/api/webhooks/stripe` exists
3. **Check signing secret**: Update `.env` dengan secret dari `stripe listen`

### Jika Error 401/403:
```bash
# Re-authenticate
stripe login
stripe listen --forward-to localhost:5000/api/webhooks/stripe
```

### Debug Webhook Payload:
```bash
# Lihat raw webhook payload
stripe listen --forward-to localhost:5000/api/webhooks/stripe --print-json
```

## Quick Test Workflow

1. **Terminal 1**: Start server
```bash
cd "d:\KELAS-XI\LKS 2025\edemy\server"
npm start
```

2. **Terminal 2**: Start Stripe CLI listener
```bash
stripe listen --forward-to localhost:5000/api/webhooks/stripe
```

3. **Terminal 3**: Trigger events
```bash
stripe trigger payment_intent.succeeded \
  --add payment_intent:metadata[courseId]=course1 \
  --add payment_intent:metadata[userEmail]=test@example.com
```

## Live Payment Testing

Untuk test dengan real payment flow:
```bash
# Create test payment intent
stripe payment_intents create \
  --amount=50000 \
  --currency=usd \
  --metadata[courseId]=course1 \
  --metadata[userEmail]=test@example.com
```

## Environment Setup Check

Pastikan file `.env` memiliki:
```env
# Stripe Keys
STRIPE_PUBLISHABLE_KEY=pk_test_xxxxxxxxxxxxx
STRIPE_SECRET_KEY=sk_test_xxxxxxxxxxxxx
STRIPE_WEBHOOK_SECRET=whsec_xxxxxxxxxxxxx  # Dari stripe listen

# Database
MONGODB_URI=mongodb://localhost:27017/edemy

# Server
PORT=5000
```

Dengan Stripe CLI, Anda bisa test webhook secara real-time tanpa perlu melakukan pembayaran actual!
