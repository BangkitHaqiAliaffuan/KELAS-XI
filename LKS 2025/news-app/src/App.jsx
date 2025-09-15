import React, { useState } from 'react'
import './App.css'
import Navbar from './Components/Navbar/Navbar'
import NewsBoard from './Components/NewsBoard/NewsBoard'
const App = () => {

  const [category, setCategory] = useState("general")

  return (
    <div>
      <Navbar setCategory={setCategory} category={category}/>
      <NewsBoard category={category}/>
    </div>
  )
}

export default App