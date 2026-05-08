/**
 * Categories Routes
 * API endpoints untuk mengelola kategori ruangan
 */

import express from 'express';
import {
  getAllCategories,
  getCategoryByName,
  categoryExists,
  getCategoryNames,
  upsertCategory,
  deleteCategory,
  getCategoryStats,
  validateCategory,
} from '../data/categories.js';

const router = express.Router();

/**
 * GET /api/v1/categories
 * Get all categories
 */
router.get('/', (req, res) => {
  try {
    const categories = getAllCategories();
    res.json({
      success: true,
      data: categories,
      count: categories.length,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to fetch categories',
      error: error.message,
    });
  }
});

/**
 * GET /api/v1/categories/names
 * Get category names only
 */
router.get('/names', (req, res) => {
  try {
    const names = getCategoryNames();
    res.json({
      success: true,
      data: names,
      count: names.length,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to fetch category names',
      error: error.message,
    });
  }
});

/**
 * GET /api/v1/categories/stats
 * Get category statistics
 */
router.get('/stats', (req, res) => {
  try {
    const stats = getCategoryStats();
    res.json({
      success: true,
      data: stats,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to fetch category statistics',
      error: error.message,
    });
  }
});

/**
 * GET /api/v1/categories/:name
 * Get category by name
 */
router.get('/:name', (req, res) => {
  try {
    const { name } = req.params;
    const category = getCategoryByName(name);

    if (!category) {
      return res.status(404).json({
        success: false,
        message: `Category '${name}' not found`,
      });
    }

    res.json({
      success: true,
      data: category,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to fetch category',
      error: error.message,
    });
  }
});

/**
 * POST /api/v1/categories
 * Create or update a category
 */
router.post('/', (req, res) => {
  try {
    const { name, description } = req.body;

    if (!name) {
      return res.status(400).json({
        success: false,
        message: 'Category name is required',
      });
    }

    const category = upsertCategory({ name, description });

    res.status(201).json({
      success: true,
      message: 'Category created/updated successfully',
      data: category,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to create/update category',
      error: error.message,
    });
  }
});

/**
 * PUT /api/v1/categories/:name
 * Update a category
 */
router.put('/:name', (req, res) => {
  try {
    const { name } = req.params;
    const { description } = req.body;

    if (!categoryExists(name)) {
      return res.status(404).json({
        success: false,
        message: `Category '${name}' not found`,
      });
    }

    const category = upsertCategory({ name, description });

    res.json({
      success: true,
      message: 'Category updated successfully',
      data: category,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to update category',
      error: error.message,
    });
  }
});

/**
 * DELETE /api/v1/categories/:name
 * Delete a category
 */
router.delete('/:name', (req, res) => {
  try {
    const { name } = req.params;

    const deleted = deleteCategory(name);

    if (!deleted) {
      return res.status(404).json({
        success: false,
        message: `Category '${name}' not found`,
      });
    }

    res.json({
      success: true,
      message: 'Category deleted successfully',
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to delete category',
      error: error.message,
    });
  }
});

/**
 * POST /api/v1/categories/validate
 * Validate a category name
 */
router.post('/validate', (req, res) => {
  try {
    const { name } = req.body;

    if (!name) {
      return res.status(400).json({
        success: false,
        message: 'Category name is required for validation',
      });
    }

    const validation = validateCategory(name);

    if (!validation.valid) {
      return res.status(400).json({
        success: false,
        message: validation.error,
      });
    }

    res.json({
      success: true,
      message: 'Category is valid',
      data: validation.category,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Failed to validate category',
      error: error.message,
    });
  }
});

export default router;
