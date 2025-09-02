import React from "react";
import { assets } from "../assets/assets";
// import Home from "../Home";
import './Sidebar.css'
const Sidebar = () => {
  return (
    <div className="main-container">
      <div className="sidebar">
        <div className="logo">
          <img src={assets.chatgpt} />
          Chatgpt
        </div>

        <div className="new-chat">
          <img src={assets.add} />
          New Chat
        </div>

        <div className="chat-container">
          <div className="chat">
            <img src={assets.message} />
            <div className="title">What Is Programming?</div>
          </div>
          <div className="chat">
            <img src={assets.message} />
            <div className="title">What Is Programming?</div>
          </div>
        </div>
      </div>

      <div className="sidebar-menu">
        <div className="menu-container">
          <div className="menu">
            <img src={assets.home} />
            Home
          </div>
          <div className="menu">
            <img src={assets.bookmark} />
            Saved
          </div>
          <div className="menu">
            <img src={assets.rocket} />
            Upgrade Level
          </div>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
