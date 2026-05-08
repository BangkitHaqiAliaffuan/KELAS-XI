#!/bin/bash

# Quick Fix CORS - Hospital Navigator Backend
# This script helps you set ALLOWED_ORIGINS environment variable in Vercel

echo "🔧 Hospital Navigator - Quick CORS Fix"
echo "======================================="
echo ""

# Check if vercel CLI is installed
if ! command -v vercel &> /dev/null
then
    echo "❌ Vercel CLI not found!"
    echo ""
    echo "Install it first:"
    echo "  npm install -g vercel"
    echo ""
    exit 1
fi

echo "✅ Vercel CLI found"
echo ""

# Get frontend URL
echo "📝 Enter your frontend URL:"
echo "   Example: https://hospitalnavigator-lake.vercel.app"
read -p "Frontend URL: " FRONTEND_URL

if [ -z "$FRONTEND_URL" ]; then
    echo "❌ Frontend URL cannot be empty!"
    exit 1
fi

echo ""
echo "🔍 You entered: $FRONTEND_URL"
read -p "Is this correct? (y/n): " CONFIRM

if [ "$CONFIRM" != "y" ]; then
    echo "❌ Cancelled"
    exit 1
fi

echo ""
echo "🚀 Setting environment variable..."
echo ""

# Set environment variable
echo "$FRONTEND_URL" | vercel env add ALLOWED_ORIGINS production

echo ""
echo "✅ Environment variable set!"
echo ""
echo "📋 Next steps:"
echo "   1. Redeploy your backend:"
echo "      vercel --prod"
echo ""
echo "   2. Or redeploy via Vercel Dashboard:"
echo "      https://vercel.com/dashboard"
echo ""
echo "   3. Test CORS:"
echo "      curl -H \"Origin: $FRONTEND_URL\" \\"
echo "           -H \"Access-Control-Request-Method: GET\" \\"
echo "           -X OPTIONS \\"
echo "           https://your-backend.vercel.app/api/v1/rooms"
echo ""
echo "🎉 Done!"
