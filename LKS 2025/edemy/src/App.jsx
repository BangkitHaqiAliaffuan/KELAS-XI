import React from 'react';
import { BrowserRouter, Routes, Route, Outlet, useLocation, Navigate } from 'react-router-dom';
import { AppProvider } from './context/AppContext.jsx';
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';
import Home from './pages/Home.jsx';
import Courses from './pages/Courses.jsx';
import CourseDetail from './pages/CourseDetail.jsx';
import Login from './pages/Login.jsx';
import Signup from './pages/Signup.jsx';
import EducatorLayout from './pages/educator/Layout.jsx';
import EducatorDashboard from './pages/educator/Dashboard.jsx';
import EducatorAddCourse from './pages/educator/AddCourse.jsx';
import EducatorMyCourses from './pages/educator/MyCourses.jsx';
import EducatorStudentsEnrolled from './pages/educator/StudentsEnrolled.jsx';

const Shell = () => {
  const location = useLocation();
  const isEducatorRoute = location.pathname.startsWith('/educator');

  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      {!isEducatorRoute && <Header />}
      <main className="flex-1">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/courses" element={<Courses />} />
          <Route path="/course/:id" element={<CourseDetail />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />

          <Route path="/educator" element={<Navigate to="/educator/dashboard" replace />} />
          <Route path="/educator/*" element={<EducatorLayout />}>
            <Route index element={<EducatorDashboard />} />
            <Route path="dashboard" element={<EducatorDashboard />} />
            <Route path="add-course" element={<EducatorAddCourse />} />
            <Route path="my-courses" element={<EducatorMyCourses />} />
            <Route path="students-enrolled" element={<EducatorStudentsEnrolled />} />
          </Route>
        </Routes>
        <Outlet />
      </main>
      {!isEducatorRoute && <Footer />}
    </div>
  );
};

const App = () => {
  return (
    <AppProvider>
      <BrowserRouter>
        <Shell />
      </BrowserRouter>
    </AppProvider>
  );
};

export default App;