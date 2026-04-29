import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import compression from 'compression';
import config from './config/index.js';
import routes from './routes/index.js';
import { errorHandler, notFoundHandler } from './middleware/errorHandler.js';

const app = express();

// Security middleware
app.use(helmet());

// CORS configuration - support multiple origins
const allowedOrigins = config.corsOrigin.split(',').map(origin => origin.trim());
app.use(
  cors({
    origin: function (origin, callback) {
      // Allow requests with no origin (like mobile apps or curl requests)
      if (!origin) return callback(null, true);
      
      if (allowedOrigins.indexOf(origin) !== -1) {
        callback(null, true);
      } else {
        console.warn(`[CORS] Blocked request from origin: ${origin}`);
        callback(new Error('Not allowed by CORS'));
      }
    },
    credentials: true,
  })
);

// Body parsing middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Compression middleware
app.use(compression());

// Logging middleware
if (config.nodeEnv === 'development') {
  app.use(morgan('dev'));
} else {
  app.use(morgan('combined'));
}

// API routes
app.use(config.apiPrefix, routes);

// Root endpoint
app.get('/', (req, res) => {
  res.json({
    success: true,
    message: 'Hospital Navigator API',
    version: '1.0.0',
    endpoints: {
      health: `${config.apiPrefix}/health`,
      rooms: `${config.apiPrefix}/rooms`,
      qrAnchors: `${config.apiPrefix}/qr-anchors`,
    },
  });
});

// 404 handler
app.use(notFoundHandler);

// Error handler
app.use(errorHandler);

// Start server
const PORT = config.port;
app.listen(PORT, () => {
  console.log(`
╔═══════════════════════════════════════════════════════════╗
║                                                           ║
║   🏥 Hospital Navigator API Server                       ║
║                                                           ║
║   Environment: ${config.nodeEnv.padEnd(43)}║
║   Port: ${String(PORT).padEnd(50)}║
║   API Prefix: ${config.apiPrefix.padEnd(44)}║
║                                                           ║
║   Endpoints:                                              ║
║   - GET  ${config.apiPrefix}/health                           ║
║   - GET  ${config.apiPrefix}/rooms                            ║
║   - GET  ${config.apiPrefix}/qr-anchors                       ║
║                                                           ║
║   Server running at: http://localhost:${PORT}              ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝
  `);
});

export default app;
