// YouTube Data API utility functions
const API_KEY = import.meta.env.VITE_YOUTUBE_API_KEY;
const BASE_URL = 'https://www.googleapis.com/youtube/v3';

// Default parameters for all API calls
const DEFAULT_PARAMS = {
  key: API_KEY,
  part: 'snippet',
  maxResults: 50,
};

// Function to build API URL with parameters
const buildApiUrl = (endpoint, params = {}) => {
  const allParams = { ...DEFAULT_PARAMS, ...params };
  const queryString = new URLSearchParams(allParams).toString();
  return `${BASE_URL}/${endpoint}?${queryString}`;
};

// Fetch popular videos (for home page)
export const fetchPopularVideos = async (categoryId = '') => {
  try {
    const params = {
      part: 'snippet,statistics',
      chart: 'mostPopular',
      regionCode: 'US',
      maxResults: 50,
    };
    
    if (categoryId && categoryId !== '0') {
      params.videoCategoryId = categoryId;
    }
    
    const url = buildApiUrl('videos', params);
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }
    
    const data = await response.json();
    return data.items || [];
  } catch (error) {
    console.error('Error fetching popular videos:', error);
    return [];
  }
};

// Search videos by query
export const searchVideos = async (query) => {
  try {
    const params = {
      part: 'snippet',
      type: 'video',
      q: query,
      maxResults: 50,
    };
    
    const url = buildApiUrl('search', params);
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }
    
    const data = await response.json();
    
    // Get video IDs to fetch statistics
    const videoIds = data.items.map(item => item.id.videoId).join(',');
    
    // Fetch video statistics
    const statsUrl = buildApiUrl('videos', {
      part: 'statistics',
      id: videoIds,
    });
    
    const statsResponse = await fetch(statsUrl);
    const statsData = await statsResponse.json();
    
    // Combine search results with statistics
    const videosWithStats = data.items.map(video => {
      const stats = statsData.items?.find(stat => stat.id === video.id.videoId);
      return {
        ...video,
        id: video.id.videoId, // Normalize the id structure
        statistics: stats?.statistics || {},
      };
    });
    
    return videosWithStats;
  } catch (error) {
    console.error('Error searching videos:', error);
    return [];
  }
};

// Fetch video details by ID
export const fetchVideoDetails = async (videoId) => {
  try {
    const params = {
      part: 'snippet,statistics',
      id: videoId,
    };
    
    const url = buildApiUrl('videos', params);
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }
    
    const data = await response.json();
    return data.items?.[0] || null;
  } catch (error) {
    console.error('Error fetching video details:', error);
    return null;
  }
};

// Fetch channel details
export const fetchChannelDetails = async (channelId) => {
  try {
    const params = {
      part: 'snippet,statistics',
      id: channelId,
    };
    
    const url = buildApiUrl('channels', params);
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }
    
    const data = await response.json();
    return data.items?.[0] || null;
  } catch (error) {
    console.error('Error fetching channel details:', error);
    return null;
  }
};

// Fetch related videos
export const fetchRelatedVideos = async (videoId) => {
  try {
    // Since the related videos endpoint was deprecated, we'll use search with the video's title
    const videoDetails = await fetchVideoDetails(videoId);
    if (!videoDetails) return [];
    
    const searchQuery = videoDetails.snippet.title.split(' ').slice(0, 3).join(' ');
    const relatedVideos = await searchVideos(searchQuery);
    
    // Filter out the current video
    return relatedVideos.filter(video => video.id !== videoId).slice(0, 20);
  } catch (error) {
    console.error('Error fetching related videos:', error);
    return [];
  }
};

// Fetch video comments
export const fetchVideoComments = async (videoId) => {
  try {
    const params = {
      part: 'snippet',
      videoId: videoId,
      maxResults: 50,
      order: 'relevance',
    };
    
    const url = buildApiUrl('commentThreads', params);
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }
    
    const data = await response.json();
    return data.items || [];
  } catch (error) {
    console.error('Error fetching comments:', error);
    return [];
  }
};

// Utility function to format view count
export const formatViewCount = (viewCount) => {
  const num = parseInt(viewCount);
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K';
  }
  return num.toString();
};

// Utility function to format duration
export const formatDuration = (duration) => {
  // Parse ISO 8601 duration format (PT4M13S)
  const match = duration.match(/PT(\d+H)?(\d+M)?(\d+S)?/);
  
  const hours = (match[1] || '').replace('H', '');
  const minutes = (match[2] || '').replace('M', '');
  const seconds = (match[3] || '').replace('S', '');
  
  let formatted = '';
  if (hours) formatted += hours + ':';
  if (minutes || hours) {
    formatted += (minutes || '0').padStart(hours ? 2 : 1, '0') + ':';
  }
  formatted += (seconds || '0').padStart(2, '0');
  
  return formatted;
};

// Category mapping for sidebar
export const VIDEO_CATEGORIES = {
  '0': 'All',
  '1': 'Film & Animation',
  '2': 'Autos & Vehicles', 
  '10': 'Music',
  '15': 'Pets & Animals',
  '17': 'Sports',
  '18': 'Short Movies',
  '19': 'Travel & Events',
  '20': 'Gaming',
  '21': 'Videoblogging',
  '22': 'People & Blogs',
  '23': 'Comedy',
  '24': 'Entertainment',
  '25': 'News & Politics',
  '26': 'Howto & Style',
  '27': 'Education',
  '28': 'Science & Technology',
};