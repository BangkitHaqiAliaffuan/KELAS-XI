import React, { useState } from "react";
import { assets } from "../assets/assets";
import "./Chat.css";
import { fetchGemini } from "../api/gemini";
// import { send } from "vite";
const Chat = () => {
  const [messages, setMessages] = useState([
    { role: "user", text: "" },
    { role: "bot", text: "" },
  ]);

  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [isSend, setIsSend] = useState(false);

  const handleinput = (input) => {
    input != "" ? setIsSend(true) : setIsSend(false);
  };

  const handleSend = async () => {
    if (loading) return;
    setMessages([...messages, { role: "user", text: input }]);
    setLoading(true);
    const reply = await fetchGemini(input);
    setMessages((msgs) => [...msgs, { role: "bot", text: reply }]);
    setInput("");
    setLoading(false);
  };

  return (
    <div className="chat-container">
      <div className="chats">
        {messages.map((item, index) => (
          
          item.text?
          <div className={`chat${item.role === "bot"?" bot":''}`} key={index}>
            <img src={item.role=="bot"?assets.logo:assets.chatgpt} />
            <div className="chat-result">
              <p>
                {item.text}
              </p>
            </div>
          </div>:
          ''

        ))}
        {loading && (
          <div className="chat bot">
            <img src={assets.logo}/>
            <div className="chat-result">
              <p>Loading....</p>
            </div>
          </div>
        )}
      </div>
      <div className="chat-footer">
        <div className="input-prompt">
          <input
            value={input}
            onChange={(e) => {
              setInput(e.target.value);
              handleinput(e.target.value);
            }}
            type="text"
            placeholder="Send a message"
          />
          {isSend ? (
            <button className="send" onClick={handleSend} disabled={loading}>
              <img src={assets.send} />
            </button>
          ) : (
            ""
          )}
        </div>
        <div className="footer">Chatgpt</div>
      </div>
    </div>
  );
};

export default Chat;
