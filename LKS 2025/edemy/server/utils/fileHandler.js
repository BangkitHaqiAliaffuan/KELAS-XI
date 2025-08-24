const fs = require('fs');
const path = require('path');

// Fungsi untuk menyimpan base64 image ke file
const saveBase64Image = (base64String, fileName) => {
  try {
    // Extract format dari base64 string
    const matches = base64String.match(/^data:image\/([a-zA-Z]+);base64,(.+)$/);
    if (!matches || matches.length !== 3) {
      throw new Error('Invalid base64 string');
    }

    const imageType = matches[1]; // jpeg, png, etc
    const imageData = matches[2];

    // Create uploads directory jika belum ada
    const uploadDir = path.join(__dirname, '..', 'uploads', 'course-thumbnails');
    if (!fs.existsSync(uploadDir)) {
      fs.mkdirSync(uploadDir, { recursive: true });
    }

    // Generate unique filename
    const uniqueFileName = `${fileName}-${Date.now()}.${imageType}`;
    const filePath = path.join(uploadDir, uniqueFileName);

    // Convert base64 to buffer dan save
    const buffer = Buffer.from(imageData, 'base64');
    fs.writeFileSync(filePath, buffer);

    // Return relative path untuk disimpan di database
    return `/uploads/course-thumbnails/${uniqueFileName}`;

  } catch (error) {
    console.error('Error saving image:', error);
    throw new Error('Failed to save image');
  }
};

// Fungsi untuk menghapus file
const deleteImage = (imagePath) => {
  try {
    if (imagePath && imagePath.startsWith('/uploads/')) {
      const fullPath = path.join(__dirname, '..', imagePath);
      if (fs.existsSync(fullPath)) {
        fs.unlinkSync(fullPath);
      }
    }
  } catch (error) {
    console.error('Error deleting image:', error);
  }
};

module.exports = {
  saveBase64Image,
  deleteImage
};
