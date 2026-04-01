#!/bin/bash
set -e

echo "==> [Railway] Starting Laravel app initialization..."

# Ensure we are in the correct working directory
cd /var/www/html

# Use Railway's PORT env var, default to 80 if not set
APP_PORT="${PORT:-80}"
echo "==> [Railway] Listening on port: $APP_PORT"

# Replace port placeholder in nginx config
sed -i "s/__PORT__/$APP_PORT/g" /etc/nginx/nginx.conf

# Ensure storage and cache directories exist and are writable
mkdir -p storage/framework/{sessions,views,cache} storage/logs bootstrap/cache
chmod -R 777 storage bootstrap/cache 2>/dev/null || true

# Clear only file-based cache first (safe before DB exists)
echo "==> [Railway] Clearing file-based cache..."
php artisan config:clear
php artisan view:clear
php artisan route:clear
php artisan event:clear

echo "==> [Railway] Creating storage symlink..."
php artisan storage:link --quiet || true

echo "==> [Railway] Running database migrations..."
php artisan migrate --force

# Now safe to clear DB-based cache (table exists after migrate)
echo "==> [Railway] Clearing app cache (post-migrate)..."
php artisan cache:clear || true

echo "==> [Railway] Publishing Filament and Livewire assets..."
php artisan vendor:publish --tag=livewire:assets --force
php artisan vendor:publish --tag=filament-assets --force
php artisan filament:assets

echo "==> [Railway] Caching config and views (NO route cache - Filament incompatible)..."
php artisan config:cache
php artisan view:cache

echo "==> [Railway] Starting PHP-FPM in background..."
php-fpm -D

echo "==> [Railway] Starting Nginx..."
nginx -g "daemon off;"
