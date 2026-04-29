import express from 'express';
import {
  getAllQrAnchors,
  getQrAnchorById,
  getQrAnchorsByRoomId,
  getQrAnchorsByFloor,
  searchQrAnchors,
  resolveQrCode,
  upsertQrAnchor,
  deleteQrAnchor,
  getQrAnchorStats,
} from '../data/qrAnchors.js';

const router = express.Router();

/**
 * GET /api/v1/qr-anchors
 * Get all QR anchors or filter by query parameters
 */
router.get('/', (req, res) => {
  try {
    const { roomId, floor, search } = req.query;

    let anchors;

    if (search) {
      anchors = searchQrAnchors(search);
    } else if (roomId) {
      anchors = getQrAnchorsByRoomId(roomId);
    } else if (floor !== undefined) {
      anchors = getQrAnchorsByFloor(Number(floor));
    } else {
      anchors = getAllQrAnchors();
    }

    res.json({
      success: true,
      data: anchors,
      count: anchors.length,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * GET /api/v1/qr-anchors/stats
 * Get QR anchor statistics
 */
router.get('/stats', (req, res) => {
  try {
    const stats = getQrAnchorStats();
    res.json({
      success: true,
      data: stats,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * POST /api/v1/qr-anchors/resolve
 * Resolve a QR code to anchor data
 */
router.post('/resolve', (req, res) => {
  try {
    const { qrCode } = req.body;
    
    if (!qrCode) {
      return res.status(400).json({
        success: false,
        error: 'QR code is required',
      });
    }

    const anchor = resolveQrCode(qrCode);
    
    if (!anchor) {
      return res.status(404).json({
        success: false,
        error: 'QR code not found',
      });
    }

    res.json({
      success: true,
      data: anchor,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * GET /api/v1/qr-anchors/:qrId
 * Get QR anchor by QR ID
 */
router.get('/:qrId', (req, res) => {
  try {
    const anchor = getQrAnchorById(req.params.qrId);
    
    if (!anchor) {
      return res.status(404).json({
        success: false,
        error: 'QR anchor not found',
      });
    }

    res.json({
      success: true,
      data: anchor,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * POST /api/v1/qr-anchors
 * Create or update a QR anchor
 */
router.post('/', (req, res) => {
  try {
    const anchor = upsertQrAnchor(req.body);
    res.status(201).json({
      success: true,
      data: anchor,
      message: 'QR anchor created/updated successfully',
    });
  } catch (error) {
    res.status(400).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * PUT /api/v1/qr-anchors/:qrId
 * Update a QR anchor
 */
router.put('/:qrId', (req, res) => {
  try {
    const anchorData = { ...req.body, qrId: req.params.qrId };
    const anchor = upsertQrAnchor(anchorData);
    res.json({
      success: true,
      data: anchor,
      message: 'QR anchor updated successfully',
    });
  } catch (error) {
    res.status(400).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * DELETE /api/v1/qr-anchors/:qrId
 * Delete a QR anchor
 */
router.delete('/:qrId', (req, res) => {
  try {
    const deleted = deleteQrAnchor(req.params.qrId);
    
    if (!deleted) {
      return res.status(404).json({
        success: false,
        error: 'QR anchor not found',
      });
    }

    res.json({
      success: true,
      message: 'QR anchor deleted successfully',
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

export default router;
