import React from 'react'
import './App.css'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Home from './Components/Home'

const App = () => {
  return (
    <main style={{ margin: 0, padding: 0, background: '#000' }}>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Home />} />
        </Routes>
      </BrowserRouter>
    </main>
  )
}

export default App