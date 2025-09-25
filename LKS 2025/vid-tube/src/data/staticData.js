// Static data for the YouTube clone
import assets from '../assets/assets';

// Sidebar categories with icons
export const sidebarCategories = [
  { id: '0', name: 'All', icon: assets.home },
  { id: '20', name: 'Gaming', icon: assets.game_icon },
  { id: '2', name: 'Automobiles', icon: assets.automobiles },
  { id: '17', name: 'Sports', icon: assets.sports },
  { id: '24', name: 'Entertainment', icon: assets.entertainment },
  { id: '28', name: 'Technology', icon: assets.tech },
  { id: '10', name: 'Music', icon: assets.music },
  { id: '22', name: 'Blogs', icon: assets.blogs },
  { id: '25', name: 'News', icon: assets.news },
];

// Sample channel data (for fallback when API doesn't return channel info)
export const sampleChannels = [
  {
    id: 'UC_sample1',
    name: 'GreatStack',
    avatar: assets.jack,
    subscribers: '2.1M'
  },
  {
    id: 'UC_sample2',
    name: 'TechChannel',
    avatar: assets.simon,
    subscribers: '890K'
  },
  {
    id: 'UC_sample3',
    name: 'GameReviews',
    avatar: assets.tom,
    subscribers: '1.5M'
  },
  {
    id: 'UC_sample4',
    name: 'MusicWorld',
    avatar: assets.megan,
    subscribers: '3.2M'
  },
  {
    id: 'UC_sample5',
    name: 'SportsCentral',
    avatar: assets.gerard,
    subscribers: '750K'
  },
  {
    id: 'UC_sample6',
    name: 'NewsToday',
    avatar: assets.cameron,
    subscribers: '1.8M'
  },
];

// Sample video data (for fallback or demo purposes)
export const sampleVideos = [
  {
    id: 'dQw4w9WgXcQ',
    snippet: {
      title: 'Rick Astley - Never Gonna Give You Up (Official Video)',
      channelTitle: 'Rick Astley',
      publishedAt: '2009-10-25T06:57:33Z',
      thumbnails: {
        medium: { url: assets.thumbnail1 }
      },
      channelId: 'UCuAXFkgsw1L7xaCfnd5JJOw'
    },
    statistics: {
      viewCount: '1234567890',
      likeCount: '12345678'
    }
  },
  {
    id: 'jNQXAC9IVRw',
    snippet: {
      title: 'Me at the zoo',
      channelTitle: 'jawed',
      publishedAt: '2005-04-23T23:11:20Z',
      thumbnails: {
        medium: { url: assets.thumbnail2 }
      },
      channelId: 'UC4QobU6STFB0P71PMvOGN5A'
    },
    statistics: {
      viewCount: '123456789',
      likeCount: '1234567'
    }
  }
];

// Navigation items for the sidebar
export const sidebarNavItems = [
  { name: 'Home', icon: assets.home, active: true },
  { name: 'Explore', icon: assets.explore },
  { name: 'Subscriptions', icon: assets.subscriprion },
  { name: 'Library', icon: assets.library },
  { name: 'History', icon: assets.history },
  { name: 'Your videos', icon: assets.video },
  { name: 'Watch later', icon: assets.save },
  { name: 'Liked videos', icon: assets.like },
  { name: 'Show more', icon: assets.show_more },
];