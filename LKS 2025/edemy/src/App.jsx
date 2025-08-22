import React from 'react';
import { BrowserRouter, Routes, Route, useLocation, Navigate } from 'react-router-dom';
import { AppProvider } from './context/AppContext.jsx';
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import EducatorOnlyAccess from './components/EducatorOnlyAccess.jsx';
import SecurityDebugPanel from './components/SecurityDebugPanel.jsx';
import ClerkSyncWrapper from './components/ClerkSyncWrapper.jsx';
import Home from './pages/Home.jsx';
import Courses from './pages/Courses.jsx';
import CourseDetail from './pages/CourseDetail.jsx';
import EducatorAuth from './pages/EducatorAuth.jsx';
import StudentDashboard from './pages/StudentDashboard.jsx';
import EducatorLayout from './pages/educator/Layout.jsx';
import EducatorDashboard from './pages/educator/Dashboard.jsx';
import EducatorAddCourse from './pages/educator/AddCourse.jsx';
import EducatorMyCourses from './pages/educator/MyCourses.jsx';
import EducatorStudentsEnrolled from './pages/educator/StudentsEnrolled.jsx';
import PaymentSuccess from './components/PaymentSuccess.jsx';
import PaymentTestPage from './pages/PaymentTestPage.jsx';

const Shell = () => {
  const location = useLocation();
  const isDashboardRoute = location.pathname.startsWith('/dashboard');
  const isAuthPage = location.pathname === '/dashboard/auth';

  return (
    <ClerkSyncWrapper>
      <div className="min-h-screen flex flex-col bg-gray-50">
        {!isDashboardRoute && !isAuthPage && <Header />}
        <main className="flex-1">
          <Routes>
          <Route path="/" element={<Home />} />
            <Route path="/courses" element={<Courses />} />
            <Route path="/course/:id" element={<CourseDetail />} />
            <Route path="/payment/success" element={<PaymentSuccess />} />
            <Route path="/payment/debug" element={<PaymentTestPage />} />

            {/* Student Routes */}
            <Route path="/my-courses" element={
              <ProtectedRoute requiredRole="student">
                <StudentDashboard />
              </ProtectedRoute>
            } />

            {/* Dashboard Routes - Open to everyone */}
            <Route path="/dashboard" element={<Navigate to="/dashboard/home" replace />} />
            
            {/* Dashboard Routes - No protection needed */}
            <Route path="/dashboard/*" element={<EducatorLayout />}>
              <Route path="home" element={<EducatorDashboard />} />
              <Route path="add-course" element={<EducatorAddCourse />} />
              <Route path="my-courses" element={<EducatorMyCourses />} />
              <Route path="students" element={<EducatorStudentsEnrolled />} />
            </Route>
          </Routes>
        </main>
        {!isDashboardRoute && !isAuthPage && <Footer />}
        <SecurityDebugPanel />
      </div>
    </ClerkSyncWrapper>
  );
};

function App() {
  return (
    <BrowserRouter>
      <AppProvider>
        <Shell />
      </AppProvider>
    </BrowserRouter>
  );
}

export default App;
