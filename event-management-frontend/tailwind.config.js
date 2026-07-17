/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1400px",
      },
    },
    extend: {
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
      colors: {
        border: "#E5E7EB",
        input: "#E5E7EB",
        ring: "#2563EB",
        background: "#FFFFFF",
        foreground: "#111827",
        primary: {
          DEFAULT: "#2563EB",
          foreground: "#FFFFFF",
        },
        secondary: {
          DEFAULT: "#F3F4F6", // using slightly lighter gray for secondary background
          foreground: "#111827",
        },
        destructive: {
          DEFAULT: "#DC2626",
          foreground: "#FFFFFF",
        },
        muted: {
          DEFAULT: "#F8F9FA",
          foreground: "#6B7280",
        },
        accent: {
          DEFAULT: "#F8F9FA",
          foreground: "#111827",
        },
        popover: {
          DEFAULT: "#FFFFFF",
          foreground: "#111827",
        },
        card: {
          DEFAULT: "#F8F9FA",
          foreground: "#111827",
        },
        success: "#16A34A",
        warning: "#F59E0B",
        danger: "#DC2626"
      },
      borderRadius: {
        lg: "0.5rem",
        md: "0.375rem",
        sm: "0.25rem",
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
      },
      keyframes: {
        "accordion-down": {
          from: { height: "0" },
          to: { height: "var(--radix-accordion-content-height)" },
        },
        "accordion-up": {
          from: { height: "var(--radix-accordion-content-height)" },
          to: { height: "0" },
        }
      },
    },
  },
  plugins: [],
}
