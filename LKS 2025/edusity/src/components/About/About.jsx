import React from 'react'
import { assets } from '../../assets/assets'
import './About.css'

const About = () => {
  return (
    <div className='about'>
        <div className='about-left'>
            <img src={assets.about} className='about-img'/>
            <img src={assets.play_icon} className='play-icon'/>
        </div>
        <div className='about-right'>
            <h3>ABOUT UNIVERSITY</h3>
            <h2>Nurturing Tomorrow Leaders Today</h2>
            <p>Embark on a transformative educational journey with our university's comprehensive education programs. Our cutting-edge curriculum is designed to empower students with the knowledge, skills, and experiences needed to excel in the dynamic field of education.</p>
            <p>With a focus on innovation, hands-on learning, and personalized mentorship, our programs prepare aspiring educators to make a meaningful impact in classrooms, schools, and communities worldwide. Join us as we shape the future of education and create tomorrow's leaders today.</p>
        </div>
    </div>
  )
}

export default About