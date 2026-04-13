import { ArrowRight } from "lucide-react";

const HeroSection = () => {
  return (
    <section className="relative flex min-h-screen flex-col">
      {/* Header */}
      <div className="flex items-center justify-between px-8 py-6">
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
      <div className="flex flex-1 items-center justify-center px-8">
        <div className="animate-fade-in w-full max-w-4xl">
          <img
            src="/images/sidoarjo-map.svg"
            alt="Peta Kabupaten Sidoarjo"
            className="h-auto w-full drop-shadow-lg"
          />
        </div>
      </div>

      {/* Leader Section */}
      <div className="relative mx-auto -mb-2 flex w-full max-w-4xl items-end justify-center">
        <div className="flex w-full items-end rounded-t-2xl bg-leader-bg shadow-lg">
          {/* Bupati */}
          <div className="flex flex-1 items-end gap-4">
            <img
              src="/images/bupati.png"
              alt="Bupati Sidoarjo"
              className="h-48 w-auto -ml-4 -mb-0 object-contain"
              loading="lazy"
            />
            <div className="pb-6">
              <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                Bupati Sidoarjo
              </p>
              <p className="text-lg font-bold text-foreground">
                H. SUBANDI
              </p>
            </div>
          </div>

          {/* Divider */}
          <div className="h-20 w-px bg-border self-center" />

          {/* Wakil Bupati */}
          <div className="flex flex-1 flex-row-reverse items-end gap-4">
            <img
              src="/images/wakil-bupati.png"
              alt="Wakil Bupati Sidoarjo"
              className="h-48 w-auto -mr-4 -mb-0 object-contain"
              loading="lazy"
            />
            <div className="pb-6 text-right">
              <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                Wakil Bupati Sidoarjo
              </p>
              <p className="text-lg font-bold text-foreground">
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
    </section>
  );
};

export default HeroSection;
