import React from 'react'
import './Portofolio.css'
import { assets } from '../../assets/assets'
const Portofolio = () => {
  return (
    <section id='portofolio'>
        <h2 className='work-title'>
        My Portofolio
        </h2>
        <span className='work-desc'>
        I take pride in paying attention to the smallest details and making sure every project I work on meets the highest standards. Here are some of my recent works that showcase my skills and dedication to quality.
        </span>
        <div className='work-images'>
            <img src={assets.portfolio1}/>
            <img src={assets.portfolio2}/>
            <img src={assets.portfolio3}/>
            <img src={assets.portfolio4}/>
            <img src={assets.portfolio5}/>
            <img src={assets.portfolio6} />
        </div>
        <button className='work-btn'>See More</button>
    </section>
  )
}

export default Portofolio