import React from "react";
import { assets } from "../assets/assets";
import "./Chat.css";
const Chat = () => {
  return (
    <div className="chat-container">
      <div className="chats">
        <div className="chat">
          <img src={assets.user_icon} />
          <div className="chat-result">
            <p>
              Halo! Bisakah Anda membantu saya memahami konsep machine learning
              dasar?
            </p>
          </div>
        </div>
        <div className="chat bot">
          <img src={assets.logo} />
          <div className="chat-result">
            <p>
              Tentu! Machine learning adalah cabang dari artificial intelligence
              yang memungkinkan komputer untuk belajar dan membuat keputusan
              dari data tanpa diprogram secara eksplisit. Ada tiga jenis utama:
              supervised learning (pembelajaran terawasi), unsupervised learning
              (pembelajaran tidak terawasi), dan reinforcement learning
              (pembelajaran penguatan). Apakah ada aspek tertentu yang ingin
              Anda pelajari lebih dalam?
            </p>
          </div>
        </div>
      </div>
      <div className="chat-footer">
        <div className="input-prompt">
          <input type="text" placeholder="Send a message" />
          <button className="send">
            <img src={assets.send} />
          </button>
        </div>
        <div className="footer">Chat Gpt Model OpenAi</div>
      </div>
    </div>
  );
};

export default Chat;
