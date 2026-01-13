import React, { useEffect, useState } from 'react'

import Navbar from './components/Navbar'
import Login from './components/Login'
import { Route, Routes } from 'react-router-dom'
import Home from './components/Home'
import Profile from './components/Profile'



const App = () => {
  const [user, setUser] = useState([])

  useEffect(() => {
    const savedUser = localStorage.getItem("user")
    if(savedUser){
      setUser(JSON.parse(savedUser))
    }
  }, []);

  return (
    <div className='main-container'>
    <Navbar/>
    <Routes>
    <Route path='/' element={<Home/>}/>
    <Route path='/login' element={<Login/>}/>
    <Route path='/profile' element={<Profile/>}/>
    </Routes>
    </div>
  )
}

export default App