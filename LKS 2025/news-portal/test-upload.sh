#!/bin/bash

# Test script for image upload
# Make sure to replace YOUR_TOKEN with actual Bearer token

echo "Testing POST request with form-data..."

# Test 1: Create post without image
echo "=== Test 1: Post without image ==="
curl -X POST http://localhost:8000/api/posts \
  -H "Accept: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "title=Test Post Without Image" \
  -F "news_content=This is test content without image"

echo -e "\n\n"

# Test 2: Create post with image (you need to put a test image in the same folder)
echo "=== Test 2: Post with image ==="
curl -X POST http://localhost:8000/api/posts \
  -H "Accept: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "title=Test Post With Image" \
  -F "news_content=This is test content with image" \
  -F "file=@test-image.jpg"

echo -e "\n\n"
