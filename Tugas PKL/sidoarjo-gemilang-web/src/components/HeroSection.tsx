import { useCallback, useRef, useState } from "react";
import { ArrowRight, ChevronLeft, ChevronRight, X } from "lucide-react";
import { FaLightbulb, FaMapMarkerAlt, FaNewspaper, FaRulerCombined, FaUsers } from "react-icons/fa";

type RegionPopupData = {
  district: string;
  uniquePlace: string;
  images: Array<{
    src: string;
    alt: string;
  }>;
  facts: string[];
  population: string;
  area: string;
  news: Array<{
    title: string;
    description: string;
    image: string;
    imageAlt: string;
  }>;
};

type RegionPopupSeed = Omit<RegionPopupData, "images" | "news"> & {
  images?: RegionPopupData["images"];
  news: Array<{
    title: string;
    description: string;
  }>;
};

const fallbackRegionImages: RegionPopupData["images"] = [
  {
    src: "/images/places/gedangan/gedangan-1-e1754389894765.jpg",
    alt: "Potret wilayah Sidoarjo",
  },
  {
    src: "/images/places/buduran/ARCA%20DI%20MUSEUM%20MPU%20TANTULAR.jpg",
    alt: "Potret destinasi Sidoarjo",
  },
];

const buildRegionPopupData = (seed: RegionPopupSeed): RegionPopupData => {
  const images = seed.images ?? fallbackRegionImages;

  return {
    district: seed.district,
    uniquePlace: seed.uniquePlace,
    images,
    facts: seed.facts,
    population: seed.population,
    area: seed.area,
    news: seed.news.map((item, index) => ({
      ...item,
      image: images[index % images.length].src,
      imageAlt: item.title,
    })),
  };
};

const regionPopupData: Record<number, RegionPopupData> = {
  1: buildRegionPopupData({
    district: "KECAMATAN BALONGBENDO",
    uniquePlace: "Koridor Industri Balongbendo",
    facts: [
      "Balongbendo memiliki kanal resmi kecamatan untuk layanan publik dan informasi pemerintahan.",
      "Wilayah ini berkembang sebagai jalur konektivitas barat Sidoarjo menuju kawasan industri sekitar.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Penataan Jalan Penghubung Balongbendo Dipercepat",
        description: "Program perbaikan ruas jalan kabupaten diprioritaskan untuk memperkuat mobilitas warga dan logistik Balongbendo.",
      },
      {
        title: "Digitalisasi Layanan Kecamatan Balongbendo Ditingkatkan",
        description: "Pembaruan kanal informasi publik dilakukan agar masyarakat lebih cepat mengakses layanan administratif.",
      },
    ],
  }),
  2: buildRegionPopupData({
    district: "KECAMATAN BUDURAN",
    uniquePlace: "Museum Mpu Tantular",
    images: [
      {
        src: "/images/places/buduran/ARCA%20DI%20MUSEUM%20MPU%20TANTULAR.jpg",
        alt: "Arca di Museum Mpu Tantular Buduran",
      },
      {
        src: "/images/places/buduran/Tuin%20bij%20een%20woning%2C%20vermoedelijk%20van%20onderneming%20Bandjar-Redjo%20te%20Boedoeran%20bij%20Sidoardjo%2C%20ca%201900.jpg",
        alt: "Dokumentasi historis kawasan Buduran",
      },
    ],
    facts: [
      "Buduran dikenal sebagai wilayah dengan destinasi edukasi sejarah melalui Museum Mpu Tantular.",
      "Portal kecamatan menyediakan profil wilayah, layanan publik, dan informasi kegiatan masyarakat.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Tur Edukasi Buduran untuk Pelajar Diperluas",
        description: "Program kunjungan belajar ke kawasan budaya Buduran ditingkatkan untuk memperkuat literasi sejarah lokal.",
      },
      {
        title: "Agenda Pameran Budaya Kecamatan Buduran Dibuka",
        description: "Kolaborasi komunitas dan pemerintah wilayah mendorong aktivitas budaya akhir pekan lebih merata.",
      },
    ],
  }),
  3: buildRegionPopupData({
    district: "KECAMATAN CANDI",
    uniquePlace: "Kawasan Sun City Sidoarjo",
    facts: [
      "Candi berada di kawasan penyangga perkotaan Sidoarjo dengan aktivitas layanan publik yang tinggi.",
      "Informasi pemerintahan dan layanan administratif Candi dipublikasikan secara rutin melalui kanal resmi.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Revitalisasi Akses Lingkungan di Candi Berjalan",
        description: "Perbaikan akses kawasan permukiman dan pusat layanan di Candi terus dilakukan bertahap.",
      },
      {
        title: "Kecamatan Candi Perkuat Transparansi Informasi Publik",
        description: "Optimalisasi kanal berita wilayah ditargetkan mempercepat penyampaian program ke masyarakat.",
      },
    ],
  }),
  4: buildRegionPopupData({
    district: "KECAMATAN GEDANGAN",
    uniquePlace: "Flyover Juanda",
    images: [
      {
        src: "/images/places/gedangan/gedangan-1-e1754389894765.jpg",
        alt: "Kawasan Gedangan",
      },
      {
        src: "/images/places/gedangan/unnamed.jpg",
        alt: "Potret wilayah Gedangan",
      },
    ],
    facts: [
      "Gedangan merupakan simpul mobilitas menuju Bandara Juanda dan wilayah utara Sidoarjo.",
      "Kawasan ini berperan sebagai penghubung strategis antara Surabaya dan pusat pertumbuhan Sidoarjo.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Penataan Akses Flyover Juanda Dilanjutkan",
        description: "Penguatan rekayasa lalu lintas dan konektivitas jalan lingkungan Gedangan dilakukan untuk mengurangi kepadatan.",
      },
      {
        title: "Pengembangan Jalur Pendukung Transportasi Gedangan",
        description: "Program peningkatan jalur pendukung di Gedangan disiapkan untuk memperlancar pergerakan komuter.",
      },
    ],
  }),
  5: buildRegionPopupData({
    district: "KECAMATAN JABON",
    uniquePlace: "Pulau Lusi",
    facts: [
      "Jabon memiliki karakter wilayah pesisir-muara dengan potensi ekowisata dan perikanan.",
      "Kecamatan ini aktif menayangkan informasi pelayanan publik serta program pembangunan wilayah.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Penguatan Infrastruktur Pesisir Jabon Diprioritaskan",
        description: "Agenda penataan kawasan pesisir diarahkan untuk mendukung aktivitas masyarakat dan ketahanan wilayah.",
      },
      {
        title: "Layanan Publik Kecamatan Jabon Masuk Tahap Digitalisasi",
        description: "Optimalisasi layanan administrasi berbasis informasi digital terus diperluas untuk warga Jabon.",
      },
    ],
  }),
  6: buildRegionPopupData({
    district: "KECAMATAN KREMBUNG",
    uniquePlace: "Pasar Krembung",
    facts: [
      "Krembung merupakan wilayah dengan aktivitas ekonomi lokal yang bertumpu pada perdagangan dan jasa masyarakat.",
      "Laman resmi kecamatan memuat informasi perangkat, standar layanan, dan berita kegiatan wilayah.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Perbaikan Ruas Jalan Lokal Krembung Dipercepat",
        description: "Program infrastruktur diarahkan untuk memperlancar konektivitas antar-desa dan pusat ekonomi lokal.",
      },
      {
        title: "Penguatan Informasi Program Sosial di Krembung",
        description: "Pemerintah wilayah menekankan penyebaran informasi program sosial agar tepat sasaran.",
      },
    ],
  }),
  7: buildRegionPopupData({
    district: "KECAMATAN KRIAN",
    uniquePlace: "Stasiun Krian",
    facts: [
      "Krian dikenal sebagai kawasan komuter dan perdagangan penting di sisi barat Kabupaten Sidoarjo.",
      "Informasi layanan publik, PPID, dan agenda pemerintahan kecamatan tersedia pada portal resmi wilayah.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Akses Komuter Krian Diperkuat Lewat Program Jalan Daerah",
        description: "Percepatan pemeliharaan jalan kabupaten mendukung arus harian pekerja dan pelaku usaha di Krian.",
      },
      {
        title: "Krian Dorong Integrasi Layanan Publik Berbasis Data",
        description: "Pembenahan sistem informasi kecamatan ditujukan untuk meningkatkan kecepatan layanan administrasi.",
      },
    ],
  }),
  8: buildRegionPopupData({
    district: "KECAMATAN PORONG",
    uniquePlace: "Tanggul Lumpur Sidoarjo",
    facts: [
      "Porong memiliki karakter kawasan dengan isu kebencanaan geologi dan pengelolaan infrastruktur khusus.",
      "Portal kecamatan menyediakan informasi profil, kontak layanan, serta program pemerintah wilayah.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Konektivitas Selatan Sidoarjo via Porong Terus Diperbaiki",
        description: "Penanganan jalan dan utilitas di kawasan Porong diprioritaskan untuk menjamin akses layanan warga.",
      },
      {
        title: "Koordinasi Mitigasi Wilayah Porong Diperkuat",
        description: "Pemerintah wilayah meningkatkan koordinasi kesiapsiagaan dan informasi publik untuk keamanan masyarakat.",
      },
    ],
  }),
  9: buildRegionPopupData({
    district: "KECAMATAN PRAMBON",
    uniquePlace: "Sentra Pertanian Prambon",
    facts: [
      "Prambon berkarakter perdesaan dengan potensi pertanian yang menopang ketahanan pangan lokal.",
      "Informasi layanan administrasi dan profil pemerintahan kecamatan dipublikasikan melalui situs resmi.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Akses Desa Produktif di Prambon Menjadi Prioritas",
        description: "Perbaikan infrastruktur antarwilayah diarahkan untuk mendukung distribusi hasil pertanian masyarakat.",
      },
      {
        title: "Literasi Informasi Publik Warga Prambon Diperkuat",
        description: "Pemerintah kecamatan memperluas sosialisasi layanan agar warga lebih mudah mengakses dokumen administratif.",
      },
    ],
  }),
  10: buildRegionPopupData({
    district: "KECAMATAN SEDATI",
    uniquePlace: "Kawasan Bandara Juanda",
    facts: [
      "Sedati merupakan kawasan strategis utara Sidoarjo yang terhubung langsung dengan bandara dan jalur pesisir.",
      "Pemerintah kecamatan menyediakan publikasi profil, layanan, dan kegiatan wilayah secara berkala.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Penataan Akses Kawasan Utara Sedati Terus Berlanjut",
        description: "Perbaikan infrastruktur jalan dilakukan untuk menjaga kelancaran mobilitas warga dan aktivitas ekonomi.",
      },
      {
        title: "Layanan Informasi Publik Sedati Diperkuat",
        description: "Optimalisasi kanal komunikasi pemerintah wilayah ditargetkan mempercepat penyampaian kebijakan daerah.",
      },
    ],
  }),
  11: buildRegionPopupData({
    district: "KECAMATAN SIDOARJO",
    uniquePlace: "Alun-Alun Sidoarjo (Monumen Jayandaru)",
    facts: [
      "Kecamatan Sidoarjo adalah pusat administratif kabupaten dengan konsentrasi layanan pemerintahan yang tinggi.",
      "Wilayah ini menjadi simpul kegiatan publik, ekonomi, dan layanan masyarakat tingkat kabupaten.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Pusat Layanan Publik Sidoarjo Ditingkatkan",
        description: "Penguatan kualitas layanan lintas sektor dilakukan untuk mendukung kebutuhan warga di pusat kabupaten.",
      },
      {
        title: "Program Kota Bersih dan Tertib Dipercepat di Sidoarjo",
        description: "Kolaborasi antar-perangkat daerah difokuskan pada kenyamanan ruang publik dan akses masyarakat.",
      },
    ],
  }),
  12: buildRegionPopupData({
    district: "KECAMATAN SUKODONO",
    uniquePlace: "Koridor Pelayanan Sukodono",
    facts: [
      "Sukodono memiliki pertumbuhan kawasan permukiman yang terus meningkat dalam beberapa tahun terakhir.",
      "Kecamatan ini aktif mempublikasikan informasi layanan dan kegiatan wilayah melalui kanal digital resmi.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Akses Permukiman Sukodono Masuk Program Prioritas",
        description: "Perbaikan jalan lingkungan dan jalur penghubung ditargetkan meningkatkan mobilitas harian warga.",
      },
      {
        title: "Sukodono Perkuat Kanal Pengaduan dan Informasi",
        description: "Peningkatan layanan informasi publik dilakukan agar respon terhadap kebutuhan masyarakat lebih cepat.",
      },
    ],
  }),
  13: buildRegionPopupData({
    district: "KECAMATAN TAMAN",
    uniquePlace: "Sentra Batik Griya Kriya Taman",
    facts: [
      "Taman termasuk kecamatan padat penduduk dengan aktivitas ekonomi dan layanan perkotaan yang dinamis.",
      "Wilayah ini memiliki komunitas UMKM aktif, termasuk pengrajin batik dan usaha rumah tangga kreatif.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Penataan Ruas Padat Mobilitas di Taman Dipercepat",
        description: "Program pemeliharaan infrastruktur dipusatkan pada kawasan dengan lalu lintas harian tinggi.",
      },
      {
        title: "Promosi UMKM Kreatif Kecamatan Taman Diperluas",
        description: "Pemerintah wilayah mendorong promosi produk lokal untuk memperkuat ekonomi masyarakat.",
      },
    ],
  }),
  14: buildRegionPopupData({
    district: "KECAMATAN TANGGULANGIN",
    uniquePlace: "Sentra Tas Tanggulangin",
    facts: [
      "Tanggulangin dikenal luas sebagai sentra kerajinan tas dan kulit yang menjadi ikon ekonomi lokal Sidoarjo.",
      "Kecamatan ini memiliki ekosistem UMKM yang kuat dengan dukungan promosi dan pembinaan usaha.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Program Penguatan UMKM Tas Tanggulangin Dijalankan",
        description: "Pendampingan promosi dan akses pasar ditingkatkan untuk mendorong daya saing pelaku usaha lokal.",
      },
      {
        title: "Perbaikan Akses Distribusi Produk Tanggulangin",
        description: "Peningkatan kualitas ruas jalan penghubung dilakukan untuk mendukung arus barang dari sentra usaha.",
      },
    ],
  }),
  15: buildRegionPopupData({
    district: "KECAMATAN TARIK",
    uniquePlace: "Kawasan Industri Tarik",
    facts: [
      "Tarik memiliki kombinasi potensi pertanian dan industri, menjadikannya kawasan ekonomi campuran di barat Sidoarjo.",
      "Informasi pemerintahan kecamatan dan layanan masyarakat dipublikasikan melalui laman resmi wilayah.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Konektivitas Industri dan Permukiman Tarik Diperkuat",
        description: "Program penanganan ruas prioritas ditujukan untuk menunjang kelancaran mobilitas pekerja dan warga.",
      },
      {
        title: "Tarik Tingkatkan Integrasi Informasi Pelayanan Publik",
        description: "Penyelarasan layanan digital dilakukan agar akses dokumen administratif masyarakat semakin mudah.",
      },
    ],
  }),
  16: buildRegionPopupData({
    district: "KECAMATAN TULANGAN",
    uniquePlace: "Kawasan Kenongo Tulangan",
    facts: [
      "Tulangan memiliki peran penting sebagai koridor penghubung antarwilayah di bagian tengah-selatan Sidoarjo.",
      "Kecamatan ini aktif menampilkan informasi PPID, layanan publik, dan kegiatan sosial pemerintahan.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Akses Antarwilayah Tulangan Masuk Skema Perbaikan",
        description: "Pemerintah daerah mempercepat pemeliharaan jalur utama untuk mendukung layanan dan aktivitas ekonomi warga.",
      },
      {
        title: "Peningkatan Komunikasi Publik Kecamatan Tulangan",
        description: "Optimalisasi informasi program pemerintah dilakukan agar masyarakat lebih cepat menerima pembaruan kebijakan.",
      },
    ],
  }),
  17: buildRegionPopupData({
    district: "KECAMATAN WARU",
    uniquePlace: "Terminal Purabaya (Bungurasih)",
    facts: [
      "Waru merupakan gerbang utama Sidoarjo yang berbatasan langsung dengan Surabaya dan dilalui arus komuter tinggi.",
      "Wilayah ini berperan penting dalam jaringan transportasi regional melalui terminal dan koridor jalan utama.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Penataan Simpul Transportasi Waru Diprioritaskan",
        description: "Penguatan manajemen lalu lintas dan infrastruktur diarahkan untuk menjaga kelancaran mobilitas harian.",
      },
      {
        title: "Layanan Publik Wilayah Waru Diperkuat Berbasis Digital",
        description: "Integrasi kanal informasi dilakukan agar masyarakat dapat mengakses pengumuman dan layanan lebih cepat.",
      },
    ],
  }),
  18: buildRegionPopupData({
    district: "KECAMATAN WONOAYU",
    uniquePlace: "Pasar Wonoayu",
    facts: [
      "Wonoayu memiliki karakter wilayah agraris dengan dukungan jaringan desa yang aktif dalam kegiatan ekonomi lokal.",
      "Portal kecamatan memuat informasi perangkat, layanan publik, dan dokumentasi kegiatan masyarakat.",
    ],
    population: "Data BPS",
    area: "Data BPS",
    news: [
      {
        title: "Penguatan Jalan Produksi Wonoayu Terus Dilakukan",
        description: "Perbaikan akses wilayah diarahkan untuk mendukung mobilitas warga dan distribusi hasil ekonomi lokal.",
      },
      {
        title: "Wonoayu Tingkatkan Publikasi Program Layanan Warga",
        description: "Kecamatan memperluas penyampaian informasi program pemerintah untuk memperkuat partisipasi masyarakat.",
      },
    ],
  }),
};

const HeroSection = () => {
  const [activeRegion, setActiveRegion] = useState<RegionPopupData | null>(null);
  const [activeSlide, setActiveSlide] = useState(0);
  const mapObjectRef = useRef<HTMLObjectElement | null>(null);

  const bindRegionPopupEvents = useCallback(() => {
    const mapDocument = mapObjectRef.current?.contentDocument;

    if (!mapDocument) {
      return;
    }

    const regionPaths = Array.from(mapDocument.querySelectorAll("#Kecamatan > path")).filter((node) => {
      const fill = (node.getAttribute("fill") || "").toLowerCase();
      return fill !== "none";
    });

    regionPaths.forEach((regionPath, index) => {
      const regionNumber = index + 1;
      const regionInfo = regionPopupData[regionNumber];

      if (!regionInfo) {
        return;
      }

      if (regionPath.getAttribute("data-popup-bound") === "true") {
        return;
      }

      regionPath.setAttribute("data-popup-bound", "true");
      regionPath.setAttribute("data-region-name", regionInfo.district);
      regionPath.setAttribute("aria-label", `${regionInfo.district} - klik untuk lihat informasi`);

      const openPopup = (event: Event) => {
        event.preventDefault();
        event.stopPropagation();
        setActiveRegion(regionInfo);
        setActiveSlide(0);
      };

      regionPath.addEventListener("click", openPopup);
      regionPath.addEventListener("keydown", (event) => {
        const keyboardEvent = event as KeyboardEvent;
        if (keyboardEvent.key === "Enter" || keyboardEvent.key === " ") {
          openPopup(event);
        }
      });
    });
  }, []);

  const closePopup = () => {
    setActiveRegion(null);
    setActiveSlide(0);
  };

  const goToPrevSlide = () => {
    if (!activeRegion) {
      return;
    }

    const totalSlides = activeRegion.images.length;
    setActiveSlide((prev) => (prev - 1 + totalSlides) % totalSlides);
  };

  const goToNextSlide = () => {
    if (!activeRegion) {
      return;
    }

    const totalSlides = activeRegion.images.length;
    setActiveSlide((prev) => (prev + 1) % totalSlides);
  };

  return (
    <section className="relative flex min-h-screen flex-col ">
      {/* Header */}
      <div className="flex items-center justify-between px-20 py-6">
        <div>
          <p className="text-sm font-medium text-muted-foreground">Portal Resmi</p>
          <h1 className="text-2xl font-bold text-foreground">Pemerintah Kabupaten Sidoarjo</h1>
        </div>
        <button className="flex items-center gap-2 rounded-full border border-border bg-background px-6 py-3 text-sm font-semibold text-foreground shadow-sm transition-all hover:shadow-md">
          Akses Cepat
          <ArrowRight className="h-4 w-4" />
        </button>
      </div>

      {/* Map Area */}
      <div className="flex flex-1 items-center justify-center px-8 pb-44">
        <div className="animate-fade-in w-full max-w-5xl">
          <div className="relative h-[clamp(360px,58vh,620px)] w-full overflow-hidden rounded-2xl bg-[#F1F1F1]">
            <object
              ref={mapObjectRef}
              onLoad={bindRegionPopupEvents}
              data="/images/sidoarjo-map.svg"
              type="image/svg+xml"
              aria-label="Peta Kabupaten Sidoarjo interaktif"
              className="absolute left-1/2 top-1/2 h-[155%] w-auto max-w-none -translate-x-1/2 -translate-y-[50%] bg-[#F1F1F1]"
            >
              <img
                src="/images/sidoarjo-map.svg"
                alt="Peta Kabupaten Sidoarjo"
                className="absolute left-1/2 top-1/2 h-[155%] w-auto max-w-none -translate-x-1/2 -translate-y-[50%] bg-[#F1F1F1]"
              />
            </object>

            <div className="pointer-events-none absolute left-[70%] top-[56%] z-20 -translate-x-1/2 -translate-y-1/2">
              <img
                src="/images/places/alun-alun.png"
                alt="Spot Alun-Alun"
                className="h-32 w-32 object-contain brightness-125 contrast-110 saturate-125 drop-shadow-[0_6px_14px_rgba(0,0,0,0.25)]"
              />
            </div>

            <div className="pointer-events-none absolute left-[66%] top-[70%] z-20 -translate-x-1/2 -translate-y-1/2">
              <img
                src="/images/places/candi-pari.png"
                alt="Spot Candi Pari"
                className="h-20 w-20 object-contain brightness-125 contrast-110 saturate-125 drop-shadow-[0_6px_14px_rgba(0,0,0,0.25)]"
              />
            </div>

            <div className="pointer-events-none absolute left-[60%] top-[25%] z-20 -translate-x-1/2 -translate-y-1/2">
              <img
                src="/images/places/monumen-juanda.png"
                alt="Spot Monumen Juanda"
                className="h-20 w-20 object-contain brightness-125 contrast-110 saturate-125 drop-shadow-[0_6px_14px_rgba(0,0,0,0.25)]"
              />
            </div>

            <div className="pointer-events-none absolute left-[60%] top-[38%] z-20 -translate-x-1/2 -translate-y-1/2">
              <img
                src="/images/places/museum-tantular.png"
                alt="Spot Museum Tantular"
                className="h-20 w-20 object-contain brightness-125 contrast-110 saturate-125 drop-shadow-[0_6px_14px_rgba(0,0,0,0.25)]"
              />
            </div>

            <div className="pointer-events-none absolute left-[80%] top-[78%] z-20 -translate-x-1/2 -translate-y-1/2">
              <img
                src="/images/places/pulau-sarinah.png"
                alt="Spot Pulau Sarinah"
                className="h-20 w-20 object-contain brightness-125 contrast-110 saturate-125 drop-shadow-[0_6px_14px_rgba(0,0,0,0.25)]"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Leader Section */}
      <div className="pointer-events-none absolute inset-x-0 bottom-5 z-30 left-10 leader mx-auto flex  items-end justify-center px-4 max-w-6xl pb-2">
        <div className="relative flex w-full leader-box items-end overflow-visible rounded-t-2xl border border-emerald-100/80 bg-leader-bg shadow-lg">
          <div className="pointer-events-none absolute inset-0 rounded-t-2xl bg-gradient-to-r from-emerald-100/75 via-white/45 to-emerald-100/75" />
          {/* Bupati */}
          <div className="relative z-10 flex flex-1 items-end gap-10">
            <img
              src="/images/bupati.png"
              alt="Bupati Sidoarjo"
              className="relative z-30 h-48 w-auto -ml-4 -mb-0 object-contain"
              loading="lazy"
            />
            <div className="pb-6 flex flex-col items-start">
              <p className="text-l font-semibold uppercase tracking-wider text-muted-foreground">
                Bupati Sidoarjo
              </p>
              <p className="text-3xl font-bold text-foreground">
                H. SUBANDI
              </p>
            </div>
          </div>

          {/* Divider */}
          <div className="relative z-10 h-20 w-px self-center bg-emerald-200/90" />

          {/* Wakil Bupati */}
          <div className="relative z-10 flex flex-1 flex-row-reverse items-end gap-10">
            <img
              src="/images/wakil-bupati.png"
              alt="Wakil Bupati Sidoarjo"
              className="relative z-30 h-48 w-auto -mr-4 -mb-0 object-contain"
              loading="lazy"
            />
            <div className="pb-6 text-right flex flex-col items-end">
              <p className="text-l font-semibold uppercase tracking-wider text-muted-foreground">
                Wakil Bupati Sidoarjo
              </p>
              <p className="text-3xl font-bold text-foreground">
                HJ. MIMIK IDAYANA
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Language Switcher */}
      <div className="fixed bottom-6 right-6 z-50 flex items-center gap-3 rounded-full bg-background px-4 py-2 shadow-lg border border-border">
        <button className="flex items-center gap-1.5 text-sm font-semibold text-foreground">
          🇮🇩 ID
        </button>
        <button className="flex items-center gap-1.5 text-sm text-muted-foreground">
          🇬🇧 EN
        </button>
      </div>

      {activeRegion && (
        <div className="fixed inset-0 z-[70] flex items-center justify-center bg-black/45 px-4 py-6 backdrop-blur-[1px]" onClick={closePopup}>
          <div
            className="w-full max-w-xl overflow-hidden rounded-3xl border border-emerald-100/90 bg-white shadow-2xl"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="relative h-64 w-full bg-emerald-50">
              <img
                src={activeRegion.images[activeSlide].src}
                alt={activeRegion.images[activeSlide].alt}
                className="h-full w-full object-cover brightness-110 contrast-110 saturate-110"
              />

              <div className="pointer-events-none absolute inset-0 bg-gradient-to-t from-black/45 via-black/10 to-transparent" />

              <div className="absolute bottom-3 left-3 rounded-full bg-white/90 px-3 py-1 text-xs font-semibold text-emerald-700">
                {activeSlide + 1} / {activeRegion.images.length}
              </div>

              <button
                className="absolute left-3 top-1/2 -translate-y-1/2 rounded-full bg-white/90 p-2 text-emerald-700 transition hover:bg-white hover:text-emerald-800"
                onClick={goToPrevSlide}
                aria-label="Gambar sebelumnya"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>

              <button
                className="absolute right-3 top-1/2 -translate-y-1/2 rounded-full bg-white/90 p-2 text-emerald-700 transition hover:bg-white hover:text-emerald-800"
                onClick={goToNextSlide}
                aria-label="Gambar berikutnya"
              >
                <ChevronRight className="h-4 w-4" />
              </button>

              <button
                className="absolute right-3 top-3 rounded-full bg-white/90 p-2 text-muted-foreground transition hover:bg-white hover:text-foreground"
                onClick={closePopup}
                aria-label="Tutup popup informasi"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            <div className="space-y-4 p-5">
              <div className="flex flex-wrap gap-2">
                {activeRegion.images.map((image, index) => (
                  <button
                    key={`${image.src}-${index}`}
                    type="button"
                    className={`h-2.5 rounded-full transition-all ${index === activeSlide ? "w-7 bg-emerald-500" : "w-2.5 bg-emerald-200 hover:bg-emerald-300"}`}
                    onClick={() => setActiveSlide(index)}
                    aria-label={`Buka gambar ${index + 1}`}
                  />
                ))}
              </div>

              <h3 className="text-2xl font-extrabold tracking-tight text-foreground">{activeRegion.district}</h3>

              <div className="rounded-xl border border-emerald-100 bg-emerald-50/70 px-3 py-2">
                <p className="mb-1 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-emerald-700">
                  <FaMapMarkerAlt className="h-3.5 w-3.5" />
                  Tempat unik
                </p>
                <p className="text-sm font-semibold text-foreground">{activeRegion.uniquePlace}</p>
              </div>

              <div>
                <p className="mb-2 flex items-center gap-2 text-base font-semibold">
                  <FaLightbulb className="h-4 w-4 text-emerald-600" />
                  Unique Facts
                </p>
                <div className="grid gap-2">
                  {activeRegion.facts.map((fact, index) => (
                    <div
                      key={fact}
                      className="flex items-start gap-2 rounded-lg border border-emerald-100 bg-emerald-50/60 px-3 py-2"
                    >
                      <span className="mt-0.5 inline-flex h-5 min-w-5 items-center justify-center rounded-full bg-emerald-500 px-1 text-[10px] font-bold text-white">
                        {index + 1}
                      </span>
                      <p className="text-sm leading-relaxed text-muted-foreground">{fact}</p>
                    </div>
                  ))}
                </div>
              </div>

              <div>
                <p className="mb-2 text-base font-semibold">Statistics</p>
                <div className="grid grid-cols-2 gap-3 rounded-xl border border-emerald-100 bg-white p-3">
                  <div className="rounded-lg border border-emerald-100/80 bg-emerald-50/60 p-2">
                    <p className="mb-0.5 flex items-center gap-1.5 text-xs text-muted-foreground">
                      <FaUsers className="h-3 w-3" />
                      Population
                    </p>
                    <p className="text-base font-bold">{activeRegion.population}</p>
                  </div>
                  <div className="rounded-lg border border-emerald-100/80 bg-emerald-50/60 p-2">
                    <p className="mb-0.5 flex items-center gap-1.5 text-xs text-muted-foreground">
                      <FaRulerCombined className="h-3 w-3" />
                      Area
                    </p>
                    <p className="text-base font-bold">{activeRegion.area}</p>
                  </div>
                </div>
              </div>

              <div>
                <p className="mb-2 flex items-center gap-2 text-base font-semibold">
                  <FaNewspaper className="h-4 w-4 text-emerald-600" />
                  Latest News
                </p>
                <div className="grid gap-2.5">
                  {activeRegion.news.map((newsItem) => (
                    <article
                      key={newsItem.title}
                      className="flex items-start gap-3 rounded-xl border border-emerald-100/90 bg-emerald-50/50 p-2.5"
                    >
                      <img
                        src={newsItem.image}
                        alt={newsItem.imageAlt}
                        className="h-14 w-20 shrink-0 rounded-lg object-cover"
                      />
                      <div className="min-w-0">
                        <h4 className="text-sm font-semibold leading-tight text-foreground">{newsItem.title}</h4>
                        <p className="mt-1 line-clamp-2 text-xs leading-relaxed text-muted-foreground">{newsItem.description}</p>
                      </div>
                    </article>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

export default HeroSection;
