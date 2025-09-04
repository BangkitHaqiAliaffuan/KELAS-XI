import React, { useState } from 'react'
import { assets } from './assets/assets'
import './App.css'
const App = () => {
  
  const [isTick, isSetTick] = useState(false)

  const handleTick = ()=>{
    isTick?isSetTick(false):isSetTick(true)
  }

  return (
    <div className='main-container'>
      <div className='logo'>
        <h2>To-Do-List</h2>
      </div>

      <div className='input-container'>
        <input type='text' placeholder='Add Your Text'/>
        <button className='add-button'>ADD</button>
      </div>

      <div className='todo-container'>
        <div className='todo'>
          <img src={!isTick?assets.not_tick:assets.tick} onClick={handleTick}/>

          <div className='todo-title'>
            First

            <img src={assets.cross}/>
          </div>

        </div>
      </div>
    </div>
  )
}

export default App