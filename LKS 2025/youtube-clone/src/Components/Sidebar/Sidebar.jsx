import React, { useState } from 'react'
import './Sidebar.css'
import assets from '../../assets'
const Sidebar = ({sidebar}) => {
    
  return (
    <div className={`sidebar ${sidebar?"":"small-sidebar"}` }>
        <div className='shortcut-links'>
            <div className='side-link'>
                <img src={assets.home}/>
                <p>Home</p>
            </div>
            <div className='side-link'>
                <img src={assets.game_icon}/>
                <p>Gaming</p>
            </div>
            <div className='side-link'>
                <img src={assets.automobiles}/>
                <p>Automobiles</p>
            </div>
            <div className='side-link'>
                <img src={assets.sports}/>
                <p>sports</p>
            </div>
            <div className='side-link'>
                <img src={assets.entertainment}/>
                <p>entertainment</p>
            </div>
            <div className='side-link'>
                <img src={assets.tech}/>
                <p>technology</p>
            </div>
            <div className='side-link'>
                <img src={assets.music}/>
                <p>music</p>
            </div>
            <div className='side-link'>
                <img src={assets.blogs}/>
                <p>blogs</p>
            </div>
            <div className='side-link'>
                <img src={assets.news}/>
                <p>news</p>
            </div>
        </div>
        <hr/>
        <div className='subscribed-list'>
            <h3>Subscribed</h3>
            <div className='side-link'>
                <img src={assets.jack}/>
                <p>Pewdiepie</p>
            </div>
            <div className='side-link'>
                <img src={assets.simon}/>
                <p>Mr.Beast</p>
            </div>
            <div className='side-link'>
                <img src={assets.tom}/>
                <p>Justin Bieber</p>
            </div>
            <div className='side-link'>
                <img src={assets.megan}/>
                <p>5 Minutes Craft</p>
            </div>
            <div className='side-link'>
                <img src={assets.cameron}/>
                <p>Nas Daily</p>
            </div>
        </div>
    </div>
  )
}

export default Sidebar