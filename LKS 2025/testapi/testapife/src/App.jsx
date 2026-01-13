import React, { useEffect, useState } from 'react'

import Navbar from './components/Navbar'
import Login from './components/Login'
import { Route, Routes } from 'react-router-dom'
import Home from './components/Home'



const App = () => {
  const [user, setUser] = useState([])

  useEffect(() => {
    const savedUser = localStorage.getItem("user")
    if(savedUser){
      setUser(JSON.parse(savedUser))
    }
  }, []);

  return (
    <>
    <Navbar/>
    <Routes>
    <Route path='/' element={<Home/>}/>
    <Route path='/login' element={<Login/>}/>
    </Routes>
    </>
  )
}

export default App