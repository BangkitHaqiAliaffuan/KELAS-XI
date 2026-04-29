import express from 'express';
import {
  getAllRooms,
  getRoomById,
  getRoomsByCategory,
  getRoomsByFloor,
  searchRooms,
  getCategories,
  upsertRoom,
  deleteRoom,
} from '../data/hospitalRooms.js';

const router = express.Router();

/**
 * GET /api/v1/rooms
 * Get all rooms or filter by query parameters
 */
router.get('/', (req, res) => {
  try {
    const { category, floor, search } = req.query;

    let rooms;

    if (search) {
      rooms = searchRooms(search);
    } else if (category) {
      rooms = getRoomsByCategory(category);
    } else if (floor !== undefined) {
      rooms = getRoomsByFloor(Number(floor));
    } else {
      rooms = getAllRooms();
    }

    res.json({
      success: true,
      data: rooms,
      count: rooms.length,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * GET /api/v1/rooms/categories
 * Get all available categories
 */
router.get('/categories', (req, res) => {
  try {
    const categories = getCategories();
    res.json({
      success: true,
      data: categories,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * GET /api/v1/rooms/:id
 * Get room by ID
 */
router.get('/:id', (req, res) => {
  try {
    const room = getRoomById(req.params.id);
    
    if (!room) {
      return res.status(404).json({
        success: false,
        error: 'Room not found',
      });
    }

    res.json({
      success: true,
      data: room,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * POST /api/v1/rooms
 * Create or update a room
 */
router.post('/', (req, res) => {
  try {
    const room = upsertRoom(req.body);
    res.status(201).json({
      success: true,
      data: room,
      message: 'Room created/updated successfully',
    });
  } catch (error) {
    res.status(400).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * PUT /api/v1/rooms/:id
 * Update a room
 */
router.put('/:id', (req, res) => {
  try {
    const roomData = { ...req.body, id: req.params.id };
    const room = upsertRoom(roomData);
    res.json({
      success: true,
      data: room,
      message: 'Room updated successfully',
    });
  } catch (error) {
    res.status(400).json({
      success: false,
      error: error.message,
    });
  }
});

/**
 * DELETE /api/v1/rooms/:id
 * Delete a room
 */
router.delete('/:id', (req, res) => {
  try {
    const deleted = deleteRoom(req.params.id);
    
    if (!deleted) {
      return res.status(404).json({
        success: false,
        error: 'Room not found',
      });
    }

    res.json({
      success: true,
      message: 'Room deleted successfully',
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message,
    });
  }
});

export default router;
