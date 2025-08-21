import React, { useState, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { CheckCircle, ArrowRight, Home, BookOpen, AlertCircle } from 'lucide-react';
import { paymentAPI } from '../services/api.js';

const PaymentSuccess = () => {
  const { user, isSignedIn } = useUser();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [verificationStatus, setVerificationStatus] = useState('verifying'); // verifying, success, error
  const [enrollmentData, setEnrollmentData] = useState(null);
  const [error, setError] = useState('');

  const sessionId = searchParams.get('session_id');
  const courseId = searchParams.get('course_id');

  useEffect(() => {
    if (!isSignedIn) {
      navigate('/');
      return;
    }

    if (!sessionId) {
      setVerificationStatus('error');
      setError('No payment session found');
      return;
    }

    verifyPayment();
  }, [isSignedIn, sessionId]);

  const verifyPayment = async () => {
    try {
      setVerificationStatus('verifying');

      const response = await paymentAPI.verifyPayment({
        sessionId,
        userId: user.id
      });

      if (response.success) {
        setVerificationStatus('success');
        setEnrollmentData(response.data);
      } else {
        setVerificationStatus('error');
        setError(response.message || 'Payment verification failed');
      }
    } catch (err) {
      console.error('Payment verification error:', err);
      setVerificationStatus('error');
      setError('Unable to verify payment. Please contact support.');
    }
  };

  const handleGoToCourse = () => {
    if (enrollmentData?.courseId) {
      navigate(`/course/${enrollmentData.courseId}`);
    }
  };

  const handleGoHome = () => {
    navigate('/');
  };

  if (verificationStatus === 'verifying') {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white p-8 rounded-lg shadow-lg text-center max-w-md w-full mx-4">
          <div className="animate-spin h-12 w-12 border-4 border-blue-600 border-t-transparent rounded-full mx-auto mb-4"></div>
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Verifying Payment
          </h2>
          <p className="text-gray-600">
            Please wait while we confirm your enrollment...
          </p>
        </div>
      </div>
    );
  }

  if (verificationStatus === 'error') {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white p-8 rounded-lg shadow-lg text-center max-w-md w-full mx-4">
          <AlertCircle className="h-16 w-16 text-red-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Payment Verification Failed
          </h2>
          <p className="text-gray-600 mb-6">
            {error}
          </p>
          <div className="space-y-3">
            <button
              onClick={verifyPayment}
              className="w-full bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors"
            >
              Try Again
            </button>
            <button
              onClick={handleGoHome}
              className="w-full bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors flex items-center justify-center space-x-2"
            >
              <Home className="h-5 w-5" />
              <span>Go Home</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-white p-8 rounded-lg shadow-lg text-center max-w-md w-full mx-4">
        <CheckCircle className="h-16 w-16 text-green-600 mx-auto mb-4" />
        
        <h2 className="text-2xl font-bold text-gray-900 mb-2">
          Payment Successful!
        </h2>
        
        <p className="text-gray-600 mb-6">
          Congratulations! You have successfully enrolled in the course.
        </p>

        {enrollmentData && (
          <div className="bg-gray-50 p-4 rounded-lg mb-6 text-left">
            <h3 className="font-semibold text-gray-900 mb-2">Enrollment Details:</h3>
            <div className="space-y-1 text-sm text-gray-600">
              <p><span className="font-medium">Course:</span> {enrollmentData.courseName}</p>
              <p><span className="font-medium">Amount Paid:</span> ${enrollmentData.amountPaid}</p>
              <p><span className="font-medium">Enrolled on:</span> {new Date(enrollmentData.enrolledDate).toLocaleDateString()}</p>
              <p><span className="font-medium">Access:</span> Lifetime access</p>
            </div>
          </div>
        )}

        <div className="space-y-3">
          <button
            onClick={handleGoToCourse}
            className="w-full bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors flex items-center justify-center space-x-2"
          >
            <BookOpen className="h-5 w-5" />
            <span>Start Learning</span>
            <ArrowRight className="h-5 w-5" />
          </button>
          
          <button
            onClick={handleGoHome}
            className="w-full bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors flex items-center justify-center space-x-2"
          >
            <Home className="h-5 w-5" />
            <span>Go Home</span>
          </button>
        </div>

        <div className="mt-6 pt-6 border-t border-gray-200">
          <p className="text-xs text-gray-500">
            A confirmation email has been sent to your registered email address.
          </p>
        </div>
      </div>
    </div>
  );
};

export default PaymentSuccess;
