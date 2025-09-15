import React from 'react'
import './Navbar.css'
const Navbar = ({setCategory, category}) => {
  return (
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
  <div class="container-fluid flex align-content-between">
    <a class="navbar-brand fs-1" href="#">HoodNews</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav">
        <li class="nav-item">
          <a class="nav-link active" onClick={()=>{setCategory("entertainment")}} aria-current="page" href="#">Entertainment</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" onClick={()=>{setCategory("general")}} aria-current="page" href="#">General</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" onClick={()=>{setCategory("business")}} aria-current="page" href="#">Business</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" onClick={()=>{setCategory("health")}} aria-current="page" href="#">Health</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" onClick={()=>{setCategory("sports")}} aria-current="page" href="#">Sports</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" onClick={()=>{setCategory("science")}} aria-current="page" href="#">Science</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" onClick={()=>{setCategory("technology")}} aria-current="page" href="#">Technology</a>
        </li>
      </ul>
    </div>
  </div>
</nav>
  )
}

export default Navbar