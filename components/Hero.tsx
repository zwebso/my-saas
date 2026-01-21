// @ts-nocheck
'use client'
import Link from 'next/link'

export default function Hero() {
  return (
    <section className="bg-gradient-to-b from-blue-50 to-white py-20 px-4">
      <div className="max-w-6xl mx-auto text-center">
        <h1 className="text-5xl md:text-6xl font-bold text-gray-900 mb-6">
          Merge PDFs in Seconds
        </h1>
        <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
          Combine multiple PDF files into one document with our fast, secure, and easy-to-use online tool. No installation required.
        </p>
        <Link href="/auth" className="inline-block bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-4 rounded-lg text-lg transition-colors shadow-lg hover:shadow-xl">
          Sign Up Free
        </Link>
        <p className="text-sm text-gray-500 mt-4">
          No credit card required • Free forever
        </p>
      </div>
    </section>
  );
}
