/**
 * Hospital Room Information Database - COMPLETE VERSION
 * Data lengkap ruangan rumah sakit dengan informasi lengkap
 * Synchronized with frontend hospitalRoomInfo.ts
 */

import { validateCategory } from './categories.js';

export const hospitalRooms = {
  IGD: {
    id: "IGD",
    name: "IGD",
    category: "Emergency",
    locationHint: "Sayap kiri bawah peta",
    description: "Instalasi Gawat Darurat untuk penanganan kondisi medis darurat 24 jam.",
    floor: 1,
  },
  Area_Pelayanan_IGD: {
    id: "Area_Pelayanan_IGD",
    name: "Area Pelayanan IGD",
    category: "Emergency",
    locationHint: "Sayap kiri bawah peta",
    description: "Area pelayanan IGD untuk penanganan kondisi medis darurat 24 jam.",
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
  Rekam_Medis: {
    id: "Rekam_Medis",
    name: "Rekam Medis",
    category: "Administration",
    locationHint: "Tengah bawah-kanan peta",
    description: "Pengelolaan data rekam medis pasien dan administrasi dokumen.",
    floor: 1,
  },
  Kamar_Operasi: {
    id: "Kamar_Operasi",
    name: "Kamar Operasi",
    category: "Surgery",
    locationHint: "Sayap kiri tengah peta",
    description: "Area tindakan operasi dengan kontrol sterilitas tinggi.",
    floor: 1,
  },
  Area_Kamar_Operasi: {
    id: "Area_Kamar_Operasi",
    name: "Area Kamar Operasi",
    category: "Surgery",
    locationHint: "Sisi kiri tengah peta",
    description: "Area pendukung kamar operasi.",
    floor: 1,
  },
  Musholla: {
    id: "Musholla",
    name: "Musholla",
    category: "Facility",
    locationHint: "Sisi kanan peta",
    description: "Fasilitas ibadah untuk pasien, keluarga, dan tenaga medis.",
    floor: 1,
  },
  CSSD: {
    id: "CSSD",
    name: "CSSD",
    category: "Facility",
    locationHint: "Sayap kiri tengah peta",
    description: "Unit sterilisasi alat medis untuk kebutuhan tindakan klinis.",
    floor: 1,
  },
  "K._Mayat": {
    id: "K._Mayat",
    name: "K. Mayat",
    category: "Facility",
    locationHint: "Sisi kiri atas peta",
    description: "Area kamar jenazah untuk penanganan sesuai prosedur rumah sakit.",
    floor: 1,
  },
  "R._HD": {
    id: "R._HD",
    name: "R. HD",
    category: "Treatment",
    locationHint: "Lantai 1 - Sisi kiri atas peta",
    description: "Ruang hemodialisa untuk layanan cuci darah pasien.",
    floor: 1,
  },
  "R._Laundry": {
    id: "R._Laundry",
    name: "R. Laundry",
    category: "Facility",
    locationHint: "Lantai 1 - Sisi kiri atas peta",
    description: "Area laundry untuk pengelolaan linen dan kebutuhan kebersihan.",
    floor: 1,
  },
  "R._Internis": {
    id: "R._Internis",
    name: "R. Internis",
    category: "Outpatient",
    locationHint: "Lantai 1 - Sisi kanan atas peta",
    description: "Ruang layanan dokter penyakit dalam.",
    floor: 1,
  },
  "R._Pemeriksaan_Internis": {
    id: "R._Pemeriksaan_Internis",
    name: "R. Pemeriksaan Internis",
    category: "Outpatient",
    locationHint: "Lantai 1 - Sisi kanan atas peta",
    description: "area khusus di rumah sakit untuk menangani masalah kesehatan organ dalam",
    floor: 1,
  },
  "R._JKN": {
    id: "R._JKN",
    name: "R. JKN",
    category: "Administration",
    locationHint: "Lantai 1 - Sisi kanan atas peta",
    description: "Layanan administrasi dan informasi kepesertaan JKN.",
    floor: 1,
  },
  "R._Gizi": {
    id: "R._Gizi",
    name: "R. Gizi",
    category: "Service",
    locationHint: "Lantai 1 - Sisi atas tengah peta",
    description: "Unit gizi untuk konsultasi dan pengelolaan diet pasien.",
    floor: 1,
  },
  "R._Pinere": {
    id: "R._Pinere",
    name: "R. Pinere",
    category: "Ward",
    locationHint: "Lantai 1 - Sisi kiri atas-tengah peta",
    description: "Ruang perawatan isolasi sesuai protokol infeksius.",
    floor: 1,
  },
  "TRP._RJ": {
    id: "TRP._RJ",
    name: "TRP. RJ",
    category: "Service",
    locationHint: "Dekat area IGD",
    description: "Area transit/triase layanan rawat jalan.",
    floor: 1,
  },
  "R._Ponek": {
    id: "R._Ponek",
    name: "R. Ponek",
    category: "Emergency",
    locationHint: "Lantai 1 - Dekat area IGD",
    description: "Ruang layanan PONEK untuk kegawatdaruratan maternal-neonatal.",
    floor: 1,
  },
  "R._IPSRS": {
    id: "R._IPSRS",
    name: "R. IPSRS",
    category: "Facility",
    locationHint: "Lantai 1 - Sisi kiri atas-tengah peta",
    description: "Instalasi pemeliharaan sarana rumah sakit.",
    floor: 1,
  },
  "R._Kebidanan": {
    id: "R._Kebidanan",
    name: "R. Kebidanan",
    category: "Ward",
    locationHint: "Lantai 1 - Sisi atas tengah-kanan peta",
    description: "Ruang layanan kebidanan dan perawatan ibu.",
    floor: 1,
  },
  "R._Anak": {
    id: "R._Anak",
    name: "R. Anak",
    category: "Ward",
    locationHint: "Lantai 1 - Sisi atas kanan peta",
    description: "Ruang layanan dan perawatan pasien anak.",
    floor: 1,
  },
  "R._Rawat_Jantung": {
    id: "R._Rawat_Jantung",
    name: "R. Rawat Jantung",
    category: "Ward",
    locationHint: "Lantai 1 - Sisi kanan tengah peta",
    description: "Ruang rawat pasien dengan layanan kardiovaskular.",
    floor: 1,
  },
  "R._Bedah": {
    id: "R._Bedah",
    name: "R. Bedah",
    category: "Ward",
    locationHint: "Lantai 1 - Sisi kanan tengah peta",
    description: "Ruang perawatan pasien bedah.",
    floor: 1,
  },
  Rehab_Medik: {
    id: "Rehab_Medik",
    name: "Rehab Medik",
    category: "Service",
    locationHint: "Lantai 1 - Sisi kanan bawah peta",
    description: "Layanan rehabilitasi medik untuk pemulihan fungsi pasien.",
    floor: 1,
  },
  "R._Fisioterapi": {
    id: "R._Fisioterapi",
    name: "R. Fisioterapi",
    category: "Service",
    locationHint: "Lantai 1 - Sisi kanan bawah peta",
    description: "Ruang fisioterapi untuk terapi gerak dan pemulihan.",
    floor: 1,
  },
  Kantor_Administrasi: {
    id: "Kantor_Administrasi",
    name: "Kantor / Administrasi",
    category: "Administration",
    locationHint: "Sisi kanan bawah peta",
    description: "Area kantor dan layanan administrasi rumah sakit.",
    floor: 1,
  },
  "R._Tunggu": {
    id: "R._Tunggu",
    name: "R. Tunggu",
    category: "Facility",
    locationHint: "Lihat detail peta",
    description: "Ruang tunggu untuk keluarga pasien dan pengunjung.",
    floor: 1,
  },
  "R._Tunggu_Keluarga_Pasien": {
    id: "R._Tunggu_Keluarga_Pasien",
    name: "R. Tunggu Keluarga Pasien",
    category: "Facility",
    locationHint: "Sisi kanan bawah peta",
    description: "Ruang tunggu untuk keluarga pasien dan pengunjung.",
    floor: 1,
  },
  "Tangga_Evakuasi_Lantai_1": {
    id: "Tangga_Evakuasi_Lantai_1",
    name: "Tangga Evakuasi Lantai 1",
    category: "Facility",
    locationHint: "Sisi kiri atas peta",
    description: "Tangga Darurat",
    floor: 1,
  },
  // Lantai 2 rooms
  "Tangga_Evakuasi_Lantai_2": {
    id: "Tangga_Evakuasi_Lantai_2",
    name: "Tangga Evakuasi Lantai 2",
    category: "Facility",
    locationHint: "Sisi kiri atas peta",
    description: "Tangga Darurat",
    floor: 2,
  },
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
  "R._Korea": {
    id: "R._Korea",
    name: "R. Korea",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas 2",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas 2.",
    floor: 2,
  },
  "R._Jepang": {
    id: "R._Jepang",
    name: "R. Jepang",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas 2",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas 2.",
    floor: 2,
  },
  "R._Prancis": {
    id: "R._Prancis",
    name: "R. Prancis",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas 1",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas 1.",
    floor: 2,
  },
  "R._Italia": {
    id: "R._Italia",
    name: "R. Italia",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas 1",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas 1.",
    floor: 2,
  },
  "R._Inggris": {
    id: "R._Inggris",
    name: "R. Inggris",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas VIP",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas VIP.",
    floor: 2,
  },
  "R._Swiss": {
    id: "R._Swiss",
    name: "R. Swiss",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas VIP",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas VIP.",
    floor: 2,
  },
  Ruang_Indonesia: {
    id: "Ruang_Indonesia",
    name: "R. Indonesia",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas 3",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas 3.",
    floor: 2,
  },
  Ruang_Nusantara: {
    id: "Ruang_Nusantara",
    name: "R. Nusantara",
    category: "Ward",
    locationHint: "Lantai 2 - Ruang Rawat Inap Kelas 3",
    description: "Ruang perawatan lantai 2. Ruang Rawat Inap Kelas 3.",
    floor: 2,
  },
  Nurse_Station: {
    id: "Nurse_Station",
    name: "Nurse Station",
    category: "Service",
    locationHint: "Koridor utama lantai 2",
    description: "Pos perawat untuk koordinasi pelayanan pasien di lantai 2.",
    floor: 2,
  },
  "R._Dokter_Spesialis": {
    id: "R._Dokter_Spesialis",
    name: "R. Dokter Spesialis",
    category: "Outpatient",
    locationHint: "Lantai 2",
    description: "Ruang konsultasi dokter spesialis.",
    floor: 2,
  },
  "R._Laundry_2": {
    id: "R._Laundry_2",
    name: "R. Laundry 2",
    category: "Facility",
    locationHint: "Lantai 2",
    description: "Area laundry pendukung operasional lantai 2.",
    floor: 2,
  },
  "R._HRD___Kepegawaian": {
    id: "R._HRD___Kepegawaian",
    name: "R. HRD / Kepegawaian",
    category: "Administration",
    locationHint: "Lantai 2",
    description: "Area administrasi sumber daya manusia dan kepegawaian.",
    floor: 2,
  },
  "R._IT___Server": {
    id: "R._IT___Server",
    name: "R. IT / Server",
    category: "Administration",
    locationHint: "Lantai 2",
    description: "Ruang infrastruktur IT dan server rumah sakit.",
    floor: 2,
  },
  "R._Tumbuh_Kembang_Anak": {
    id: "R._Tumbuh_Kembang_Anak",
    name: "R. Tumbuh Kembang Anak",
    category: "Treatment",
    locationHint: "Lantai 2",
    description: "Layanan pemantauan dan terapi tumbuh kembang anak.",
    floor: 2,
  },
  Terapi_Okupasi_Lanjutan: {
    id: "Terapi_Okupasi_Lanjutan",
    name: "Terapi Okupasi Lanjutan",
    category: "Treatment",
    locationHint: "Lantai 2",
    description: "Ruang terapi okupasi lanjutan.",
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
  Manajemen: {
    id: "Manajemen",
    name: "Manajemen",
    category: "Administration",
    locationHint: "Lantai 2",
    description: "Area manajemen rumah sakit.",
    floor: 2,
  },
  Radioterapi: {
    id: "Radioterapi",
    name: "Radioterapi",
    category: "Treatment",
    locationHint: "Lantai 2",
    description: "Ruang layanan radioterapi.",
    floor: 2,
  },
  "R._PACS": {
    id: "R._PACS",
    name: "R. PACS",
    category: "Diagnostic",
    locationHint: "Lantai 2",
    description: "Ruang sistem penyimpanan dan komunikasi citra medis.",
    floor: 2,
  },
  "R._MRI": {
    id: "R._MRI",
    name: "R. MRI",
    category: "Diagnostic",
    locationHint: "Lantai 2",
    description: "Ruang pemeriksaan Magnetic Resonance Imaging.",
    floor: 2,
  },
  Gudang_Alat_Medis_Steril: {
    id: "Gudang_Alat_Medis_Steril",
    name: "Gudang Alat Medis Steril",
    category: "Facility",
    locationHint: "Lantai 2",
    description: "Gudang alat medis steril.",
    floor: 2,
  },
  "R._Istirahat_Perawat": {
    id: "R._Istirahat_Perawat",
    name: "R. Istirahat Perawat",
    category: "Facility",
    locationHint: "Lantai 2",
    description: "Ruang istirahat perawat.",
    floor: 2,
  },
  "R._Training_Medis": {
    id: "R._Training_Medis",
    name: "R. Training Medis",
    category: "Service",
    locationHint: "Lantai 2",
    description: "Ruang pelatihan dan pengembangan tenaga medis.",
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
  Meja_Resepsionis_Lantai_2: {
    id: "Meja_Resepsionis_Lantai_2",
    name: "Meja Resepsionis Lantai 2",
    category: "Service",
    locationHint: "Dekat lobby lantai 2",
    description: "Meja resepsionis untuk informasi layanan lantai 2.",
    floor: 2,
  },
  Toilet_Lantai_2: {
    id: "Toilet_Lantai_2",
    name: "Toilet Lantai 2",
    category: "Facility",
    locationHint: "Lantai 2",
    description: "Fasilitas toilet umum lantai 2.",
    floor: 2,
  },
  "R._Meeting": {
    id: "R._Meeting",
    name: "R. Meeting",
    category: "Administration",
    locationHint: "Lantai 2",
    description: "Ruang rapat staf dan manajemen.",
    floor: 2,
  },
  "R._Direktur___Manajemen": {
    id: "R._Direktur___Manajemen",
    name: "R. Direktur / Manajemen",
    category: "Administration",
    locationHint: "Lantai 2",
    description: "Ruang direktur dan manajemen rumah sakit.",
    floor: 2,
  },
  "R._Arsip_Utama": {
    id: "R._Arsip_Utama",
    name: "R. Arsip Utama",
    category: "Administration",
    locationHint: "Lantai 2",
    description: "Ruang penyimpanan arsip utama rumah sakit.",
    floor: 2,
  },
  "R._Konsultasi_Dokter": {
    id: "R._Konsultasi_Dokter",
    name: "R. Konsultasi Dokter",
    category: "Outpatient",
    locationHint: "Lantai 2",
    description: "Ruang konsultasi dokter untuk pasien rawat jalan.",
    floor: 2,
  },
  // Parking areas
  Parking_Lantai_1: {
    id: "Parking_Lantai_1",
    name: "Lahan Parkir",
    category: "Facility",
    locationHint: "Area parkir kendaraan rumah sakit",
    description: "Area parkir kendaraan pengunjung dan tenaga medis. Tersedia parkir sepeda motor dan mobil.",
    floor: 0,
  },
  Tangga_Pengunjung_di_Lahan_Parkir_lantai_1: {
    id: "Tangga_Pengunjung_di_Lahan_Parkir_lantai_1",
    name: "Tangga Pengunjung Parkir L1",
    category: "Facility",
    locationHint: "Tangga penghubung parkir lantai 1 dan 2",
    description: "Tangga untuk akses antara parkir lantai 1 dan lantai 2.",
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
  Tangga_Pengunjung_di_Lahan_Parkir_lantai_2: {
    id: "Tangga_Pengunjung_di_Lahan_Parkir_lantai_2",
    name: "Tangga Pengunjung Parkir L2",
    category: "Facility",
    locationHint: "Tangga penghubung parkir lantai 1 dan 2",
    description: "Tangga untuk akses antara parkir lantai 1 dan lantai 2.",
    floor: -1,
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

  // Validate category exists in CATEGORIES table
  const categoryValidation = validateCategory(category);
  if (!categoryValidation.valid) {
    throw new Error(categoryValidation.error);
  }

  hospitalRooms[id] = {
    id,
    name,
    category,
    locationHint: locationHint || '',
    description: description || '',
    floor: floor !== undefined ? floor : 1,
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

/**
 * Get statistics
 */
export const getRoomStats = () => {
  const rooms = Object.values(hospitalRooms);
  const byFloor = {};
  const byCategory = {};
  
  rooms.forEach((room) => {
    const floor = room.floor;
    const category = room.category;
    
    byFloor[floor] = (byFloor[floor] || 0) + 1;
    byCategory[category] = (byCategory[category] || 0) + 1;
  });

  return {
    total: rooms.length,
    byFloor,
    byCategory,
    categories: Object.keys(byCategory).sort(),
  };
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
  getRoomStats,
};
