import React from 'react'
import Navbar from './Components/Navbar/Navbar'
import Intro from './Components/Intro/Intro'
import About from './Components/About/About'
import Portofolio from './Components/Portofolio/Portofolio'

const App = () => {
  return (
    <div>
      <Navbar/>
      <Intro/>
      <About/>
      <Portofolio/>
    </div>
  )
}

export default App