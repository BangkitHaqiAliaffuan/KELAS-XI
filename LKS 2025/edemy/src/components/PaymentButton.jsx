import React, { useState } from 'react';
import { useUser } from '@clerk/clerk-react';
import { CreditCard, Loader2, ShoppingCart, Bug } from 'lucide-react';
import { paymentAPI } from '../services/api.js';
import PaymentDebugPanel from './PaymentDebugPanel';

const PaymentButton = ({ course, onSuccess, onError, className = "", showDebugMode = false }) => {
  const { user, isSignedIn } = useUser();
  const [isLoading, setIsLoading] = useState(false);
  const [showDebugPanel, setShowDebugPanel] = useState(false);

  // Calculate discounted price
  const calculatePrice = () => {
    if (!course) return 0;
    
    let price = course.coursePrice || 0;
    if (course.courseDiscount > 0) {
      price = price * (1 - course.courseDiscount / 100);
    }
    return price;
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const handlePayment = async () => {
    if (!isSignedIn) {
      onError?.('Please sign in to enroll in this course');
      return;
    }

    if (!course) {
      onError?.('Course information not available');
      return;
    }

    try {
      setIsLoading(true);

      // Create checkout session
      const response = await paymentAPI.createCheckoutSession({
        courseId: course._id,
        userId: user.id,
        successUrl: `${window.location.origin}/payment/success?session_id={CHECKOUT_SESSION_ID}&course_id=${course._id}`,
        cancelUrl: `${window.location.origin}/course/${course._id}?payment=cancelled`
      });

      if (response.success && response.data.url) {
        // Redirect to Stripe checkout
        window.location.href = response.data.url;
      } else {
        throw new Error(response.message || 'Failed to create checkout session');
      }

    } catch (error) {
      console.error('Payment error:', error);
      onError?.(error.message || 'Payment failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const finalPrice = calculatePrice();
  const originalPrice = course?.coursePrice || 0;
  const hasDiscount = course?.courseDiscount > 0;

  return (
    <div className={`space-y-4 ${className}`}>
      {/* Price Display */}
      <div className="text-center">
        <div className="flex items-center justify-center space-x-2 mb-2">
          {hasDiscount ? (
            <>
              <span className="text-3xl font-bold text-gray-900">
                {formatPrice(finalPrice)}
              </span>
              <span className="text-lg text-gray-500 line-through">
                {formatPrice(originalPrice)}
              </span>
              <span className="bg-red-100 text-red-800 text-sm px-2 py-1 rounded">
                {course.courseDiscount}% OFF
              </span>
            </>
          ) : (
            <span className="text-3xl font-bold text-gray-900">
              {formatPrice(finalPrice)}
            </span>
          )}
        </div>
        {hasDiscount && (
          <p className="text-sm text-red-600">
            Save {formatPrice(originalPrice - finalPrice)}
          </p>
        )}
      </div>

      {/* Payment Button */}
      <button
        onClick={handlePayment}
        disabled={isLoading || !isSignedIn}
        className={`w-full bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold 
          hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed 
          transition-colors flex items-center justify-center space-x-2`}
      >
        {isLoading ? (
          <>
            <Loader2 className="h-5 w-5 animate-spin" />
            <span>Processing...</span>
          </>
        ) : (
          <>
            <CreditCard className="h-5 w-5" />
            <span>
              {finalPrice === 0 ? 'Enroll for Free' : 'Enroll Now'}
            </span>
          </>
        )}
      </button>

      {/* Debug Mode Button */}
      {showDebugMode && (
        <button
          onClick={() => setShowDebugPanel(true)}
          className="w-full mt-3 flex items-center justify-center space-x-2 px-6 py-3 border border-gray-300 rounded-lg text-gray-700 bg-gray-50 hover:bg-gray-100 transition-colors duration-200"
        >
          <Bug className="h-5 w-5" />
          <span>🧪 Debug Payment System</span>
        </button>
      )}

      {!isSignedIn && (
        <p className="text-sm text-gray-600 text-center">
          Please sign in to enroll in this course
        </p>
      )}

      {/* Security Notice */}
      <div className="text-xs text-gray-500 text-center">
        <div className="flex items-center justify-center space-x-1 mb-1">
          <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
          </svg>
          <span>Secure payment powered by Stripe</span>
        </div>
        <p>30-day money-back guarantee</p>
      </div>

      {/* Debug Panel */}
      {showDebugPanel && (
        <PaymentDebugPanel 
          courseId={course?._id} 
          onClose={() => setShowDebugPanel(false)} 
        />
      )}
    </div>
  );
};

export default PaymentButton;
