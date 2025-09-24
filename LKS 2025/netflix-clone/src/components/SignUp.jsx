import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signUp } from '../utils/firebase';
import { assets } from '../assets/assets';
import './Auth.css';

const SignUp = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      setLoading(false);
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters long.');
      setLoading(false);
      return;
    }

    try {
      await signUp(email, password);
      navigate('/');
    } catch (error) {
      setError(getErrorMessage(error.code));
    } finally {
      setLoading(false);
    }
  };

  const getErrorMessage = (errorCode) => {
    switch (errorCode) {
      case 'auth/email-already-in-use':
        return 'An account with this email already exists.';
      case 'auth/invalid-email':
        return 'Invalid email address.';
      case 'auth/weak-password':
        return 'Password is too weak. Please choose a stronger password.';
      default:
        return 'An error occurred. Please try again.';
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-background">
        <img src={assets.backgroundBanner} alt="Netflix Background" />
        <div className="auth-overlay"></div>
      </div>
      
      <div className="auth-header">
        <img src={assets.logo} alt="Netflix" className="auth-logo" />
      </div>

      <div className="auth-form-container">
        <form onSubmit={handleSubmit} className="auth-form">
          <h1>Sign Up</h1>
          
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
              minLength="6"
            />
          </div>
          
          <div className="form-group">
            <input
              type="password"
              placeholder="Confirm Password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="input"
              required
              minLength="6"
            />
          </div>
          
          <button type="submit" className="btn btn-primary auth-button" disabled={loading}>
            {loading ? <span className="loading-spinner"></span> : 'Sign Up'}
          </button>
          
          <div className="auth-switch">
            <span>Already have an account? </span>
            <Link to="/login">Sign in now</Link>
          </div>
          
          <div className="auth-info">
            By signing up, you agree to our Terms of Service and Privacy Policy.
          </div>
        </form>
      </div>
    </div>
  );
};

export default SignUp;