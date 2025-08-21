import React, { useState, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { useClerkUserSync, manualSyncClerkUser } from '../hooks/useClerkUserSync';
import './PaymentDebugPanel.css';

const PaymentDebugPanel = ({ courseId, onClose }) => {
  const { user, isLoaded } = useUser();
  const { syncStatus, syncUserToMongoDB } = useClerkUserSync();
  const [activeTab, setActiveTab] = useState('realFlow');
  const [testData, setTestData] = useState({
    courseId: courseId || '',
    userId: '',
    userEmail: '',
    sessionId: '',
    testEmail: 'test@example.com'
  });
  const [results, setResults] = useState({});
  const [loading, setLoading] = useState({});
  const [availableUsers, setAvailableUsers] = useState([]);
  const [availableCourses, setAvailableCourses] = useState([]);

  const API_BASE_URL = 'http://localhost:5000';

  // Initialize user data from Clerk
  useEffect(() => {
    if (isLoaded && user) {
      setTestData(prev => ({
        ...prev,
        userId: user.id,
        userEmail: user.primaryEmailAddress?.emailAddress || ''
      }));
    }
  }, [isLoaded, user]);

  // Load initial data
  useEffect(() => {
    loadAvailableUsers();
    loadAvailableCourses();
  }, []);

  // API Request Helper
  const apiRequest = async (endpoint, options = {}) => {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    try {
      console.log(`Making request to: ${url}`);
      const response = await fetch(url, config);
      const data = await response.json();
      
      console.log(`Response status: ${response.status}`);
      console.log('Response data:', data);
      
      return { success: response.ok, data, status: response.status };
    } catch (error) {
      console.error(`API request failed: ${endpoint}`, error);
      return { success: false, error: error.message };
    }
  };

  // Show result helper
  const showResult = (section, result, type = 'info') => {
    setResults(prev => ({
      ...prev,
      [section]: { ...result, type }
    }));
  };

  // Set loading state
  const setLoadingState = (section, isLoading) => {
    setLoading(prev => ({
      ...prev,
      [section]: isLoading
    }));
  };

  // Manual sync current user to MongoDB
  const manualSyncCurrentUser = async () => {
    if (!user) {
      showResult('userSync', { error: 'No user logged in' }, 'error');
      return;
    }

    setLoadingState('userSync', true);
    try {
      const syncedUser = await manualSyncClerkUser(user);
      showResult('userSync', {
        message: 'User successfully synced to MongoDB',
        user: syncedUser
      }, 'success');
      
      // Reload users list
      loadAvailableUsers();
    } catch (error) {
      showResult('userSync', {
        error: 'Failed to sync user to MongoDB',
        details: error.message
      }, 'error');
    }
    setLoadingState('userSync', false);
  };

  // Load available users
  const loadAvailableUsers = async () => {
    setLoadingState('users', true);
    try {
      const result = await apiRequest('/api/users');
      if (result.success && result.data) {
        setAvailableUsers(Array.isArray(result.data) ? result.data : []);
        showResult('users', {
          message: `Found ${result.data.length} users`,
          users: result.data
        }, 'success');
      } else {
        setAvailableUsers([]);
        showResult('users', result, 'error');
      }
    } catch (error) {
      setAvailableUsers([]);
      showResult('users', { error: 'Failed to load users', details: error.message }, 'error');
    }
    setLoadingState('users', false);
  };

  // Load available courses
  const loadAvailableCourses = async () => {
    setLoadingState('courses', true);
    try {
      const result = await apiRequest('/api/courses');
      if (result.success && result.data.courses) {
        setAvailableCourses(Array.isArray(result.data.courses) ? result.data.courses : []);
        if (result.data.courses.length > 0 && !testData.courseId) {
          setTestData(prev => ({
            ...prev,
            courseId: result.data.courses[0]._id
          }));
        }
        showResult('courses', {
          message: `Found ${result.data.courses.length} courses`,
          courses: result.data.courses
        }, 'success');
      } else {
        setAvailableCourses([]);
        showResult('courses', result, 'error');
      }
    } catch (error) {
      setAvailableCourses([]);
      showResult('courses', { error: 'Failed to load courses', details: error.message }, 'error');
    }
    setLoadingState('courses', false);
  };

  // Real Payment Flow Functions
  const testRealPaymentFlow = async () => {
    setLoadingState('realFlow', true);
    const { courseId, userEmail } = testData;

    if (!courseId || !userEmail) {
      showResult('realFlow', {
        error: 'Please select Course ID and User Email first',
        suggestion: 'Use the dropdowns to select course and user'
      }, 'error');
      setLoadingState('realFlow', false);
      return;
    }

    showResult('realFlow', {
      message: 'Testing complete payment flow...',
      courseId: courseId,
      userEmail: userEmail
    }, 'info');

    try {
      const result = await apiRequest('/api/payments/simulate-real-payment', {
        method: 'POST',
        body: JSON.stringify({
          courseId: courseId,
          userEmail: userEmail,
          amount: 50000 // $500.00
        })
      });

      showResult('realFlow', result, result.success ? 'success' : 'error');
    } catch (error) {
      showResult('realFlow', { error: 'Failed to test payment flow', details: error.message }, 'error');
    }
    setLoadingState('realFlow', false);
  };

  const simulateStripeWebhook = async () => {
    setLoadingState('webhook', true);
    const { courseId, userEmail } = testData;

    if (!courseId || !userEmail) {
      showResult('webhook', {
        error: 'Please select Course ID and User Email first'
      }, 'error');
      setLoadingState('webhook', false);
      return;
    }

    const webhookData = {
      type: 'payment_intent.succeeded',
      data: {
        object: {
          id: 'pi_simulated_webhook_' + Date.now(),
          amount: 50000,
          currency: 'usd',
          status: 'succeeded',
          metadata: {
            courseId: courseId,
            userEmail: userEmail
          }
        }
      }
    };

    showResult('webhook', {
      message: 'Simulating Stripe webhook...',
      note: 'This simulates what Stripe sends to our webhook endpoint',
      webhookData: webhookData
    }, 'info');

    setTimeout(() => {
      testRealPaymentFlow();
    }, 1000);
    setLoadingState('webhook', false);
  };

  // Complete Stripe Payment Functions
  const createStripeCheckoutSession = async () => {
    setLoadingState('stripeCheckout', true);
    const { courseId, userId, userEmail } = testData;

    if (!courseId || !userId) {
      showResult('stripeCheckout', {
        error: 'Please select Course ID and ensure you are logged in with Clerk'
      }, 'error');
      setLoadingState('stripeCheckout', false);
      return;
    }

    try {
      const sessionData = {
        courseId,
        userId,
        userEmail,
        successUrl: `${window.location.origin}/payment/success?session_id={CHECKOUT_SESSION_ID}&course_id=${courseId}`,
        cancelUrl: `${window.location.origin}/course/${courseId}?payment=cancelled`
      };

      const result = await apiRequest('/api/payments/create-checkout-session', {
        method: 'POST',
        body: JSON.stringify(sessionData)
      });

      if (result.success && result.data.url) {
        setTestData(prev => ({
          ...prev,
          sessionId: result.data.sessionId || ''
        }));

        showResult('stripeCheckout', {
          message: 'Real Stripe checkout session created!',
          sessionId: result.data.sessionId,
          checkoutUrl: result.data.url,
          instructions: {
            step1: 'Click the link below to open Stripe checkout',
            step2: 'Use test card: 4242 4242 4242 4242',
            step3: 'Enter any future expiry date and CVC',
            step4: 'Complete the payment',
            step5: 'Watch server console for real webhook events!'
          },
          note: 'This will trigger REAL webhook events when payment completes!'
        }, 'success');
      } else {
        showResult('stripeCheckout', result, 'error');
      }
    } catch (error) {
      showResult('stripeCheckout', { error: 'Failed to create checkout session', details: error.message }, 'error');
    }
    setLoadingState('stripeCheckout', false);
  };

  const openStripeCheckout = async () => {
    await createStripeCheckoutSession();
    const result = results.stripeCheckout;
    if (result && result.data && result.data.url) {
      window.open(result.data.url, '_blank');
    }
  };

  const verifyPayment = async () => {
    setLoadingState('verify', true);
    const { sessionId, userId } = testData;

    if (!sessionId) {
      showResult('verify', { error: 'Please create a checkout session first' }, 'error');
      setLoadingState('verify', false);
      return;
    }

    try {
      const verificationData = {
        sessionId,
        userId
      };

      const result = await apiRequest('/api/payments/verify-payment', {
        method: 'POST',
        body: JSON.stringify(verificationData)
      });

      if (!result.success && result.error && result.error.includes('payment not completed')) {
        result.explanation = 'This is expected for test sessions. Real payments would have payment_status: "paid"';
      }

      showResult('verify', result, result.success ? 'success' : 'info');
    } catch (error) {
      showResult('verify', { error: 'Failed to verify payment', details: error.message }, 'error');
    }
    setLoadingState('verify', false);
  };

  // Check enrollment status
  const checkEnrollmentStatus = async () => {
    setLoadingState('enrollment', true);
    const { userId } = testData;

    if (!userId) {
      showResult('enrollment', { error: 'Please ensure you are logged in with Clerk' }, 'error');
      setLoadingState('enrollment', false);
      return;
    }

    try {
      const result = await apiRequest(`/api/enrollments/user/${userId}`);
      showResult('enrollment', result, result.success ? 'success' : 'error');
    } catch (error) {
      showResult('enrollment', { error: 'Failed to check enrollment', details: error.message }, 'error');
    }
    setLoadingState('enrollment', false);
  };

  // Test server health
  const testServerHealth = async () => {
    setLoadingState('health', true);
    try {
      const endpoints = [
        '/health',
        '/api/courses',
        '/api/users',
        '/api/enrollments',
        '/api/payments/test-stripe-connection'
      ];

      const results = {};
      
      for (const endpoint of endpoints) {
        try {
          const result = await apiRequest(endpoint);
          results[endpoint] = {
            status: result.status,
            success: result.success,
            message: result.success ? 'OK' : result.error || 'Failed'
          };
        } catch (error) {
          results[endpoint] = {
            status: 'error',
            success: false,
            message: error.message
          };
        }
      }

      const allHealthy = Object.values(results).every(r => r.success);
      showResult('health', results, allHealthy ? 'success' : 'error');
    } catch (error) {
      showResult('health', { error: 'Failed to test server health', details: error.message }, 'error');
    }
    setLoadingState('health', false);
  };

  // Result Display Component
  const ResultDisplay = ({ section, title }) => {
    const result = results[section];
    const isLoading = loading[section];

    if (!result && !isLoading) return null;

    return (
      <div className="result-display">
        <h4>{title}</h4>
        {isLoading && <div className="loading">Loading...</div>}
        {result && (
          <div className={`result ${result.type}`}>
            <strong>{result.type?.toUpperCase()}:</strong>
            <pre>{JSON.stringify(result, null, 2)}</pre>
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="payment-debug-panel">
      {/* Header with close button */}
      <div className="debug-header">
        <h2>🧪 Payment Debug Panel</h2>
        <button className="close-btn" onClick={onClose}>✕</button>
      </div>

      {/* User Info Display */}
      <div className="user-info">
        <h3>👤 Current User (Clerk)</h3>
        {isLoaded && user ? (
          <div className="user-details">
            <p><strong>Name:</strong> {user.fullName || user.firstName + ' ' + user.lastName}</p>
            <p><strong>Email:</strong> {user.primaryEmailAddress?.emailAddress}</p>
            <p><strong>User ID:</strong> {user.id}</p>
          </div>
        ) : (
          <p>Not logged in or loading...</p>
        )}
      </div>

      {/* Test Data Configuration */}
      <div className="test-config">
        <h3>⚙️ Test Configuration</h3>
        <div className="form-grid">
          <div className="form-group">
            <label>Course:</label>
            <select 
              value={testData.courseId} 
              onChange={(e) => setTestData(prev => ({ ...prev, courseId: e.target.value }))}
            >
              <option value="">Select Course</option>
              {availableCourses.map(course => (
                <option key={course._id} value={course._id}>
                  {course.title} - ${course.price}
                </option>
              ))}
            </select>
          </div>
          
          <div className="form-group">
            <label>Test User Email:</label>
            <select 
              value={testData.userEmail} 
              onChange={(e) => setTestData(prev => ({ ...prev, userEmail: e.target.value }))}
            >
              <option value="">Select User</option>
              {availableUsers.map(user => (
                <option key={user.clerkId} value={user.email}>
                  {user.name} - {user.email}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Session ID:</label>
            <input 
              type="text" 
              value={testData.sessionId}
              onChange={(e) => setTestData(prev => ({ ...prev, sessionId: e.target.value }))}
              placeholder="Will be set after creating checkout session"
            />
          </div>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="tab-navigation">
        <button 
          className={activeTab === 'realFlow' ? 'active' : ''}
          onClick={() => setActiveTab('realFlow')}
        >
          🎯 Real Payment Flow Test
        </button>
        <button 
          className={activeTab === 'stripeComplete' ? 'active' : ''}
          onClick={() => setActiveTab('stripeComplete')}
        >
          💳 Complete Stripe Payment
        </button>
        <button 
          className={activeTab === 'userSync' ? 'active' : ''}
          onClick={() => setActiveTab('userSync')}
        >
          👤 User Sync
        </button>
        <button 
          className={activeTab === 'health' ? 'active' : ''}
          onClick={() => setActiveTab('health')}
        >
          🏥 System Health
        </button>
      </div>

      {/* Tab Content */}
      <div className="tab-content">
        {activeTab === 'realFlow' && (
          <div className="real-flow-tab">
            <h3>🎯 Real Payment Flow Test</h3>
            <p>Test the complete payment flow that simulates real Stripe webhook processing</p>
            
            <div className="button-group">
              <button onClick={testRealPaymentFlow} disabled={loading.realFlow}>
                🚀 Test Complete Payment Flow
              </button>
              <button onClick={simulateStripeWebhook} disabled={loading.webhook}>
                🔔 Simulate Stripe Webhook
              </button>
              <button onClick={checkEnrollmentStatus} disabled={loading.enrollment}>
                📝 Check Enrollment Status
              </button>
            </div>

            <div className="info-box">
              <h4>ℹ️ What this does:</h4>
              <ul>
                <li><strong>Simulated Flow:</strong> Tests webhook processing without actual Stripe payment</li>
                <li><strong>Real Database:</strong> Creates actual enrollment records in MongoDB</li>
                <li><strong>Clerk Integration:</strong> Uses your actual Clerk user data</li>
                <li><strong>Instant Results:</strong> No need to complete actual payment process</li>
              </ul>
            </div>

            <ResultDisplay section="realFlow" title="Payment Flow Results" />
            <ResultDisplay section="webhook" title="Webhook Simulation Results" />
            <ResultDisplay section="enrollment" title="Enrollment Status" />
          </div>
        )}

        {activeTab === 'stripeComplete' && (
          <div className="stripe-complete-tab">
            <h3>💳 Complete Stripe Payment</h3>
            <p>Create real Stripe checkout sessions and test with actual Stripe test cards</p>
            
            <div className="button-group">
              <button onClick={createStripeCheckoutSession} disabled={loading.stripeCheckout}>
                🛒 Create Checkout Session
              </button>
              <button onClick={openStripeCheckout} disabled={loading.stripeCheckout}>
                🔗 Open Stripe Checkout
              </button>
              <button onClick={verifyPayment} disabled={loading.verify}>
                ✅ Verify Payment
              </button>
            </div>

            <div className="info-box">
              <h4>💳 Stripe Test Cards:</h4>
              <ul>
                <li><code>4242 4242 4242 4242</code> - Visa (Success)</li>
                <li><code>4000 0000 0000 0002</code> - Card Declined</li>
                <li><code>4000 0000 0000 9995</code> - Insufficient Funds</li>
                <li><code>4000 0025 0000 3155</code> - 3D Secure</li>
              </ul>
              <p><small>Use any future date for expiry, any 3 digits for CVC, any 5 digits for ZIP</small></p>
            </div>

            <div className="info-box">
              <h4>🔄 Real Stripe Flow:</h4>
              <ol>
                <li>Create checkout session with real course data</li>
                <li>Open Stripe hosted checkout page</li>
                <li>Complete payment with test card</li>
                <li>Stripe sends real webhook to server</li>
                <li>Server creates enrollment automatically</li>
                <li>Verify payment completion</li>
              </ol>
            </div>

            <ResultDisplay section="stripeCheckout" title="Stripe Checkout Results" />
            <ResultDisplay section="verify" title="Payment Verification" />
          </div>
        )}

        {activeTab === 'userSync' && (
          <div className="user-sync-tab">
            <h3>👤 User Sync Management</h3>
            <p>Manage Clerk user synchronization with MongoDB</p>
            
            <div className="info-box">
              <h4>🔄 Auto-Sync Status:</h4>
              <p>Auto-sync is enabled via ClerkSyncWrapper. When users register with Clerk, they are automatically added to MongoDB.</p>
              {user && (
                <p>Current user: <strong>{user.primaryEmailAddress?.emailAddress}</strong></p>
              )}
            </div>

            <div className="button-group">
              <button onClick={manualSyncCurrentUser} disabled={loading.userSync || !user}>
                🔄 Manual Sync Current User
              </button>
              <button onClick={loadAvailableUsers} disabled={loading.users}>
                👥 Refresh Users List
              </button>
            </div>

            <div className="info-box">
              <h4>📋 Available Users in MongoDB:</h4>
              {availableUsers.length > 0 ? (
                <ul>
                  {availableUsers.map(user => (
                    <li key={user.clerkId}>
                      <strong>{user.name}</strong> - {user.email} 
                      <small> (Clerk ID: {user.clerkId})</small>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>No users found in MongoDB. Try syncing users from Clerk.</p>
              )}
            </div>

            <ResultDisplay section="userSync" title="User Sync Results" />
            <ResultDisplay section="users" title="Users List" />
          </div>
        )}

        {activeTab === 'health' && (
          <div className="health-tab">
            <h3>🏥 System Health Check</h3>
            <p>Test server connectivity, database connections, and Stripe integration</p>
            
            <div className="button-group">
              <button onClick={testServerHealth} disabled={loading.health}>
                🔍 Run Health Check
              </button>
              <button onClick={loadAvailableUsers} disabled={loading.users}>
                👥 Reload Users
              </button>
              <button onClick={loadAvailableCourses} disabled={loading.courses}>
                📚 Reload Courses
              </button>
            </div>

            <ResultDisplay section="health" title="Server Health Results" />
            <ResultDisplay section="users" title="Available Users" />
            <ResultDisplay section="courses" title="Available Courses" />
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentDebugPanel;

// Add CSS styling
const styles = `
  .payment-debug-panel {
    position: fixed;
    top: 0;
    right: 0;
    width: 400px;
    height: 100vh;
    background: white;
    border-left: 2px solid #e0e0e0;
    box-shadow: -2px 0 10px rgba(0,0,0,0.1);
    overflow-y: auto;
    z-index: 9999;
    font-family: Arial, sans-serif;
    font-size: 14px;
  }

  .debug-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: #4f46e5;
    color: white;
    position: sticky;
    top: 0;
    z-index: 1000;
  }

  .debug-header h2 {
    margin: 0;
    font-size: 18px;
  }

  .close-btn {
    background: rgba(255,255,255,0.2);
    border: none;
    color: white;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    transition: background 0.2s;
  }

  .close-btn:hover {
    background: rgba(255,255,255,0.3);
  }

  .user-info, .test-config {
    padding: 16px;
    border-bottom: 1px solid #e0e0e0;
  }

  .user-info h3, .test-config h3 {
    margin: 0 0 12px 0;
    font-size: 16px;
    color: #374151;
  }

  .user-details p {
    margin: 4px 0;
    color: #6b7280;
  }

  .form-grid {
    display: grid;
    gap: 12px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
  }

  .form-group label {
    margin-bottom: 4px;
    font-weight: 500;
    color: #374151;
  }

  .form-group select, .form-group input {
    padding: 8px;
    border: 1px solid #d1d5db;
    border-radius: 4px;
    font-size: 14px;
  }

  .tab-navigation {
    display: flex;
    border-bottom: 1px solid #e0e0e0;
    background: #f9fafb;
  }

  .tab-navigation button {
    flex: 1;
    padding: 12px 8px;
    border: none;
    background: transparent;
    cursor: pointer;
    border-bottom: 3px solid transparent;
    font-size: 12px;
    transition: all 0.2s;
  }

  .tab-navigation button.active {
    background: white;
    border-bottom-color: #4f46e5;
    color: #4f46e5;
    font-weight: 500;
  }

  .tab-navigation button:hover {
    background: rgba(79, 70, 229, 0.05);
  }

  .tab-content {
    padding: 16px;
  }

  .tab-content h3 {
    margin: 0 0 12px 0;
    font-size: 16px;
    color: #374151;
  }

  .button-group {
    display: grid;
    gap: 8px;
    margin: 16px 0;
  }

  .button-group button {
    padding: 10px 12px;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    background: white;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.2s;
  }

  .button-group button:hover {
    background: #f3f4f6;
    border-color: #9ca3af;
  }

  .button-group button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .info-box {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    padding: 12px;
    margin: 12px 0;
  }

  .info-box h4 {
    margin: 0 0 8px 0;
    font-size: 14px;
    color: #1e293b;
  }

  .info-box p, .info-box li {
    margin: 4px 0;
    font-size: 13px;
    color: #475569;
  }

  .info-box ul {
    margin: 8px 0;
    padding-left: 20px;
  }

  .info-box code {
    background: #e2e8f0;
    padding: 2px 4px;
    border-radius: 3px;
    font-family: monospace;
    font-size: 12px;
  }

  .result-display {
    margin: 16px 0;
    border-radius: 6px;
    overflow: hidden;
  }

  .result-display h4 {
    margin: 0;
    padding: 8px 12px;
    background: #f1f5f9;
    border-bottom: 1px solid #e2e8f0;
    font-size: 14px;
    color: #334155;
  }

  .loading {
    padding: 12px;
    text-align: center;
    color: #6b7280;
    font-style: italic;
  }

  .result {
    padding: 12px;
    font-family: monospace;
    font-size: 12px;
    max-height: 200px;
    overflow-y: auto;
  }

  .result.success {
    background: #f0fdf4;
    border-left: 4px solid #22c55e;
    color: #166534;
  }

  .result.error {
    background: #fef2f2;
    border-left: 4px solid #ef4444;
    color: #991b1b;
  }

  .result pre {
    margin: 8px 0 0 0;
    white-space: pre-wrap;
    word-break: break-word;
  }
`;

// Inject styles
if (typeof document !== 'undefined' && !document.getElementById('payment-debug-styles')) {
  const styleSheet = document.createElement('style');
  styleSheet.id = 'payment-debug-styles';
  styleSheet.textContent = styles;
  document.head.appendChild(styleSheet);
}
