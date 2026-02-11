import React from 'react'
import { Link, useNavigate } from 'react-router-dom'

const HomeAdmin = () => {

  const navigate = useNavigate()
  const token =  localStorage.getItem('admintoken')
  if(!token){
    navigate('/')
  }


  return (
    <>
      <nav class="navbar navbar-expand-lg sticky-top bg-primary navbar-dark">
      <div class="container">
        <a class="navbar-brand" href="index.html">Administrator Portal</a>
        <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
          
         <li><Link to="/list-admin" class="nav-link px-2 text-white">List Admins</Link></li>
         <li><Link to="/list-user" class="nav-link px-2 text-white">List Users</Link></li>
         <li class="nav-item">
           <a class="nav-link active bg-dark" href="#">Welcome, Administrator</a>
         </li> 
         <li class="nav-item">
          <a href="../signin.html" class="btn bg-white text-primary ms-4">Sign Out</a>
         </li>
       </ul> 
      </div>
    </nav>

    <main>

      <div class="hero py-5 bg-light">
         <div class="container text-center">
          <h1 class="mb-0 mt-0">Dashboard</h1>
         </div>
      </div>

      <div class="list-form py-5">
         <div class="container">
          <h5 class="alert alert-info">
            Welcome, Administrator. Don't forget to sign out when you are finished using this page
          </h5>
         </div>
      </div>
      
    </main>

    </>
  )
}

export default HomeAdmin