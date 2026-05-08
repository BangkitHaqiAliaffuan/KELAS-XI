import dotenv from 'dotenv';

dotenv.config();

// Default allowed origins for production if not set
const defaultProductionOrigins = [
  'https://hospitalnavigator-lake.vercel.app',
  'https://hospital-navigator-frontend.vercel.app',
].join(',');

export const config = {
  port: process.env.PORT || 3001,
  nodeEnv: process.env.NODE_ENV || 'development',
  corsOrigin: process.env.ALLOWED_ORIGINS || 
              process.env.CORS_ORIGIN || 
              (process.env.NODE_ENV === 'production' ? defaultProductionOrigins : 'http://localhost:5173'),
  apiPrefix: process.env.API_PREFIX || '/api/v1',
};

export default config;
