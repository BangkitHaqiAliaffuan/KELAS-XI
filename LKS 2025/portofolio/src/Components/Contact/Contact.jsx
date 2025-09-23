import React from 'react'
import './Contact.css'
import {assets} from '../../assets/assets.js'
import { send } from 'vite'
const Contact = () => {
  return (
    <section id='contact-page'>
        <div id='clients'>
            <h1 className='contact-page-title'>
                My Clients
            </h1>
            <p className='client-desc'>
            I have had the opportunity to work with diverse groups of people and organizations. Throughout my career, I have collaborated with talented teams to deliver innovative solutions and create meaningful impact across various industries.
            </p>

            <div className='client-images'>
                <img src={assets.walmart}/>
                <img src={assets.adobe}/>
                <img src={assets.microsoft}/>
                <img src={assets.facebook}/>
            </div>
        </div>
        <div id='contact'>
    <h1 className='contact-title'>
        Contact Me
    </h1>
    <span className='contact-desc'>
        Please fill out the form below to discuss any work opportunities.
    </span>

    <form className='contact-form'>
        <input type='text' className='name' placeholder='Your Name'/>
        <input type='email' className='email' placeholder='Insert Your Email Here'/>
        <textarea name='message' rows={5} className='message' placeholder='Tell us a message'></textarea>
        <button className='submit-btn' type='submit' value={send}>Submit</button>
        <div className='links'>
            <img src={assets.instagram} className='link'/>
            <img src={assets.facebookIcon} className='link'/>
            <img src={assets.twitter} className='link'/>
            <img src={assets.youtube} className='link'/>
        </div>
    </form>
        </div>
    </section>
  )
}

export default Contact