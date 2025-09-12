import React from 'react'
import './App.css'
import {assets} from './assets/assets.js'
const App = () => {
  return (
    <div style={{backgroundImage:`url(${assets.image1})`}} className='main-container'>
      <nav className='nav'>
        <div className='logo'>
          EV-olution
        </div>

        <div className='nav-items'>
          <div className='item'>
            Home
          </div>
          <div className='item'>
            Explore
          </div>
          <div className='item'>
            About
          </div>
          <div className='item active'>
            Contact
          </div>
        </div>
      </nav>

      <div className='hero'>
        <div className='title'>
          <h1>Give in to your passions</h1>
        </div>

        <div className='btn-wrapper'>
          <p>Explore the features</p>
          <button>
            <img src={assets.arrow_btn}/>
          </button>
        </div>
      </div>

    <div className='features'>
      <div className='round-btns'>
        <div className='btn'></div>
        <div className='btn'></div>
        <div className='btn active'></div>
      </div>

      <div className='play-container'>
        <div className='btn-play'>
          <img src={assets.play_icon}/>
        </div>

        See the video
      </div>
    </div>


    </div>
  )
}

export default App