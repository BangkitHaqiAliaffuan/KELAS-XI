import Sidebar from "@/components/Sidebar";
import HeroSection from "@/components/HeroSection";

const Index = () => {
  return (
    <div className="flex min-h-screen ">
      <Sidebar />
      <main className="ml-24 flex-1">
        <HeroSection />
      </main>
    </div>
  );
};

export default Index;
