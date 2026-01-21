export default function Features() {
  const features = [
    {
      icon: "⚡",
      title: "Lightning Fast",
      description: "Merge PDFs instantly with our optimized processing engine. No waiting, no delays."
    },
    {
      icon: "🔒",
      title: "Secure & Private",
      description: "Your files are processed locally in your browser. We never store or access your documents."
    },
    {
      icon: "🎯",
      title: "Simple to Use",
      description: "Drag and drop your PDFs, reorder them as needed, and download your merged file in seconds."
    }
  ];

  return (
    <section className="py-20 px-4 bg-white">
      <div className="max-w-6xl mx-auto">
        <h2 className="text-4xl font-bold text-center text-gray-900 mb-4">
          Why Choose Our PDF Merger?
        </h2>
        <p className="text-center text-gray-600 mb-12 max-w-2xl mx-auto">
          The fastest and most secure way to combine your PDF documents online
        </p>
        
        <div className="grid md:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <div key={index} className="bg-blue-50 rounded-xl p-8 hover:shadow-lg transition-shadow">
              <div className="text-4xl mb-4">{feature.icon}</div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">
                {feature.title}
              </h3>
              <p className="text-gray-600">
                {feature.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
