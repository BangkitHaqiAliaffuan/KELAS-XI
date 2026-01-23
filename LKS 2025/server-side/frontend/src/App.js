import React from "react";
import { Route, Routes } from "react-router-dom";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import Request from "./components/Request";
import JobVacan from "./components/JobVacan";
import JobDetail from "./components/JobDetail";

const App = () => {
  return (
    <Routes>
      <Route path="/request" element={<Request />} />
      <Route path="/" element={<Login />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/jobvacan" element={<JobVacan />} />
      <Route path="/jobdetail" element={<JobDetail />} />
    </Routes>
  );
};

export default App;
