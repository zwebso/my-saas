import Hero from '@/components/Hero';
import Features from '@/components/Features';
import PDFMerger from '@/components/PDFMerger';
import Pricing from '@/components/Pricing';
import Footer from '@/components/Footer';

export default function Home() {
  return (
    <main className="min-h-screen">
      <Hero />
      <PDFMerger />
      <Features />
      <Pricing />
      <Footer />
    </main>
  );
}
