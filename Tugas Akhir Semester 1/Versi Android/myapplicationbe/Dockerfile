FROM php:8.3-fpm-alpine

# Install system dependencies and PHP extensions
RUN apk add --no-cache \
    nginx \
    icu-dev \
    libzip-dev \
    zip \
    unzip \
    curl \
    libxml2-dev \
    oniguruma-dev \
    dos2unix \
    && docker-php-ext-install \
        intl \
        zip \
        pdo \
        pdo_mysql \
        bcmath \
        mbstring \
        xml \
        pcntl \
        opcache

# Install Composer
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

WORKDIR /var/www/html

# Copy application files
COPY . .

# Install PHP dependencies
RUN composer install --optimize-autoloader --no-dev --no-interaction

# Set permissions
RUN chown -R www-data:www-data storage bootstrap/cache \
    && chmod -R 775 storage bootstrap/cache

# Fix line endings and make startup script executable
RUN dos2unix ./railway/init-app.sh && chmod +x ./railway/init-app.sh

# Copy nginx config (Railway-ready, reads PORT at runtime)
COPY docker/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

# Healthcheck for Railway
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD curl -f http://localhost:${PORT:-80}/up || exit 1

CMD ["/bin/sh", "./railway/init-app.sh"]
