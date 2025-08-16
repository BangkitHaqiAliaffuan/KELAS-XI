import React from 'react'
import './Program.css'
import { assets } from '../../assets/assets'
const Program = () => {
  return (
    <div className='programs' >
        <div className='program'>
            <img src={assets.program_1} alt=''/>
            <div className='caption'>
                <img src={assets.program_icon_1}/>
                <p>Graduation Degree</p>
            </div>
        </div>
        <div className='program'>
            <img src={assets.program_2} alt=''/>
            <div className='caption'>
                <img src={assets.program_icon_2}/>
                <p>Masters Degree</p>
            </div>
        </div>
        <div className='program'>
            <img src={assets.program_3} alt=''/>
            <div className='caption'>
                <img src={assets.program_icon_1}/>
                <p>Post Graduation</p>
            </div>
        </div>
    </div>
  )
}

export default Program