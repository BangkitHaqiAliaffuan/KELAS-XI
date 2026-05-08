import express from 'express';
import roomsRouter from './rooms.js';
import qrAnchorsRouter from './qrAnchors.js';
import categoriesRouter from './categories.js';

const router = express.Router();

// Health check endpoint
router.get('/health', (req, res) => {
  res.json({
    success: true,
    message: 'Hospital Navigator API is running',
    timestamp: new Date().toISOString(),
  });
});

// API routes
router.use('/rooms', roomsRouter);
router.use('/qr-anchors', qrAnchorsRouter);
router.use('/categories', categoriesRouter);

export default router;
