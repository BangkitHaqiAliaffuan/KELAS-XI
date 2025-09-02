import React from 'react'
import { Route, Routes } from 'react-router-dom'
// import Home from './Home'
import Sidebar from './Sidebar/Sidebar'
import Chat from './Chat/Chat'
import './App.css'
const App = () => {
  return (
   <>
   <div className='container'>
   
   <div className='left'>
   <Sidebar/>
   </div>

   <div className='right'>
   <Chat/>
   </div>
    

   </div>
   </>
  )
}

export default App