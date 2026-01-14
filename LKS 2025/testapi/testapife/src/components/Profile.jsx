import React from 'react'
import './Profile.css'
import profile from '../assets/profile.png'
const Profile = () => {
  const user = JSON.parse(localStorage.getItem("user"));
  // console.log("berhasil", user)  
  return (
    <div className="container-manual">    
      <div className="profile-container">
        <div className="img-container">
          <img src={profile}/>
        </div>
          <br/>
          <div className='profile-item'>
            <div className='title'>Name</div>
            <div className='value'>{user.name}</div>
          </div>
          <div className='profile-item'>
            <div className='title'>Email</div>
            <div className='value'>{user.email}</div>
          </div>
          <br/>
      </div>
    </div>
  )
}

export default Profile