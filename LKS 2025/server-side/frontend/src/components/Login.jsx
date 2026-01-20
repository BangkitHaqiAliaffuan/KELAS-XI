// import React from 'react'
import React, { useState } from "react";
import { Route, Router, useNavigate } from "react-router-dom";
import Dashboard from "./dashboard";
const Login = () => {
 const [idnumber, setIdnumber] = useState("");
   const [password, setPassword] = useState("");
   const navigate = useNavigate()
   
 
   const handleSubmit = async (e) => {
     e.preventDefault();
 
     try {
       const response = await fetch("http://127.0.0.1:8000/api/v1/auth/login", {
         method: "POST",
          headers: {
           "Content-Type": "application/json",
           "Accept": "application/json"
         },
         body: JSON.stringify({
           idnumber: idnumber,
           password: password,
         }),
       });
 
     //   console.log(idnumber)
 
     
     const data = await response.json();
     localStorage.setItem('token', data.test.login_tokens)
 
       navigate('/')
     //   console.log(data.test.login_tokens);
     
     } catch (error) {}
   };
 
   return (
     <>
       <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-primary">
         <div class="container">
           <a class="navbar-brand" href="#">
             Job Seekers Platform
           </a>
           <button
             class="navbar-toggler"
             type="button"
             data-toggle="collapse"
             data-target="#navbarsExampleDefault"
             aria-controls="navbarsExampleDefault"
             aria-expanded="false"
             aria-label="Toggle navigation"
           >
             <span class="navbar-toggler-icon"></span>
           </button>
 
           <div class="collapse navbar-collapse" id="navbarsExampleDefault">
             <ul class="navbar-nav ml-auto">
               <li class="nav-item">
                 <a class="nav-link" href="#">
                   Login
                 </a>
               </li>
             </ul>
           </div>
         </div>
       </nav>
 
       <main>
         <header class="jumbotron">
           <div class="container text-center m-5">
             <h1 class="display-4">Job Seekers Platform</h1>
           </div>
         </header>
 
         <div class="container">
           <div class="row justify-content-center">
             <div class="col-md-6">
               <form class="card card-default" onSubmit={handleSubmit}>
                 <div class="card-header">
                   <h4 class="mb-0">Login</h4>
                 </div>
                 <div class="card-body">
                   <div class="form-group row align-items-center">
                     <div class="col-4 text-right">ID Card Number</div>
                     <div class="col-8">
                       <input
                         type="text"
                         class="form-control"
                         value={idnumber}
                         onChange={(e) => setIdnumber(e.target.value)}
                       />
                     </div>
                   </div>
                   <div class="form-group row align-items-center">
                     <div class="col-4 text-right">Password</div>
                     <div class="col-8">
                       <input
                         type="password"
                         class="form-control"
                         value={password}
                         onChange={(e) => setPassword(e.target.value)}
                       />
                     </div>
                   </div>
                   <div class="form-group row align-items-center mt-4">
                     <div class="col-4"></div>
                     <div class="col-8">
                       <button class="btn btn-primary">Login</button>
                     </div>
                   </div>
                 </div>
               </form>
             </div>
           </div>
         </div>
       </main>
 
       <footer>
         <div class="container">
           <div class="text-center py-4 text-muted">
             Copyright &copy; 2023 - Web Tech ID
           </div>
         </div>
       </footer>
     </>
   );
}

export default Login