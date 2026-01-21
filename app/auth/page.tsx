'use client'
import { Auth } from '@supabase/auth-ui-react'
import { ThemeSupa } from '@supabase/auth-ui-shared'
import { supabase } from '../../lib/supabase'
import Link from 'next/link'

export default function AuthPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-white flex items-center justify-center">
      <div className="max-w-md w-full mx-auto">
        <div className="text-center mb-8">
          <Link href="/" className="text-2xl font-bold text-blue-600">
            PDF Merge Tool
          </Link>
          <p className="text-gray-600 mt-2">Sign in to your account</p>
        </div>
        
        <div className="bg-white rounded-lg shadow-lg p-8">
          <Auth
            supabaseClient={supabase}
            appearance={{ 
              theme: ThemeSupa,
              variables: {
                default: {
                  colors: {
                    brand: '#2563eb',
                    brandAccent: '#1d4ed8'
                  }
                }
              }
            }}
            theme="light"
            providers={['google', 'github']}
            redirectTo={`${typeof window !== 'undefined' ? window.location.origin : ''}/dashboard`}
          />
        </div>
      </div>
    </div>
  )
}
