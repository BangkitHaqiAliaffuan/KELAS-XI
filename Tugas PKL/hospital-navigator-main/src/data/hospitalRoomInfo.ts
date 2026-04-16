export interface HospitalRoomInfo {
  id: string;
  name: string;
  category: string;
  locationHint: string;
  description: string;
}

export const roomInfoBySvgId: Record<string, HospitalRoomInfo> = {
  IGD: {
    id: "IGD",
    name: "IGD",
    category: "Emergency",
    locationHint: "Sayap kiri bawah peta",
    description: "Instalasi Gawat Darurat untuk penanganan kondisi medis darurat 24 jam.",
  },
  Poliklinik: {
    id: "Poliklinik",
    name: "Poliklinik",
    category: "Outpatient",
    locationHint: "Area kanan bawah peta",
    description: "Layanan konsultasi rawat jalan berbagai poli spesialis.",
  },
  ICU: {
    id: "ICU",
    name: "ICU",
    category: "Critical Care",
    locationHint: "Tengah kiri peta",
    description: "Unit perawatan intensif untuk pasien dengan kondisi kritis.",
  },
  Lab: {
    id: "Lab",
    name: "Laboratorium",
    category: "Diagnostic",
    locationHint: "Bagian tengah peta",
    description: "Pemeriksaan laboratorium penunjang diagnosis pasien.",
  },
  Farmasi: {
    id: "Farmasi",
    name: "Farmasi",
    category: "Facility",
    locationHint: "Tengah bawah peta",
    description: "Pelayanan obat resep, informasi obat, dan konseling farmasi.",
  },
  Radiologi: {
    id: "Radiologi",
    name: "Radiologi",
    category: "Diagnostic",
    locationHint: "Tengah bawah-kiri peta",
    description: "Layanan pemeriksaan radiologi seperti X-Ray dan USG.",
  },
  Toilet: {
    id: "Toilet",
    name: "Toilet",
    category: "Facility",
    locationHint: "Tengah kanan bawah peta",
    description: "Fasilitas toilet umum untuk pengunjung dan pasien.",
  },
  Informasi: {
    id: "Informasi",
    name: "Informasi",
    category: "Service",
    locationHint: "Dekat area IGD",
    description: "Meja informasi untuk bantuan arah ruangan dan layanan rumah sakit.",
  },
  Rekam_Medis: {
    id: "Rekam_Medis",
    name: "Rekam Medis",
    category: "Administration",
    locationHint: "Tengah bawah-kanan peta",
    description: "Pengelolaan data rekam medis pasien dan administrasi dokumen.",
  },
  Kamar_Operasi: {
    id: "Kamar_Operasi",
    name: "Kamar Operasi",
    category: "Surgery",
    locationHint: "Sayap kiri tengah peta",
    description: "Area tindakan operasi dengan kontrol sterilitas tinggi.",
  },
  Musholla: {
    id: "Musholla",
    name: "Musholla",
    category: "Facility",
    locationHint: "Sisi kanan peta",
    description: "Fasilitas ibadah untuk pasien, keluarga, dan tenaga medis.",
  },
  CSSD: {
    id: "CSSD",
    name: "CSSD",
    category: "Facility",
    locationHint: "Sayap kiri tengah peta",
    description: "Unit sterilisasi alat medis untuk kebutuhan tindakan klinis.",
  },
};
