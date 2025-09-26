import { createContext, useContext, useReducer, useEffect } from 'react';

// Initial state
const initialState = {
  currency: 'usd',
  currencySymbol: '$',
  searchTerm: '',
  coins: [],
  loading: false,
  error: null,
  watchlist: [],
};

// Action types
const actionTypes = {
  SET_CURRENCY: 'SET_CURRENCY',
  SET_SEARCH_TERM: 'SET_SEARCH_TERM',
  SET_COINS: 'SET_COINS',
  SET_LOADING: 'SET_LOADING',
  SET_ERROR: 'SET_ERROR',
  ADD_TO_WATCHLIST: 'ADD_TO_WATCHLIST',
  REMOVE_FROM_WATCHLIST: 'REMOVE_FROM_WATCHLIST',
};

// Reducer function
const cryptoReducer = (state, action) => {
  switch (action.type) {
    case actionTypes.SET_CURRENCY:
      return {
        ...state,
        currency: action.payload.currency,
        currencySymbol: action.payload.symbol,
      };
    case actionTypes.SET_SEARCH_TERM:
      return {
        ...state,
        searchTerm: action.payload,
      };
    case actionTypes.SET_COINS:
      return {
        ...state,
        coins: action.payload,
        loading: false,
        error: null,
      };
    case actionTypes.SET_LOADING:
      return {
        ...state,
        loading: action.payload,
      };
    case actionTypes.SET_ERROR:
      return {
        ...state,
        error: action.payload,
        loading: false,
      };
    case actionTypes.ADD_TO_WATCHLIST:
      return {
        ...state,
        watchlist: [...state.watchlist, action.payload],
      };
    case actionTypes.REMOVE_FROM_WATCHLIST:
      return {
        ...state,
        watchlist: state.watchlist.filter(coin => coin !== action.payload),
      };
    default:
      return state;
  }
};

// Currency options with symbols
const currencyOptions = {
  usd: { symbol: '$', name: 'USD' },
  idr: { symbol: 'Rp ', name: 'IDR' },
  eur: { symbol: '€', name: 'EUR' },
  jpy: { symbol: '¥', name: 'JPY' },
  gbp: { symbol: '£', name: 'GBP' },
};

// Create Context
const CryptoContext = createContext();

// Custom hook to use crypto context
export const useCrypto = () => {
  const context = useContext(CryptoContext);
  if (!context) {
    throw new Error('useCrypto must be used within a CryptoProvider');
  }
  return context;
};

// Provider component
export const CryptoProvider = ({ children }) => {
  const [state, dispatch] = useReducer(cryptoReducer, initialState);

  // Load saved preferences from localStorage
  useEffect(() => {
    const savedCurrency = localStorage.getItem('cryptoTracker_currency');
    const savedWatchlist = localStorage.getItem('cryptoTracker_watchlist');

    if (savedCurrency && currencyOptions[savedCurrency]) {
      dispatch({
        type: actionTypes.SET_CURRENCY,
        payload: {
          currency: savedCurrency,
          symbol: currencyOptions[savedCurrency].symbol,
        },
      });
    }

    if (savedWatchlist) {
      try {
        const watchlist = JSON.parse(savedWatchlist);
        watchlist.forEach(coinId => {
          dispatch({
            type: actionTypes.ADD_TO_WATCHLIST,
            payload: coinId,
          });
        });
      } catch (error) {
        console.error('Error loading watchlist from localStorage:', error);
      }
    }
  }, []);

  // Action creators
  const setCurrency = (currency) => {
    if (currencyOptions[currency]) {
      dispatch({
        type: actionTypes.SET_CURRENCY,
        payload: {
          currency,
          symbol: currencyOptions[currency].symbol,
        },
      });
      localStorage.setItem('cryptoTracker_currency', currency);
    }
  };

  const setSearchTerm = (term) => {
    dispatch({
      type: actionTypes.SET_SEARCH_TERM,
      payload: term.toLowerCase(),
    });
  };

  const setCoins = (coins) => {
    dispatch({
      type: actionTypes.SET_COINS,
      payload: coins,
    });
  };

  const setLoading = (loading) => {
    dispatch({
      type: actionTypes.SET_LOADING,
      payload: loading,
    });
  };

  const setError = (error) => {
    dispatch({
      type: actionTypes.SET_ERROR,
      payload: error,
    });
  };

  const addToWatchlist = (coinId) => {
    if (!state.watchlist.includes(coinId)) {
      dispatch({
        type: actionTypes.ADD_TO_WATCHLIST,
        payload: coinId,
      });
      const updatedWatchlist = [...state.watchlist, coinId];
      localStorage.setItem('cryptoTracker_watchlist', JSON.stringify(updatedWatchlist));
    }
  };

  const removeFromWatchlist = (coinId) => {
    dispatch({
      type: actionTypes.REMOVE_FROM_WATCHLIST,
      payload: coinId,
    });
    const updatedWatchlist = state.watchlist.filter(coin => coin !== coinId);
    localStorage.setItem('cryptoTracker_watchlist', JSON.stringify(updatedWatchlist));
  };

  // Filtered coins based on search term
  const filteredCoins = state.coins.filter(coin =>
    coin.name.toLowerCase().includes(state.searchTerm) ||
    coin.symbol.toLowerCase().includes(state.searchTerm)
  );

  // Context value
  const value = {
    ...state,
    currencyOptions,
    filteredCoins,
    setCurrency,
    setSearchTerm,
    setCoins,
    setLoading,
    setError,
    addToWatchlist,
    removeFromWatchlist,
  };

  return (
    <CryptoContext.Provider value={value}>
      {children}
    </CryptoContext.Provider>
  );
};

export default CryptoContext;