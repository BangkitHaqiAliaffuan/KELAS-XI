/**
 * Categories Database
 * Master data kategori ruangan rumah sakit
 * Sesuai dengan ERD: CATEGORIES table
 */

export const categories = {
  Emergency: {
    name: "Emergency",
    description: "Layanan gawat darurat dan penanganan kondisi medis kritis yang memerlukan tindakan segera 24 jam",
  },
  Outpatient: {
    name: "Outpatient",
    description: "Layanan konsultasi dan pemeriksaan rawat jalan untuk berbagai spesialisasi medis",
  },
  "Critical Care": {
    name: "Critical Care",
    description: "Unit perawatan intensif untuk pasien dengan kondisi kritis yang memerlukan monitoring ketat",
  },
  Diagnostic: {
    name: "Diagnostic",
    description: "Layanan pemeriksaan penunjang diagnosis seperti laboratorium, radiologi, dan pencitraan medis",
  },
  Facility: {
    name: "Facility",
    description: "Fasilitas umum dan pendukung operasional rumah sakit seperti toilet, lift, tangga, dan parkir",
  },
  Service: {
    name: "Service",
    description: "Layanan pendukung medis dan non-medis seperti informasi, edukasi, dan konsultasi",
  },
  Administration: {
    name: "Administration",
    description: "Area administrasi, manajemen, dan tata kelola rumah sakit",
  },
  Surgery: {
    name: "Surgery",
    description: "Area tindakan operasi dan prosedur bedah dengan kontrol sterilitas tinggi",
  },
  Treatment: {
    name: "Treatment",
    description: "Ruang terapi dan tindakan medis khusus seperti hemodialisa, fisioterapi, dan rehabilitasi",
  },
  Ward: {
    name: "Ward",
    description: "Ruang rawat inap untuk perawatan pasien dengan berbagai kelas dan spesialisasi",
  },
};

/**
 * Get all categories
 */
export const getAllCategories = () => {
  return Object.values(categories);
};

/**
 * Get category by name
 */
export const getCategoryByName = (name) => {
  return categories[name] || null;
};

/**
 * Check if category exists
 */
export const categoryExists = (name) => {
  return categories.hasOwnProperty(name);
};

/**
 * Get category names only
 */
export const getCategoryNames = () => {
  return Object.keys(categories).sort();
};

/**
 * Add or update a category
 */
export const upsertCategory = (categoryData) => {
  const { name, description } = categoryData;
  
  if (!name) {
    throw new Error('Category must have a name');
  }

  categories[name] = {
    name,
    description: description || '',
  };

  return categories[name];
};

/**
 * Delete a category
 */
export const deleteCategory = (name) => {
  if (!categories[name]) {
    return false;
  }
  delete categories[name];
  return true;
};

/**
 * Get statistics
 */
export const getCategoryStats = () => {
  return {
    total: Object.keys(categories).length,
    categories: getCategoryNames(),
  };
};

/**
 * Validate category name
 */
export const validateCategory = (categoryName) => {
  if (!categoryName) {
    return {
      valid: false,
      error: 'Category name is required',
    };
  }

  if (!categoryExists(categoryName)) {
    return {
      valid: false,
      error: `Category '${categoryName}' does not exist. Available categories: ${getCategoryNames().join(', ')}`,
    };
  }

  return {
    valid: true,
    category: categories[categoryName],
  };
};

export default {
  getAllCategories,
  getCategoryByName,
  categoryExists,
  getCategoryNames,
  upsertCategory,
  deleteCategory,
  getCategoryStats,
  validateCategory,
};
