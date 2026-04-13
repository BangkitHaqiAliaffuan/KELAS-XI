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

const regionPopupData: Record<number, RegionPopupData> = {
  2: {
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
    facts: ["Pusat edukasi sejarah dan budaya Sidoarjo", "Menyimpan koleksi benda cagar budaya Jawa Timur"],
    population: "80.420",
    area: "41.56 km²",
    news: [
      {
        title: "Tur Edukasi Sekolah Diperluas",
        description: "Program kunjungan pelajar ke Museum Mpu Tantular ditambah untuk memperkuat literasi sejarah lokal.",
        image: "/images/places/buduran/ARCA%20DI%20MUSEUM%20MPU%20TANTULAR.jpg",
        imageAlt: "Tur edukasi di Museum Mpu Tantular",
      },
      {
        title: "Pameran Budaya Akhir Pekan",
        description: "Agenda pameran artefak dan budaya daerah Buduran dibuka untuk publik pada akhir pekan ini.",
        image: "/images/places/buduran/Tuin%20bij%20een%20woning%2C%20vermoedelijk%20van%20onderneming%20Bandjar-Redjo%20te%20Boedoeran%20bij%20Sidoardjo%2C%20ca%201900.jpg",
        imageAlt: "Pameran budaya Buduran",
      },
    ],
  },
  4: {
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
    facts: ["Simpul mobilitas utama menuju Bandara Juanda", "Kawasan strategis penghubung Surabaya-Sidoarjo"],
    population: "93.170",
    area: "24.06 km²",
    news: [
      {
        title: "Akses Flyover Juanda Ditata",
        description: "Peningkatan kualitas akses dan arus lalu lintas sekitar Flyover Juanda terus dipercepat.",
        image: "/images/places/gedangan/gedangan-1-e1754389894765.jpg",
        imageAlt: "Penataan akses Flyover Juanda",
      },
      {
        title: "Jalur Pendukung Transportasi",
        description: "Pengembangan jalur pendukung transportasi di Gedangan sedang berjalan untuk konektivitas yang lebih baik.",
        image: "/images/places/gedangan/unnamed.jpg",
        imageAlt: "Pengembangan jalur transportasi Gedangan",
      },
    ],
  },
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
          <div className="relative h-[clamp(360px,58vh,620px)] w-full overflow-hidden rounded-2xl border border-emerald-100/70 bg-emerald-50/40">
            <object
              ref={mapObjectRef}
              onLoad={bindRegionPopupEvents}
              data="/images/sidoarjo-map.svg"
              type="image/svg+xml"
              aria-label="Peta Kabupaten Sidoarjo interaktif"
              className="absolute left-1/2 top-1/2 h-[155%] w-[155%] -translate-x-1/2 -translate-y-[50%] drop-shadow-lg"
            >
              <img
                src="/images/sidoarjo-map.svg"
                alt="Peta Kabupaten Sidoarjo"
                className="absolute left-1/2 top-1/2 h-[155%] w-[155%] -translate-x-1/2 -translate-y-[50%] drop-shadow-lg"
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
            <div className="relative h-48 w-full bg-emerald-50">
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
                <ul className="space-y-1.5 text-sm text-muted-foreground">
                  {activeRegion.facts.map((fact) => (
                    <li key={fact}>• {fact}</li>
                  ))}
                </ul>
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
