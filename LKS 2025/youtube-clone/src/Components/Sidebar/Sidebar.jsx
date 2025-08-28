import React, { useState } from 'react'
import './Sidebar.css'
import assets from '../../assets'
const Sidebar = ({sidebar, category, setCategory}) => {
    
  return (
    <div className={`sidebar ${sidebar?"":"small-sidebar"}` }>
        <div className='shortcut-links'>
            <div className={`side-link ${category===0?"active":""}`} onClick={()=>setCategory(0)}>
                <img src={assets.home}/>
                <p>Home</p>
            </div>
            <div className={`side-link ${category===20?"active":""}`} onClick={()=>setCategory(20)}>
                <img src={assets.game_icon}/>
                <p>Gaming</p>
            </div>
            <div className={`side-link ${category===2?"active":""}`} onClick={()=>setCategory(2)}>
                <img src={assets.automobiles}/>
                <p>Automobiles</p>
            </div>
            <div className={`side-link ${category===17?"active":""}`} onClick={()=>setCategory(17)}>
                <img src={assets.sports}/>
                <p>sports</p>
            </div>
            <div className={`side-link ${category===24?"active":""}`} onClick={()=>setCategory(24)}>
                <img src={assets.entertainment}/>
                <p>entertainment</p>
            </div>
            <div className={`side-link ${category===28?"active":""}`} onClick={()=>setCategory(28)}>
                <img src={assets.tech}/>
                <p>technology</p>
            </div>
            <div className={`side-link ${category===10?"active":""}`} onClick={()=>setCategory(10)}>
                <img src={assets.music}/>
                <p>music</p>
            </div>
            <div className={`side-link ${category===22?"active":""}`} onClick={()=>setCategory(22)}>
                <img src={assets.blogs}/>
                <p>blogs</p>
            </div>
            <div className={`side-link ${category===25?"active":""}`} onClick={()=>setCategory(25)}>
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