import React from 'react'
import './Hero.css'
import { assets } from '../../assets/assets'

const Hero = () => {
  return (
    <div className='hero container'>
        <div className='hero-text'>
            <h1>We Ensure Education for the better world</h1>
            <p></p>
            <button className='btn'>Explore more
                <img src={assets.dark_arrow}/>
            </button>
        </div>
    </div>
  )
}

export default Hero