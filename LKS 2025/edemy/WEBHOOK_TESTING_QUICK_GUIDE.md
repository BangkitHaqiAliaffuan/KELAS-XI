# 🔔 Quick Webhook Testing Guide

## Setup (One Time Only)

### 1. **Install Stripe CLI** (if not already installed)
```powershell
# Windows PowerShell (as Administrator)
choco install stripe-cli

# Or download from: https://github.com/stripe/stripe-cli/releases
```

### 2. **Login to Stripe**
```powershell
stripe login
```

## Testing Steps

### 3. **Start Server** (Terminal 1)
```powershell
cd "d:\KELAS-XI\LKS 2025\edemy\server"
npm start
```

### 4. **Start Stripe Webhook Listener** (Terminal 2)
```powershell
cd "d:\KELAS-XI\LKS 2025\edemy\server"
stripe listen --forward-to localhost:5000/api/webhooks/stripe
```

**Copy the webhook secret** from output (starts with `whsec_`)

### 5. **Update .env File**
Add the webhook secret to your `.env` file:
```env
STRIPE_WEBHOOK_SECRET=whsec_xxxxxxxxxxxxxxxxx
```

### 6. **Restart Server** (Terminal 1)
Stop server (Ctrl+C) and start again:
```powershell
npm start
```

## Trigger Tests (Terminal 3)

### Test requires_payment_method:
```powershell
stripe trigger payment_intent.created --add payment_intent:metadata[courseId]=course1 --add payment_intent:metadata[userEmail]=test@example.com --add payment_intent:amount=50000 --add payment_intent:status=requires_payment_method
```

### Test requires_action:
```powershell
stripe trigger payment_intent.requires_action --add payment_intent:metadata[courseId]=course1 --add payment_intent:metadata[userEmail]=test@example.com --add payment_intent:amount=50000
```

### Test successful payment:
```powershell
stripe trigger payment_intent.succeeded --add payment_intent:metadata[courseId]=course1 --add payment_intent:metadata[userEmail]=test@example.com --add payment_intent:amount=50000
```

### Test checkout completed:
```powershell
stripe trigger checkout.session.completed --add checkout_session:metadata[courseId]=course1 --add checkout_session:customer_email=test@example.com --add checkout_session:amount_total=50000
```

### Test payment failed:
```powershell
stripe trigger payment_intent.payment_failed --add payment_intent:metadata[courseId]=course1 --add payment_intent:metadata[userEmail]=test@example.com --add payment_intent:amount=50000
```

## Expected Server Output

### ✅ requires_payment_method:
```
🆕 Payment Intent created: pi_xxxxxxxxxxxxx
🔧 Status: requires_payment_method
💳 Processing payment that requires payment method...
📧 Customer email: test@example.com
🎯 Course ID: course1
💰 Amount: 50000 cents
ℹ️ Payment Intent created but requires payment method attachment
ℹ️ Waiting for customer to provide payment details
```

### ✅ requires_action:
```
⚠️ Payment requires action: pi_xxxxxxxxxxxxx
🔧 Status: requires_action
📧 Customer email: test@example.com
🎯 Course ID: course1
ℹ️ Payment requires additional authentication or verification
```

### ✅ succeeded (AUTO ENROLLMENT):
```
💰 Payment Intent succeeded: pi_xxxxxxxxxxxxx
📧 Customer email: test@example.com
💰 Amount: 50000 (dalam cents = $500.00)
🎯 Course ID: course1
👤 Finding user by email: test@example.com
✅ User found: [user_id]
📝 Creating enrollment...
✅ Enrollment created successfully
```

## Quick Test with UI

1. **Open Test Interface**: Open `payment-test.html` in browser
2. **Use Webhook Section**: Go to "🔔 Stripe Webhook Test" section
3. **Click Trigger Buttons**: Each button shows the CLI command to run
4. **Monitor Server Console**: Watch for webhook processing logs

## Troubleshooting

- **Webhook not received**: Check if Stripe CLI is running and forwarding to correct port
- **Signature verification failed**: Update webhook secret in .env and restart server
- **User not found**: Make sure user with test email exists in database
- **Course not found**: Make sure course with courseId exists in database

## Real Payment Testing

Use Stripe test cards in `payment-test.html`:
- **Success**: `4242 4242 4242 4242`
- **Decline**: `4000 0000 0000 0002`
- **3D Secure**: `4000 0025 0000 3155`

This will trigger real webhook events that you can monitor!
