// Contoh webhook untuk auto-assign role di server
// Endpoint: /api/webhooks/clerk
// Di server/routes/ buat file webhooks.js

const express = require('express');
const { Webhook } = require('svix');
const router = express.Router();

// Webhook untuk handle user.created event
router.post('/clerk', async (req, res) => {
  const WEBHOOK_SECRET = process.env.CLERK_WEBHOOK_SECRET;
  
  if (!WEBHOOK_SECRET) {
    throw new Error('Please add CLERK_WEBHOOK_SECRET to .env');
  }

  const headers = req.headers;
  const payload = JSON.stringify(req.body);

  const wh = new Webhook(WEBHOOK_SECRET);
  
  let evt;
  try {
    evt = wh.verify(payload, headers);
  } catch (err) {
    console.error('Webhook verification failed:', err.message);
    return res.status(400).json({ message: 'Webhook verification failed' });
  }

  // Handle user created event
  if (evt.type === 'user.created') {
    const { id, email_addresses } = evt.data;
    
    // Default role assignment logic
    let defaultRole = 'student';
    
    // Contoh: educator jika email mengandung kata 'teacher' atau 'edu'
    const email = email_addresses[0]?.email_address || '';
    if (email.includes('teacher') || email.includes('edu') || email.includes('instructor')) {
      defaultRole = 'educator';
    }
    
    try {
      // Update user metadata via Clerk Backend API
      const response = await fetch(`https://api.clerk.com/v1/users/${id}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${process.env.CLERK_SECRET_KEY}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          public_metadata: {
            role: defaultRole
          }
        })
      });
      
      if (response.ok) {
        console.log(`User ${id} assigned role: ${defaultRole}`);
      }
    } catch (error) {
      console.error('Failed to assign role:', error);
    }
  }

  res.status(200).json({ message: 'Webhook processed successfully' });
});

module.exports = router;
