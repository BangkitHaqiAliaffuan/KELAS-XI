import React from 'react'
import './Campus.css'
import { assets } from '../../assets/assets'
const Campus = () => {
  return (
    <div className='campus'>
        <div className='gallery'>
            <img src={assets.gallery_1}/>
            <img src={assets.gallery_2}/>
            <img src={assets.gallery_3}/>
            <img src={assets.gallery_4}/>
        </div>
        <button className='btn dark-btn'>See more here <img src={assets.white_arrow}/></button>
    </div>
  )
}

export default Campus