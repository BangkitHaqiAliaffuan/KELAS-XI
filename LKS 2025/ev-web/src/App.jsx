import React, { useEffect, useState } from "react";
import "./App.css";
import { assets } from "./assets/assets.js";
const App = () => {
  const [text, setText] = useState("Dive into \n what you love");
  const [bg, setBg] = useState(assets.image1);
  const [play, setPlay] = useState(false);
  const [active, setActive] = useState(1);


  useEffect(()=>{
    
   active == 1 ? handleClick("Dive into \n what you love", assets.image1) : 
   active == 2 ?  handleClick("Indulge \n your passions", assets.image2):
   active == 3 ?  handleClick("Indulge \n your passions", assets.image3) : ""
  })

  
const useEffectActive = useEffect(()=>{
    if(!play){
      const interval = setInterval(()=>{
        setActive(prev=>prev >= 3 ? 1 : prev+1)
      }, 3000)
      return ()=>clearInterval(interval)
    }
  }, [play])

  
  

  const handleClick = (text, url) => {
    setText(text);
    setBg(url);
    // console.log(bg);
  };

  return (
    <div style={{ backgroundImage: `url(${bg})` }} className="main-container">
      {play ? <video src={assets.video1} autoPlay muted loop/>:""}
      <nav className="nav">
        <div className="logo">EV-olution</div>

        <div className="nav-items">
          <div className="item">Home</div>
          <div className="item">Explore</div>
          <div className="item">About</div>
          <div className="item active">Contact</div>
        </div>
      </nav>

      <div className="hero">
        <div className="title">
          <p className="title">
            {text.split("\n").map((line, idx) => (
              <React.Fragment key={idx}>
                {line}
                {idx !== text.split("\n").length - 1 && <br />}
                {/* {console.log(idx, text.split("\n").length - 1)} */}
              </React.Fragment>
            ))}
          </p>
        </div>

        <div className="btn-wrapper">
          <p>Explore the features</p>
          <button>
            <img src={assets.arrow_btn} />
          </button>
        </div>
      </div>

      <div className="features">
        <div className="round-btns">
          <div
            onClick={() => {
              handleClick("Dive into \n what you love", assets.image1);

              setActive(1)
            }}
            className={`btn ${active == 1 && "active"}`}
          ></div>
          
          <div
            onClick={() => {
              handleClick("Indulge \n your passions", assets.image2);
                            setActive(2)
            }}
                        className={`btn ${active === 2 && "active"}`}
          ></div>
          
          <div
            onClick={() => {
              handleClick("Give in to \n your passions", assets.image3);
                            setActive(3)
            }}
                        className={`btn ${active === 3 && "active"}`}
          ></div>
          
        </div>

        <div className="play-container">
          <div onClick={()=>{ setPlay(prev=>!prev)}} className="btn-play">
            <img src={!play? assets.play_icon:assets.pause_icon} />
          </div>
          See the video
        </div>
      </div>
    </div>
  );
};

export default App;
