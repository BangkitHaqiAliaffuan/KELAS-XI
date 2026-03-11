/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        'green-primary':   '#2E7D32',
        'green-medium':    '#388E3C',
        'green-light':     '#E8F5E9',
        'green-dark':      '#1B5E20',
        'orange-accent':   '#FF6F00',
        'orange-dark':     '#E65100',
        'orange-light':    '#FFF3E0',
        'text-primary':    '#212121',
        'text-secondary':  '#757575',
        'text-hint':       '#BDBDBD',
        'surface':         '#F9FBF9',
        'surface-white':   '#FFFFFF',
        'divider':         '#E0E0E0',
        'status-done':     '#388E3C',
        'status-cancelled':'#D32F2F',
        'status-pending':  '#FFA000',
        'status-shipped':  '#1976D2',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      maxWidth: {
        'app': '430px',
      },
      screens: {
        'xs': '375px',
      },
    },
  },
  plugins: [],
};
