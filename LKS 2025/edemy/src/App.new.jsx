import React from 'react';
import { BrowserRouter, Routes, Route, useLocation, Navigate } from 'react-router-dom';
import { OrganizationProvider } from '@clerk/clerk-react';
import { AppProvider } from './context/AppContext.jsx';
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
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

const Shell = () => {
  const location = useLocation();
  const isEducatorRoute = location.pathname.startsWith('/educator');
  const isAuthPage = location.pathname === '/educator/auth' || location.pathname === '/educator';

  return (
    <OrganizationProvider>
      <div className="min-h-screen flex flex-col bg-gray-50">
        {!isEducatorRoute && !isAuthPage && <Header />}
        <main className="flex-1">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/courses" element={<Courses />} />
            <Route path="/course/:id" element={<CourseDetail />} />

            {/* Student Routes */}
            <Route path="/my-courses" element={
              <ProtectedRoute requiredRole="student">
                <StudentDashboard />
              </ProtectedRoute>
            } />

            {/* Educator Auth Route */}
            <Route path="/educator" element={<EducatorAuth />} />
            <Route path="/educator/auth" element={<EducatorAuth />} />

            {/* Educator Routes */}
            <Route path="/educator/*" element={
              <ProtectedRoute requiredRole="educator">
                <EducatorLayout />
              </ProtectedRoute>
            }>
              <Route path="dashboard" element={<EducatorDashboard />} />
              <Route path="add-course" element={<EducatorAddCourse />} />
              <Route path="my-courses" element={<EducatorMyCourses />} />
              <Route path="students" element={<EducatorStudentsEnrolled />} />
            </Route>
          </Routes>
        </main>
        {!isEducatorRoute && !isAuthPage && <Footer />}
      </div>
    </OrganizationProvider>
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
