import React from 'react'
import './Navbar.css'
import react from '../../assets/react.svg'
import { assets } from '../../assets/assets'


const Navbar = () => {
  return (
    <nav className='nav-container'>
        <div className='logo'>
            <img src={assets.write}/>
            <p>ChatGPT</p>
        </div>
    </nav>
  )
}

export default Navbar