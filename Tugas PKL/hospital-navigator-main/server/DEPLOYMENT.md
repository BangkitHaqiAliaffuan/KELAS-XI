# Hospital Navigator Backend - Deployment Guide

Panduan deployment untuk Hospital Navigator Backend API.

## 📋 Prerequisites

- Node.js >= 16.x
- npm atau yarn
- Git (untuk version control)

## 🚀 Development Setup

### 1. Clone & Install

```bash
cd server
npm install
```

### 2. Environment Configuration

```bash
cp .env.example .env
```

Edit `.env`:
```env
PORT=3001
NODE_ENV=development
CORS_ORIGIN=http://localhost:5173
API_PREFIX=/api/v1
```

### 3. Start Development Server

```bash
npm run dev
```

Server akan berjalan di `http://localhost:3001` dengan hot-reload.

---

## 🏭 Production Deployment

### Option 1: Traditional Server (VPS/Dedicated)

#### 1. Prepare Server

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Node.js (using NodeSource)
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Verify installation
node --version
npm --version
```

#### 2. Deploy Application

```bash
# Clone repository
git clone <your-repo-url>
cd hospital-navigator/server

# Install dependencies (production only)
npm install --production

# Create .env file
nano .env
```

Production `.env`:
```env
PORT=3001
NODE_ENV=production
CORS_ORIGIN=https://your-frontend-domain.com
API_PREFIX=/api/v1
```

#### 3. Use Process Manager (PM2)

```bash
# Install PM2 globally
sudo npm install -g pm2

# Start application
pm2 start src/index.js --name hospital-api

# Save PM2 configuration
pm2 save

# Setup PM2 to start on boot
pm2 startup
```

PM2 Commands:
```bash
pm2 status              # Check status
pm2 logs hospital-api   # View logs
pm2 restart hospital-api # Restart
pm2 stop hospital-api   # Stop
pm2 delete hospital-api # Remove
```

#### 4. Setup Nginx Reverse Proxy

```bash
sudo apt install nginx -y
```

Create Nginx configuration:
```bash
sudo nano /etc/nginx/sites-available/hospital-api
```

```nginx
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://localhost:3001;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/hospital-api /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

#### 5. Setup SSL with Let's Encrypt

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d api.yourdomain.com
```

---

### Option 2: Docker Deployment

#### 1. Create Dockerfile

```dockerfile
# server/Dockerfile
FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Expose port
EXPOSE 3001

# Start application
CMD ["node", "src/index.js"]
```

#### 2. Create docker-compose.yml

```yaml
# server/docker-compose.yml
version: '3.8'

services:
  api:
    build: .
    ports:
      - "3001:3001"
    environment:
      - NODE_ENV=production
      - PORT=3001
      - CORS_ORIGIN=https://your-frontend-domain.com
      - API_PREFIX=/api/v1
    restart: unless-stopped
    volumes:
      - ./logs:/app/logs
```

#### 3. Build and Run

```bash
# Build image
docker build -t hospital-api .

# Run container
docker run -d \
  --name hospital-api \
  -p 3001:3001 \
  -e NODE_ENV=production \
  -e CORS_ORIGIN=https://your-frontend-domain.com \
  hospital-api

# Or use docker-compose
docker-compose up -d
```

Docker Commands:
```bash
docker ps                    # List containers
docker logs hospital-api     # View logs
docker restart hospital-api  # Restart
docker stop hospital-api     # Stop
docker rm hospital-api       # Remove
```

---

### Option 3: Cloud Platforms

#### Heroku

1. Install Heroku CLI
2. Create `Procfile`:
```
web: node src/index.js
```

3. Deploy:
```bash
heroku login
heroku create hospital-navigator-api
git push heroku main
heroku config:set NODE_ENV=production
heroku config:set CORS_ORIGIN=https://your-frontend.com
```

#### Railway

1. Connect GitHub repository
2. Set environment variables in dashboard
3. Deploy automatically on push

#### Render

1. Connect repository
2. Set build command: `npm install`
3. Set start command: `node src/index.js`
4. Add environment variables

#### DigitalOcean App Platform

1. Connect repository
2. Configure build settings
3. Set environment variables
4. Deploy

---

## 🔒 Security Checklist

- [ ] Set `NODE_ENV=production`
- [ ] Use strong CORS configuration
- [ ] Enable HTTPS/SSL
- [ ] Set proper environment variables
- [ ] Use process manager (PM2)
- [ ] Setup firewall rules
- [ ] Regular security updates
- [ ] Monitor logs
- [ ] Implement rate limiting (future)
- [ ] Add authentication (future)

---

## 📊 Monitoring

### PM2 Monitoring

```bash
# Real-time monitoring
pm2 monit

# Web dashboard
pm2 plus
```

### Log Management

```bash
# View logs
pm2 logs hospital-api

# Clear logs
pm2 flush

# Rotate logs
pm2 install pm2-logrotate
```

### Health Checks

Setup monitoring service to ping:
```
GET http://your-api-domain.com/api/v1/health
```

Expected response:
```json
{
  "success": true,
  "message": "Hospital Navigator API is running",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Example

```yaml
# .github/workflows/deploy.yml
name: Deploy API

on:
  push:
    branches: [main]
    paths:
      - 'server/**'

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v2
      
      - name: Setup Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '18'
      
      - name: Install dependencies
        run: |
          cd server
          npm ci
      
      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd /path/to/hospital-navigator/server
            git pull
            npm install --production
            pm2 restart hospital-api
```

---

## 🧪 Testing Before Deployment

```bash
# Run in production mode locally
NODE_ENV=production node src/index.js

# Test endpoints
curl http://localhost:3001/api/v1/health
curl http://localhost:3001/api/v1/rooms
curl http://localhost:3001/api/v1/qr-anchors
```

---

## 📝 Maintenance

### Update Application

```bash
# Pull latest changes
git pull origin main

# Install new dependencies
npm install --production

# Restart application
pm2 restart hospital-api
```

### Backup Data

Since this is an in-memory database, consider:
1. Exporting data periodically
2. Version control for data files
3. Database migration (future enhancement)

### Monitor Performance

```bash
# Check CPU/Memory usage
pm2 status

# Detailed monitoring
pm2 monit
```

---

## 🆘 Troubleshooting

### Port Already in Use

```bash
# Find process using port 3001
lsof -i :3001

# Kill process
kill -9 <PID>
```

### Permission Denied

```bash
# Fix file permissions
chmod +x src/index.js

# Run with sudo (not recommended)
sudo pm2 start src/index.js
```

### CORS Issues

Check `.env` file:
```env
CORS_ORIGIN=https://your-frontend-domain.com
```

Multiple origins:
```javascript
// src/config/index.js
corsOrigin: process.env.CORS_ORIGIN?.split(',') || 'http://localhost:5173'
```

### Application Crashes

```bash
# Check logs
pm2 logs hospital-api --lines 100

# Check error logs
pm2 logs hospital-api --err

# Restart application
pm2 restart hospital-api
```

---

## 📞 Support

For issues or questions:
1. Check logs: `pm2 logs hospital-api`
2. Review documentation
3. Check GitHub issues
4. Contact development team

---

## 🔄 Rollback Strategy

```bash
# View PM2 history
pm2 list

# Revert to previous version
git checkout <previous-commit>
npm install --production
pm2 restart hospital-api

# Or use PM2 ecosystem file for versioning
pm2 start ecosystem.config.js
```

---

## 📈 Scaling

### Horizontal Scaling

```bash
# Start multiple instances
pm2 start src/index.js -i max --name hospital-api

# Or specify number of instances
pm2 start src/index.js -i 4 --name hospital-api
```

### Load Balancing

Use Nginx for load balancing:
```nginx
upstream hospital_api {
    server localhost:3001;
    server localhost:3002;
    server localhost:3003;
    server localhost:3004;
}

server {
    location / {
        proxy_pass http://hospital_api;
    }
}
```

---

## ✅ Post-Deployment Checklist

- [ ] Application is running
- [ ] Health check endpoint responds
- [ ] All API endpoints work
- [ ] CORS is configured correctly
- [ ] SSL/HTTPS is enabled
- [ ] Logs are being written
- [ ] Monitoring is active
- [ ] Backup strategy in place
- [ ] Documentation is updated
- [ ] Team is notified
