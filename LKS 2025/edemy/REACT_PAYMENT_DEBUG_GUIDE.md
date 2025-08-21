# 🧪 React Payment Debug System

## 📖 Overview

Sistem payment debugging terintegrasi dengan React, Clerk, dan MongoDB yang menyediakan 2 mode testing:

1. **Real Payment Flow Test** - Simulasi webhook processing tanpa Stripe actual
2. **Complete Stripe Payment** - Testing dengan Stripe checkout real menggunakan test cards

## 🚀 Getting Started

### 1. **Enable Debug Mode**

Ada 3 cara untuk mengaktifkan debug mode:

#### Option A: Development Environment
```bash
npm run dev  # Debug mode otomatis aktif di development
```

#### Option B: URL Parameter
```
http://localhost:3000/course/[course-id]?debug=true
```

#### Option C: Local Storage
```javascript
// Di browser console
localStorage.setItem('paymentDebugMode', 'true');
```

### 2. **Access Debug Panel**

#### Via Course Page:
1. Buka halaman course detail
2. Klik tombol **"🧪 Debug Payment System"**
3. Panel debug akan muncul sebagai modal

#### Via Dedicated Page:
```
http://localhost:3000/payment/debug
```

## 🎯 Mode Testing

### 1. **Real Payment Flow Test**

**Apa yang dilakukan:**
- ✅ Simulasi webhook `payment_intent.succeeded`
- ✅ Menggunakan data user real dari Clerk
- ✅ Membuat enrollment real di MongoDB
- ✅ Tidak perlu pembayaran actual
- ✅ Testing instant tanpa Stripe checkout

**Cara menggunakan:**
1. Pilih course dari dropdown
2. Pilih user email dari database
3. Klik **"🚀 Test Complete Payment Flow"**
4. System akan mensimulasi webhook processing
5. Check enrollment status untuk melihat hasil

**Use Case:**
- Testing logika webhook processing
- Debugging enrollment creation
- Quick testing tanpa pembayaran real

### 2. **Complete Stripe Payment**

**Apa yang dilakukan:**
- ✅ Membuat Stripe checkout session real
- ✅ Redirect ke Stripe hosted page
- ✅ Testing dengan Stripe test cards
- ✅ Trigger webhook real dari Stripe
- ✅ End-to-end payment flow

**Cara menggunakan:**
1. Pilih course dan pastikan login dengan Clerk
2. Klik **"Create Checkout Session"**
3. Klik **"Open Stripe Checkout"**
4. Gunakan test card: `4242 4242 4242 4242`
5. Complete payment di Stripe
6. Webhook real akan trigger enrollment

**Test Cards:**
```
Success: 4242 4242 4242 4242
Decline: 4000 0000 0000 0002
3D Secure: 4000 0025 0000 3155
Insufficient Funds: 4000 0000 0000 9995
```

**Use Case:**
- Testing complete Stripe integration
- Webhook verification
- Real user experience testing

## 🔧 Technical Integration

### Clerk Integration

```javascript
const { user, isLoaded } = useUser();

// Auto-populate user data
useEffect(() => {
  if (isLoaded && user) {
    setTestData(prev => ({
      ...prev,
      userId: user.id,
      userEmail: user.primaryEmailAddress?.emailAddress || ''
    }));
  }
}, [isLoaded, user]);
```

### MongoDB Integration

```javascript
// Load real users from database
const loadAvailableUsers = async () => {
  const result = await apiRequest('/api/users');
  if (result.success && result.data) {
    setAvailableUsers(result.data);
  }
};

// Load real courses from database
const loadAvailableCourses = async () => {
  const result = await apiRequest('/api/courses');
  if (result.success && result.data.courses) {
    setAvailableCourses(result.data.courses);
  }
};
```

### Stripe Integration

```javascript
// Real payment flow test
const testRealPaymentFlow = async () => {
  const result = await apiRequest('/api/payments/simulate-real-payment', {
    method: 'POST',
    body: JSON.stringify({
      courseId: courseId,
      userEmail: userEmail,
      amount: 50000 // $500.00
    })
  });
};

// Complete Stripe checkout
const createStripeCheckoutSession = async () => {
  const result = await apiRequest('/api/payments/create-checkout-session', {
    method: 'POST',
    body: JSON.stringify({
      courseId,
      userId,
      userEmail,
      successUrl: `${window.location.origin}/payment/success`,
      cancelUrl: `${window.location.origin}/course/${courseId}`
    })
  });
};
```

## 🎨 Component Structure

```
src/
├── components/
│   ├── PaymentDebugPanel.jsx     # Main debug interface
│   ├── PaymentDebugPanel.css     # Styling
│   └── PaymentButton.jsx         # Updated with debug mode
├── pages/
│   ├── PaymentTestPage.jsx       # Standalone debug page
│   └── CourseDetail.jsx          # Updated with debug access
└── App.jsx                       # Added debug route
```

## 🔐 Security Features

### Access Control
- ✅ Hanya aktif di development mode
- ✅ Memerlukan URL parameter atau localStorage flag
- ✅ Redirect jika tidak authorized
- ✅ Requires Clerk authentication

### Data Protection
- ✅ Hanya menggunakan test Stripe keys
- ✅ Real user data tapi safe environment
- ✅ Webhook signature verification tetap aktif
- ✅ Error handling untuk semua API calls

## 📋 Usage Examples

### Debug Mode dari Course Page
```javascript
// CourseDetail.jsx
<PaymentButton
  course={course}
  onSuccess={handlePaymentSuccess}
  onError={handlePaymentError}
  showDebugMode={isDebugMode}  // Auto-detect debug mode
/>
```

### Manual Debug Access
```javascript
// Enable debug via localStorage
localStorage.setItem('paymentDebugMode', 'true');

// Access debug page directly
window.location.href = '/payment/debug';
```

### Server Integration
```javascript
// Server endpoint untuk simulasi
app.post('/api/payments/simulate-real-payment', async (req, res) => {
  const { courseId, userEmail, amount } = req.body;
  
  // Simulate payment intent
  const simulatedPaymentIntent = {
    id: 'pi_simulated_' + Date.now(),
    amount: amount,
    metadata: { courseId, userEmail }
  };
  
  // Call real webhook handler
  await handleSuccessfulPayment(simulatedPaymentIntent);
});
```

## 🚨 Important Notes

1. **Environment Safety**: Debug mode hanya aktif di development
2. **Real Data**: Menggunakan user dan course data real dari database
3. **Test Payments**: Semua Stripe payments menggunakan test mode
4. **Webhook Testing**: Bisa simulasi atau real webhook dari Stripe
5. **Error Handling**: Semua API calls memiliki proper error handling

## 🎯 Benefits

- **Faster Testing**: Tidak perlu complete payment untuk test enrollment
- **Real Integration**: Testing dengan data real dari Clerk & MongoDB  
- **Comprehensive**: Cover semua scenario payment dari simulasi hingga real
- **User Friendly**: UI yang mudah digunakan untuk testing
- **Safe**: Semua proteksi keamanan tetap aktif

System ini memungkinkan testing payment flow yang complete dengan fleksibilitas antara simulasi cepat dan testing real Stripe integration!
