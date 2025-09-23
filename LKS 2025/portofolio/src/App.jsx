import React from 'react'
import Navbar from './Components/Navbar/Navbar'
import Intro from './Components/Intro/Intro'
import About from './Components/About/About'
import Portofolio from './Components/Portofolio/Portofolio'
import Contact from './Components/Contact/Contact'
import Footer from './Components/Footer/Footer'

const App = () => {
  return (
    <div>
      <Navbar/>
      <Intro/>
      <About/>
      <Portofolio/>
      <Contact/>
      <Footer/>
    </div>
  )
}

export default App