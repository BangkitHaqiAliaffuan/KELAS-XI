import React from "react";
import { Route, Routes } from "react-router-dom";
import Login from "./components/Login";
// import Dashboard from "./components/dashboard";

const App = () => {
  return 
  <Routes>
    <Route path="/" element={<Dashboard/>}/>
  </Routes>;
};

export default App;
