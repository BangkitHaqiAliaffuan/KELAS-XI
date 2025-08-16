import React, { useEffect, useState } from "react";
import "./Navbar.css";
import { assets } from "../../assets/assets";
import {Link} from 'react-scroll'
const Navbar = () => {

  const [sticky, setSticky] = useState(false) 

  useEffect(()=>{
    window.addEventListener('scroll', ()=>{
      window.scrollY > 50 ? setSticky(true) : setSticky(false) 
    })
  },[])

  const [mobileMenu, setMobileMenu] = useState(false)
  const toggleMenu=()=>{
    mobileMenu? setMobileMenu(false) : setMobileMenu(true)
  }

  return (
    
    <div className="nav-container">
      
    <nav className={`container ${sticky? 'dark-nav' : ''}`}>
      <img className="logo" src={assets.logo} alt="" />

      <ul className={mobileMenu?'':'hide-mobile-menu'}>
        <li><Link to='hero' smooth={true} offset={0} duration={500}>Home</Link></li>
        
        <li><Link to='program' smooth={true} offset={-260} duration={500}>Program</Link></li>
        
        
        <li><Link to='about' smooth={true} offset={-150} duration={500}>About us</Link></li>
        
        <li><Link to='campus' smooth={true} offset={-260} duration={500}>Campus</Link></li>
        
        <li><Link to='testimonials' smooth={true} offset={-260} duration={500}>Testimonials</Link></li>
        
        <li>
          <Link to='contact' className="btn btn-contact" smooth={true} offset={-260} duration={500}>Contact Us</Link>
        </li>
      </ul>
      <img src={assets.menu_icon} className="menu-icon" onClick={toggleMenu}/>
    </nav>
    </div>
  );
};

export default Navbar;
