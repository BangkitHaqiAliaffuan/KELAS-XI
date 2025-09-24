import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signIn } from '../utils/firebase';
import { assets } from '../assets/assets';
import './Auth.css';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await signIn(email, password);
      navigate('/');
    } catch (error) {
      setError(getErrorMessage(error.code));
    } finally {
      setLoading(false);
    }
  };

  const getErrorMessage = (errorCode) => {
    switch (errorCode) {
      case 'auth/user-not-found':
        return 'No account found with this email address.';
      case 'auth/wrong-password':
        return 'Incorrect password.';
      case 'auth/invalid-email':
        return 'Invalid email address.';
      case 'auth/too-many-requests':
        return 'Too many failed attempts. Please try again later.';
      default:
        return 'An error occurred. Please try again.';
    }
  };

  return (
    <div style={{backgroundImage:`url(${assets.backgroundBanner})`}} className="auth-container">
      <div className="auth-header">
        <img src={assets.logo} alt="Netflix" className="auth-logo" />
      </div>

      <div className="auth-form-container">
        <form onSubmit={handleSubmit} className="auth-form">
          <h1>Sign In</h1>
          
          {error && <div className="error-message">{error}</div>}
          
          <div className="form-group">
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input"
              required
            />
          </div>
          
          <div className="form-group">
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input"
              required
            />
          </div>
          
          <button type="submit" className="btn btn-primary auth-button" disabled={loading}>
            {loading ? <span className="loading-spinner"></span> : 'Sign In'}
          </button>
          
          <div className="auth-help">
            <div className="remember-me">
              <input type="checkbox" id="remember" />
              <label htmlFor="remember">Remember me</label>
            </div>
            <a href="#" className="need-help">Need help?</a>
          </div>
          
          <div className="auth-switch">
            <span>New to Netflix? </span>
            <Link to="/signup">Sign up now</Link>
          </div>
          
          <div className="auth-info">
            This page is protected by Google reCAPTCHA to ensure you're not a bot.
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;