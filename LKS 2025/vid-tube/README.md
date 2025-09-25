# YouTube Clone - React Application

A fully functional YouTube clone built with React.js that replicates the core features of YouTube, including video browsing, search functionality, and video playback with a responsive dark theme design.

## 🚀 Features

### Core Functionality
- **Home Page**: Browse popular videos with category filtering
- **Video Player**: Watch videos with embedded YouTube player
- **Search**: Search for videos using the YouTube Data API
- **Responsive Design**: Mobile-friendly layout that adapts to different screen sizes
- **Dark Theme**: YouTube-style dark theme throughout the application

### Technical Features
- **React Router**: Client-side routing for seamless navigation
- **YouTube Data API**: Real-time video data fetching
- **Component Architecture**: Modular, reusable React components
- **Loading States**: Loading indicators for better user experience
- **Error Handling**: Graceful error handling for API failures
- **Responsive Grid**: Dynamic video grid layout

## 📋 Prerequisites

Before running this application, make sure you have:

- **Node.js** (version 14 or higher)
- **npm** or **yarn** package manager
- **YouTube Data API Key** (see setup instructions below)

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd vid-tube
```

### 2. Install Dependencies
```bash
npm install
# or
yarn install
```

### 3. Get YouTube Data API Key

1. Go to the [Google Cloud Console](https://console.developers.google.com/)
2. Create a new project or select an existing project
3. Enable the **YouTube Data API v3**:
   - Go to "APIs & Services" > "Library"
   - Search for "YouTube Data API v3"
   - Click "Enable"
4. Create credentials:
   - Go to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "API Key"
   - Copy the generated API key

### 4. Environment Configuration

1. Copy the example environment file:
```bash
cp .env.example .env
```

2. Open `.env` file and add your YouTube API key:
```env
VITE_YOUTUBE_API_KEY=your_actual_api_key_here
```

**Important**: Never commit your `.env` file to version control. It's already included in `.gitignore`.

### 5. Run the Application
```bash
npm run dev
# or
yarn dev
```

The application will start on `http://localhost:5173`

## 📁 Project Structure

```
src/
├── components/           # Reusable React components
│   ├── Navbar.jsx       # Navigation bar with search
│   ├── Sidebar.jsx      # Category sidebar
│   ├── VideoCard.jsx    # Individual video card
│   ├── Feed.jsx         # Video grid display
│   ├── PlayVideo.jsx    # Video player component
│   ├── Recommended.jsx  # Recommended videos
│   └── *.css           # Component stylesheets
├── pages/               # Page components
│   ├── Home.jsx        # Home page
│   ├── Video.jsx       # Video player page
│   └── *.css          # Page stylesheets
├── data/               # Data layer
│   ├── api.js         # YouTube API utilities
│   └── staticData.js  # Static data and constants
├── assets/            # Static assets (images, icons)
│   ├── assets.js      # Asset exports
│   └── *.png         # Image files
├── App.jsx            # Main application component
├── App.css           # Global app styles
├── index.css         # Global base styles
└── main.jsx          # Application entry point
```

## 🎨 Component Overview

### Core Components
- **Navbar**: Search functionality and navigation
- **Sidebar**: Category filtering and navigation menu
- **VideoCard**: Displays video thumbnail, title, and metadata
- **Feed**: Grid layout for displaying multiple videos
- **PlayVideo**: Embedded video player with video details
- **Recommended**: Shows related videos on video pages

### Pages
- **Home**: Main page with video feed and category filtering
- **Video**: Individual video page with player and recommendations

## 🔧 API Integration

The application uses the YouTube Data API v3 for:
- Fetching popular videos by category
- Searching videos by keywords
- Getting video details and statistics
- Retrieving channel information
- Loading related/recommended videos

### API Rate Limits
- YouTube Data API has daily quota limits
- Free tier: 10,000 units per day
- Each API call consumes different units (1-100+ units)
- Monitor your usage in Google Cloud Console

## 📱 Responsive Design

The application is fully responsive with breakpoints:
- **Desktop**: 1200px+
- **Tablet**: 768px - 1199px
- **Mobile**: < 768px

### Mobile Features
- Collapsible sidebar
- Touch-friendly interface
- Optimized video grid
- Responsive navigation

## 🚀 Build for Production

```bash
npm run build
# or
yarn build
```

This creates a `dist` folder with optimized production files.

### Preview Production Build
```bash
npm run preview
# or
yarn preview
```

## 🎯 Usage Examples

### Searching for Videos
1. Use the search bar in the navigation
2. Enter keywords and press Enter
3. Results will display in the main feed

### Browsing by Category
1. Use the sidebar categories (Gaming, Music, etc.)
2. Click any category to filter videos
3. Videos will update automatically

### Watching Videos
1. Click any video thumbnail
2. Navigate to dedicated video page
3. Video plays automatically
4. View recommendations on the side

## 🐛 Troubleshooting

### Common Issues

**"Failed to load videos" Error**
- Check your YouTube API key in `.env`
- Verify API key has YouTube Data API v3 enabled
- Check browser console for detailed error messages

**Videos Not Loading**
- Ensure internet connection is stable
- Check API quota limits in Google Cloud Console
- Verify API key permissions

**Layout Issues**
- Clear browser cache
- Check for console errors
- Ensure all CSS files are loading properly

## 🔐 Security Considerations

- **API Key**: Never expose API keys in client-side code in production
- **Environment Variables**: Use proper environment management
- **CORS**: Configure CORS properly for production deployment
- **Rate Limiting**: Implement client-side rate limiting for API calls

## 🚀 Deployment

### Netlify
1. Build the project: `npm run build`
2. Deploy the `dist` folder to Netlify
3. Set environment variables in Netlify dashboard

### Vercel
1. Connect your GitHub repository
2. Set environment variables in Vercel dashboard
3. Deploy automatically on push

### Firebase Hosting
1. Install Firebase CLI: `npm install -g firebase-tools`
2. Initialize Firebase: `firebase init hosting`
3. Deploy: `firebase deploy`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit changes: `git commit -m 'Add feature'`
4. Push to branch: `git push origin feature-name`
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- YouTube Data API v3 for providing video data
- React team for the amazing framework
- Vite for fast development experience
- All contributors and open-source libraries used

## 📞 Support

If you encounter any issues or have questions:
1. Check the troubleshooting section above
2. Search existing GitHub issues
3. Create a new issue with detailed information
4. Include error messages and steps to reproduce

---

**Happy coding! 🎉**+ Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## React Compiler

The React Compiler is not enabled on this template. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
