import React from 'react'
import './About.css'
import { assets } from '../../assets/assets'
const About = () => {
  return (
    <section id='skills'>
      <span className='skill-title'>What I do </span>
      <span className='skill-description'>i'm a skilled and passionate Web Developer with 2 years of experience</span>
      <div className='skill-bars'>
        <div className='skill-bar'>
          <img src={assets.uiDesign}/>
          <div className='skill-bar-text'>
            <h2>UI/UX Design</h2>
            <p>this is a demo text, you can write your own content here</p>
          </div>
        </div>
        <div className='skill-bar'>
          <img src={assets.websiteDesign}/>
          <div className='skill-bar-text'>
            <h2></h2>
            <p></p>
          </div>
        </div>
        <div className='skill-bar'>
          <img src={assets.appDesign}/>
          <div className='skill-bar-text'>
            <h2></h2>
            <p></p>
          </div>
        </div>
      </div>
    </section>
  )
}

export default About