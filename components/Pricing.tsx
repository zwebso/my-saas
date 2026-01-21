export default function Pricing() {
  const plans = [
    {
      name: "Free",
      price: "$0",
      period: "forever",
      features: [
        "Merge up to 5 PDFs",
        "10 MB file size limit",
        "Basic support",
        "No credit card required"
      ],
      cta: "Get Started",
      highlighted: false
    },
    {
      name: "Pro",
      price: "$9",
      period: "per month",
      features: [
        "Unlimited PDF merges",
        "100 MB file size limit",
        "Priority support",
        "Batch processing",
        "Advanced features"
      ],
      cta: "Start Free Trial",
      highlighted: true
    },
    {
      name: "Enterprise",
      price: "$49",
      period: "per month",
      features: [
        "Everything in Pro",
        "Unlimited file size",
        "API access",
        "Custom integrations",
        "Dedicated support",
        "SLA guarantee"
      ],
      cta: "Contact Sales",
      highlighted: false
    }
  ];

  return (
    <section className="py-20 px-4 bg-blue-50">
      <div className="max-w-6xl mx-auto">
        <h2 className="text-4xl font-bold text-center text-gray-900 mb-4">
          Simple, Transparent Pricing
        </h2>
        <p className="text-center text-gray-600 mb-12 max-w-2xl mx-auto">
          Choose the plan that works best for you
        </p>
        
        <div className="grid md:grid-cols-3 gap-8">
          {plans.map((plan, index) => (
            <div
              key={index}
              className={`bg-white rounded-xl p-8 ${
                plan.highlighted
                  ? "ring-2 ring-blue-600 shadow-xl scale-105"
                  : "shadow-lg"
              } hover:shadow-2xl transition-all`}
            >
              {plan.highlighted && (
                <div className="text-center mb-4">
                  <span className="bg-blue-600 text-white px-3 py-1 rounded-full text-sm font-semibold">
                    Most Popular
                  </span>
                </div>
              )}
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                {plan.name}
              </h3>
              <div className="mb-6">
                <span className="text-4xl font-bold text-gray-900">
                  {plan.price}
                </span>
                <span className="text-gray-600 ml-2">/ {plan.period}</span>
              </div>
              <ul className="space-y-3 mb-8">
                {plan.features.map((feature, featureIndex) => (
                  <li key={featureIndex} className="flex items-start">
                    <svg
                      className="w-5 h-5 text-blue-600 mr-2 flex-shrink-0 mt-0.5"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                    <span className="text-gray-600">{feature}</span>
                  </li>
                ))}
              </ul>
              <button
                className={`w-full py-3 px-6 rounded-lg font-semibold transition-colors ${
                  plan.highlighted
                    ? "bg-blue-600 hover:bg-blue-700 text-white"
                    : "bg-gray-100 hover:bg-gray-200 text-gray-900"
                }`}
              >
                {plan.cta}
              </button>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
