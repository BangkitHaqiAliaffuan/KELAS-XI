import React from 'react'
import './About.css'
import { assets } from '../../assets/assets'
const About = () => {
  return (
    <section id='skills'>
      <span className='skill-title'>What I do </span>
  <span className='skill-description'>Saya seorang Web Developer yang berpengalaman selama 2 tahun, fokus pada pembuatan situs dan aplikasi yang responsif serta mudah digunakan. Saya mengutamakan kualitas kode, pengalaman pengguna, dan solusi yang dapat diskalakan.</span>
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
            <h2>Web Design</h2>
            <p>this is a demo text, you can write your own content here</p>
          </div>
        </div>
        
        <div className='skill-bar'>
          <img src={assets.appDesign}/>
          <div className='skill-bar-text'>
            <h2>App Design</h2>
            <p>this is a demo text, you can write your own content here</p>
          </div>
        </div>
        
      </div>
    </section>
  )
}

export default About