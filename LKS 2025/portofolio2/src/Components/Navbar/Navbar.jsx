import React from 'react'
import './Navbar.css'
import { Link } from 'react-scroll'
import { assets } from '../../assets/assets'
const Navbar = () => {
  return (
    <nav className='navbar'>
        <img className='logo' src={assets.logo}/>
        <div className='desktop-menu'>
            <Link className='desktop-menu-list-item'>Home</Link>
            <Link className='desktop-menu-list-item'>Clients</Link>
            <Link className='desktop-menu-list-item'>About</Link>
            <Link className='desktop-menu-list-item'>Portofolio</Link>
        </div>
        <button className='desktop-menu-btn'>
            <img src={assets.contact} className='desktop-menu-img'/>
            Contact Me
        </button>
    </nav>
  )
}

export default Navbar