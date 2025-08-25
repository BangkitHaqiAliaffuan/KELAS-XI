import React from 'react'
import './Video.css'
import Playvideo from '../../Components/PlayVideo/Playvideo'
import Recommended from '../../Components/Recommended/Recommended'
const Video = () => {
  return (
    <div className='play-container'>
      <Playvideo/>
      <Recommended/>
    </div>
  )
}

export default Video