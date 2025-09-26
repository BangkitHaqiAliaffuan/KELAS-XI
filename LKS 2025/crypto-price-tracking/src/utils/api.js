import axios from 'axios';

// Base URL untuk CoinGecko API
const BASE_URL = 'https://api.coingecko.com/api/v3';
const API_KEY = import.meta.env.VITE_COINGECKO_API_KEY;

// Create axios instance dengan konfigurasi default
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000, // 10 detik timeout
  headers: {
    'Content-Type': 'application/json',
    ...(API_KEY && { 'x-cg-demo-api-key': API_KEY })
  },
});

// Request interceptor untuk logging
api.interceptors.request.use(
  (config) => {
    console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('❌ Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor untuk error handling
api.interceptors.response.use(
  (response) => {
    console.log(`✅ API Response: ${response.status} ${response.config.url}`);
    return response;
  },
  (error) => {
    console.error('❌ Response Error:', error.response?.status, error.message);
    
    // Handle different error types
    if (error.code === 'ECONNABORTED') {
      error.message = 'Request timeout. Please try again.';
    } else if (error.response?.status === 429) {
      error.message = 'Rate limit exceeded. Please wait a moment.';
    } else if (error.response?.status >= 500) {
      error.message = 'Server error. Please try again later.';
    } else if (!error.response) {
      error.message = 'Network error. Please check your connection.';
    }
    
    return Promise.reject(error);
  }
);

/**
 * Fetch daftar cryptocurrency dengan market data
 * @param {string} currency - Currency code (usd, idr, eur, etc.)
 * @param {number} perPage - Number of coins per page (default: 100)
 * @param {number} page - Page number (default: 1)
 * @returns {Promise<Array>} Array of coin objects
 */
export const fetchCoinsMarket = async (currency = 'usd', perPage = 100, page = 1) => {
  try {
    const response = await api.get('/coins/markets', {
      params: {
        vs_currency: currency,
        order: 'market_cap_desc',
        per_page: perPage,
        page: page,
        sparkline: false,
        price_change_percentage: '24h',
      },
    });

    return response.data;
  } catch (error) {
    console.error('Error fetching coins market data:', error);
    throw new Error(error.message || 'Failed to fetch cryptocurrency data');
  }
};

/**
 * Fetch detail cryptocurrency berdasarkan ID
 * @param {string} coinId - ID coin (bitcoin, ethereum, etc.)
 * @returns {Promise<Object>} Coin detail object
 */
export const fetchCoinDetail = async (coinId) => {
  try {
    const response = await api.get(`/coins/${coinId}`, {
      params: {
        localization: false,
        tickers: false,
        market_data: true,
        community_data: false,
        developer_data: false,
        sparkline: false,
      },
    });

    return response.data;
  } catch (error) {
    console.error(`Error fetching coin detail for ${coinId}:`, error);
    throw new Error(error.message || 'Failed to fetch coin details');
  }
};

/**
 * Fetch historical price data untuk chart
 * @param {string} coinId - ID coin
 * @param {string} currency - Currency code
 * @param {number} days - Number of days (1, 7, 30, 90, 365)
 * @returns {Promise<Array>} Array of [timestamp, price] pairs
 */
export const fetchCoinHistory = async (coinId, currency = 'usd', days = 7) => {
  try {
    const response = await api.get(`/coins/${coinId}/market_chart`, {
      params: {
        vs_currency: currency,
        days: days,
        interval: days <= 1 ? 'hourly' : 'daily',
      },
    });

    return response.data.prices;
  } catch (error) {
    console.error(`Error fetching coin history for ${coinId}:`, error);
    throw new Error(error.message || 'Failed to fetch price history');
  }
};

/**
 * Search cryptocurrency berdasarkan query
 * @param {string} query - Search term
 * @returns {Promise<Array>} Array of search results
 */
export const searchCoins = async (query) => {
  try {
    if (!query || query.trim().length < 2) {
      return [];
    }

    const response = await api.get('/search', {
      params: {
        query: query.trim(),
      },
    });

    // Return only coins, not exchanges or categories
    return response.data.coins || [];
  } catch (error) {
    console.error(`Error searching for "${query}":`, error);
    throw new Error(error.message || 'Failed to search cryptocurrencies');
  }
};

/**
 * Fetch trending coins
 * @returns {Promise<Array>} Array of trending coins
 */
export const fetchTrendingCoins = async () => {
  try {
    const response = await api.get('/search/trending');
    return response.data.coins.map(item => item.item);
  } catch (error) {
    console.error('Error fetching trending coins:', error);
    throw new Error(error.message || 'Failed to fetch trending coins');
  }
};

/**
 * Fetch global cryptocurrency data
 * @returns {Promise<Object>} Global market data
 */
export const fetchGlobalData = async () => {
  try {
    const response = await api.get('/global');
    return response.data.data;
  } catch (error) {
    console.error('Error fetching global data:', error);
    throw new Error(error.message || 'Failed to fetch global market data');
  }
};

/**
 * Format number dengan separator ribuan
 * @param {number} num - Number to format
 * @returns {string} Formatted number string
 */
export const formatNumber = (num) => {
  if (num === null || num === undefined) return 'N/A';
  
  if (num >= 1e12) {
    return `${(num / 1e12).toFixed(2)}T`;
  } else if (num >= 1e9) {
    return `${(num / 1e9).toFixed(2)}B`;
  } else if (num >= 1e6) {
    return `${(num / 1e6).toFixed(2)}M`;
  } else if (num >= 1e3) {
    return `${(num / 1e3).toFixed(2)}K`;
  } else {
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2,
    }).format(num);
  }
};

/**
 * Format price dengan currency symbol
 * @param {number} price - Price to format
 * @param {string} currency - Currency code
 * @param {string} symbol - Currency symbol
 * @returns {string} Formatted price string
 */
export const formatPrice = (price, currency = 'usd', symbol = '$') => {
  if (price === null || price === undefined) return 'N/A';
  
  const options = {
    minimumFractionDigits: 2,
    maximumFractionDigits: price < 1 ? 6 : 2,
  };
  
  const formattedPrice = new Intl.NumberFormat('en-US', options).format(price);
  
  // For IDR, put symbol after the number
  if (currency === 'idr') {
    return `${formattedPrice} ${symbol.trim()}`;
  }
  
  return `${symbol}${formattedPrice}`;
};

/**
 * Format percentage dengan warna
 * @param {number} percentage - Percentage value
 * @returns {Object} Object with formatted percentage and color class
 */
export const formatPercentage = (percentage) => {
  if (percentage === null || percentage === undefined) {
    return { value: 'N/A', colorClass: '' };
  }
  
  const formatted = `${percentage >= 0 ? '+' : ''}${percentage.toFixed(2)}%`;
  const colorClass = percentage >= 0 ? 'text-success' : 'text-danger';
  
  return { value: formatted, colorClass };
};

/**
 * Debounce function untuk search
 * @param {Function} func - Function to debounce
 * @param {number} delay - Delay in milliseconds
 * @returns {Function} Debounced function
 */
export const debounce = (func, delay) => {
  let timeoutId;
  return (...args) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func.apply(null, args), delay);
  };
};

export default api;