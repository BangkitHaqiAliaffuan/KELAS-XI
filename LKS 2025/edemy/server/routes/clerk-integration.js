const express = require('express');
const router = express.Router();
const User = require('../models/User');

// Clerk integration endpoint - Create/Update user from Clerk data
router.post('/sync-clerk-user', async (req, res) => {
  try {
    const { 
      clerkId, 
      email, 
      firstName, 
      lastName, 
      imageUrl, 
      role = 'student' 
    } = req.body;

    console.log('🔄 Syncing Clerk user:', { clerkId, email, firstName, lastName });

    if (!clerkId || !email) {
      return res.status(400).json({
        success: false,
        error: 'clerkId and email are required'
      });
    }

    // Check if user already exists
    let user = await User.findOne({ clerkId });

    if (user) {
      // Update existing user
      user.email = email;
      user.name = `${firstName || ''} ${lastName || ''}`.trim() || email.split('@')[0];
      user.imageUrl = imageUrl || user.imageUrl;
      user.updatedAt = new Date();
      
      await user.save();
      
      console.log('✅ User updated:', user.clerkId);
      
      return res.json({
        success: true,
        message: 'User updated successfully',
        user: {
          clerkId: user.clerkId,
          email: user.email,
          name: user.name,
          role: user.role
        }
      });
    } else {
      // Create new user
      user = new User({
        clerkId,
        email,
        name: `${firstName || ''} ${lastName || ''}`.trim() || email.split('@')[0],
        imageUrl: imageUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(firstName || email)}&background=667eea&color=fff`,
        role,
        createdAt: new Date(),
        updatedAt: new Date()
      });

      await user.save();
      
      console.log('✅ New user created:', user.clerkId);
      
      return res.json({
        success: true,
        message: 'User created successfully',
        user: {
          clerkId: user.clerkId,
          email: user.email,
          name: user.name,
          role: user.role
        }
      });
    }

  } catch (error) {
    console.error('❌ Error syncing Clerk user:', error);
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// Get user by Clerk ID
router.get('/clerk/:clerkId', async (req, res) => {
  try {
    const { clerkId } = req.params;
    
    const user = await User.findOne({ clerkId });
    
    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'User not found',
        clerkId
      });
    }

    res.json({
      success: true,
      user: {
        clerkId: user.clerkId,
        email: user.email,
        name: user.name,
        role: user.role,
        imageUrl: user.imageUrl,
        createdAt: user.createdAt
      }
    });

  } catch (error) {
    console.error('❌ Error fetching user by Clerk ID:', error);
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// Bulk sync multiple users (for migration)
router.post('/bulk-sync-users', async (req, res) => {
  try {
    const { users } = req.body;
    
    if (!Array.isArray(users)) {
      return res.status(400).json({
        success: false,
        error: 'users must be an array'
      });
    }

    const results = {
      created: 0,
      updated: 0,
      errors: []
    };

    for (const userData of users) {
      try {
        const { clerkId, email, firstName, lastName, imageUrl, role = 'student' } = userData;
        
        if (!clerkId || !email) {
          results.errors.push({ userData, error: 'Missing clerkId or email' });
          continue;
        }

        let user = await User.findOne({ clerkId });

        if (user) {
          // Update existing
          user.email = email;
          user.name = `${firstName || ''} ${lastName || ''}`.trim() || email.split('@')[0];
          user.imageUrl = imageUrl || user.imageUrl;
          user.updatedAt = new Date();
          await user.save();
          results.updated++;
        } else {
          // Create new
          user = new User({
            clerkId,
            email,
            name: `${firstName || ''} ${lastName || ''}`.trim() || email.split('@')[0],
            imageUrl: imageUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(firstName || email)}&background=667eea&color=fff`,
            role,
            createdAt: new Date(),
            updatedAt: new Date()
          });
          await user.save();
          results.created++;
        }

      } catch (error) {
        results.errors.push({ userData, error: error.message });
      }
    }

    res.json({
      success: true,
      message: 'Bulk sync completed',
      results
    });

  } catch (error) {
    console.error('❌ Error in bulk sync:', error);
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

module.exports = router;
