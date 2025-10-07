/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#fef7ee',
          100: '#fdead6',
          200: '#fad2ac',
          300: '#f6b177',
          400: '#f18740',
          500: '#ed6b1a',
          600: '#de5210',
          700: '#b83d10',
          800: '#933115',
          900: '#762b14',
          950: '#401308',
        }
      },
      fontFamily: {
        'sans': ['Inter', 'system-ui', 'sans-serif'],
      }
    },
  },
  plugins: [],
}