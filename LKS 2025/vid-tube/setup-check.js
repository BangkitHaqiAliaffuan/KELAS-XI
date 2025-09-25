#!/usr/bin/env node

// Setup verification script for YouTube Clone
const fs = require('fs');
const path = require('path');

console.log('🚀 YouTube Clone - Setup Verification\n');

// Check if package.json exists and has required dependencies
const packageJsonPath = path.join(__dirname, 'package.json');
if (fs.existsSync(packageJsonPath)) {
  const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
  const requiredDeps = ['react', 'react-dom', 'react-router-dom', 'moment'];
  const missingDeps = requiredDeps.filter(dep => !packageJson.dependencies[dep]);
  
  if (missingDeps.length === 0) {
    console.log('✅ All required dependencies are installed');
  } else {
    console.log('❌ Missing dependencies:', missingDeps.join(', '));
    console.log('   Run: npm install react-router-dom moment');
  }
} else {
  console.log('❌ package.json not found');
}

// Check if .env file exists
const envPath = path.join(__dirname, '.env');
if (fs.existsSync(envPath)) {
  const envContent = fs.readFileSync(envPath, 'utf8');
  if (envContent.includes('VITE_YOUTUBE_API_KEY=your_youtube_api_key_here')) {
    console.log('⚠️  Environment file exists but API key needs to be configured');
    console.log('   Replace "your_youtube_api_key_here" with your actual YouTube API key');
  } else if (envContent.includes('VITE_YOUTUBE_API_KEY=')) {
    console.log('✅ Environment file configured (API key present)');
  } else {
    console.log('❌ Environment file missing VITE_YOUTUBE_API_KEY');
  }
} else {
  console.log('⚠️  .env file not found - copying from .env.example');
  if (fs.existsSync('.env.example')) {
    fs.copyFileSync('.env.example', '.env');
    console.log('✅ Created .env file from template');
  }
}

// Check core component files
const coreFiles = [
  'src/App.jsx',
  'src/components/Navbar.jsx',
  'src/components/Sidebar.jsx',
  'src/components/VideoCard.jsx',
  'src/components/Feed.jsx',
  'src/components/PlayVideo.jsx',
  'src/components/Recommended.jsx',
  'src/pages/Home.jsx',
  'src/pages/Video.jsx',
  'src/data/api.js',
  'src/assets/assets.js'
];

const missingFiles = coreFiles.filter(file => !fs.existsSync(path.join(__dirname, file)));

if (missingFiles.length === 0) {
  console.log('✅ All core component files are present');
} else {
  console.log('❌ Missing core files:', missingFiles.join(', '));
}

console.log('\n📋 Next Steps:');
console.log('1. Configure your YouTube API key in .env file');
console.log('2. Run: npm run dev');
console.log('3. Open: http://localhost:5173');
console.log('\n📖 For detailed setup instructions, see README.md');
console.log('\n🎉 Happy coding!');