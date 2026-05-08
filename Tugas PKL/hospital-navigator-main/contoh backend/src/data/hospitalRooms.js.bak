/**
 * Hospital Room Information Database
 * Data ruangan rumah sakit dengan informasi lengkap
 */

export const hospitalRooms = {
  IGD: {
    id: "IGD",
    name: "IGD",
    category: "Emergency",
    locationHint: "Sayap kiri bawah peta",
    description: "Instalasi Gawat Darurat untuk penanganan kondisi medis darurat 24 jam.",
    floor: 1,
  },
  Poliklinik: {
    id: "Poliklinik",
    name: "Poliklinik",
    category: "Outpatient",
    locationHint: "Area kanan bawah peta",
    description: "Layanan konsultasi rawat jalan berbagai poli spesialis.",
    floor: 1,
  },
  ICU: {
    id: "ICU",
    name: "ICU",
    category: "Critical Care",
    locationHint: "Tengah kiri peta",
    description: "Unit perawatan intensif untuk pasien dengan kondisi kritis.",
    floor: 1,
  },
  Lab: {
    id: "Lab",
    name: "Laboratorium",
    category: "Diagnostic",
    locationHint: "Bagian tengah peta",
    description: "Pemeriksaan laboratorium penunjang diagnosis pasien.",
    floor: 1,
  },
  Farmasi: {
    id: "Farmasi",
    name: "Farmasi",
    category: "Facility",
    locationHint: "Tengah bawah peta",
    description: "Pelayanan obat resep, informasi obat, dan konseling farmasi.",
    floor: 1,
  },
  Radiologi: {
    id: "Radiologi",
    name: "Radiologi",
    category: "Diagnostic",
    locationHint: "Tengah bawah-kiri peta",
    description: "Layanan pemeriksaan radiologi seperti X-Ray dan USG.",
    floor: 1,
  },
  Toilet: {
    id: "Toilet",
    name: "Toilet",
    category: "Facility",
    locationHint: "Tengah kanan bawah peta",
    description: "Fasilitas toilet umum untuk pengunjung dan pasien.",
    floor: 1,
  },
  Lift_Lantai_1: {
    id: "Lift_Lantai_1",
    name: "Lift Lantai 1",
    category: "Facility",
    locationHint: "Koridor tengah-kanan peta",
    description: "Akses lift untuk perpindahan antar lantai rumah sakit.",
    floor: 1,
  },
  Tangga_Lantai_1: {
    id: "Tangga_Lantai_1",
    name: "Tangga Lantai 1",
    category: "Facility",
    locationHint: "Koridor tengah-kanan peta",
    description: "Akses tangga untuk perpindahan antar lantai rumah sakit.",
    floor: 1,
  },
  Informasi: {
    id: "Informasi",
    name: "Informasi",
    category: "Service",
    locationHint: "Dekat area IGD",
    description: "Meja informasi untuk bantuan arah ruangan dan layanan rumah sakit.",
    floor: 1,
  },
  Parking_Lantai_1: {
    id: "Parking_Lantai_1",
    name: "Lahan Parkir",
    category: "Facility",
    locationHint: "Area parkir kendaraan rumah sakit",
    description: "Area parkir kendaraan pengunjung dan tenaga medis. Tersedia parkir sepeda motor dan mobil.",
    floor: 0,
  },
  Parking_Lantai_2: {
    id: "Parking_Lantai_2",
    name: "Lahan Parkir Lantai 2",
    category: "Facility",
    locationHint: "Area parkir lantai 2 rumah sakit",
    description: "Area parkir lantai 2 dengan akses langsung ke gedung rumah sakit lantai 2 melalui jembatan penghubung.",
    floor: -1,
  },
  // Lantai 2 rooms
  Lift_Lantai_2: {
    id: "Lift_Lantai_2",
    name: "Lift Lantai 2",
    category: "Facility",
    locationHint: "Koridor tengah-kanan peta lantai 2",
    description: "Akses lift untuk perpindahan antar lantai rumah sakit.",
    floor: 2,
  },
  Tangga_Lantai_2: {
    id: "Tangga_Lantai_2",
    name: "Tangga Lantai 2",
    category: "Facility",
    locationHint: "Koridor tengah-kanan peta lantai 2",
    description: "Akses tangga untuk perpindahan antar lantai rumah sakit.",
    floor: 2,
  },
  Edukasi_Pasien_dan_Keluarga: {
    id: "Edukasi_Pasien_dan_Keluarga",
    name: "Edukasi Pasien dan Keluarga",
    category: "Service",
    locationHint: "Lantai 2",
    description: "Area edukasi kesehatan untuk pasien dan keluarga.",
    floor: 2,
  },
  Lobby_Lantai_2: {
    id: "Lobby_Lantai_2",
    name: "Lobby Lantai 2",
    category: "Facility",
    locationHint: "Area depan lantai 2",
    description: "Area lobi lantai 2.",
    floor: 2,
  },
};

/**
 * Get all rooms
 */
export const getAllRooms = () => {
  return Object.values(hospitalRooms);
};

/**
 * Get room by ID
 */
export const getRoomById = (roomId) => {
  return hospitalRooms[roomId] || null;
};

/**
 * Get rooms by category
 */
export const getRoomsByCategory = (category) => {
  return Object.values(hospitalRooms).filter(
    (room) => room.category.toLowerCase() === category.toLowerCase()
  );
};

/**
 * Get rooms by floor
 */
export const getRoomsByFloor = (floor) => {
  return Object.values(hospitalRooms).filter((room) => room.floor === floor);
};

/**
 * Search rooms by name or description
 */
export const searchRooms = (query) => {
  const lowerQuery = query.toLowerCase();
  return Object.values(hospitalRooms).filter(
    (room) =>
      room.name.toLowerCase().includes(lowerQuery) ||
      room.description.toLowerCase().includes(lowerQuery) ||
      room.id.toLowerCase().includes(lowerQuery)
  );
};

/**
 * Get available categories
 */
export const getCategories = () => {
  const categories = new Set();
  Object.values(hospitalRooms).forEach((room) => {
    categories.add(room.category);
  });
  return Array.from(categories).sort();
};

/**
 * Add or update a room
 */
export const upsertRoom = (roomData) => {
  const { id, name, category, locationHint, description, floor } = roomData;
  
  if (!id || !name || !category) {
    throw new Error('Room must have id, name, and category');
  }

  hospitalRooms[id] = {
    id,
    name,
    category,
    locationHint: locationHint || '',
    description: description || '',
    floor: floor || 1,
  };

  return hospitalRooms[id];
};

/**
 * Delete a room
 */
export const deleteRoom = (roomId) => {
  if (!hospitalRooms[roomId]) {
    return false;
  }
  delete hospitalRooms[roomId];
  return true;
};

export default {
  getAllRooms,
  getRoomById,
  getRoomsByCategory,
  getRoomsByFloor,
  searchRooms,
  getCategories,
  upsertRoom,
  deleteRoom,
};
