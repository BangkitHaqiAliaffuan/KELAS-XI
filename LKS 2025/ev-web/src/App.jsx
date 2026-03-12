import React, { useEffect, useState } from "react";
import "./App.css";
import { assets } from "./assets/assets.js";
const App = () => {
  const [text, setText] = useState("Dive into \n what you love");
  const [bg, setBg] = useState(assets.image1);
  const [prevBg, setPrevBg] = useState(null);
  const [play, setPlay] = useState(false);
  const [active, setActive] = useState(1);
  const [transitioning, setTransitioning] = useState(false);

  const changeBg = (newText, newBg) => {
    if (newBg === bg) return;
    setPrevBg(bg);
    setBg(newBg);
    setText(newText);
    setTransitioning(false);
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        setTransitioning(true);
      });
    });
  };

  useEffect(()=>{
    
   active == 1 ? changeBg("Dive into \n what you love", assets.image1) : 
   active == 2 ?  changeBg("Indulge \n your passions", assets.image2):
   active == 3 ?  changeBg("Give in to \n your passions", assets.image3) : ""
  }, [active])

  
const useEffectActive = useEffect(()=>{
    if(!play){
      const interval = setInterval(()=>{
        setActive(prev=>prev >= 3 ? 1 : prev+1)
      }, 3000)
      return ()=>clearInterval(interval)
    }
  }, [play])

  
  

  return (
    <div className="main-container">
      {/* Wrapper khusus background — selalu di belakang konten */}
      <div className="bg-wrapper">
        {prevBg && (
          <div
            style={{ backgroundImage: `url(${prevBg})` }}
            className={`bg-overlay prev-bg ${transitioning ? "slide-out" : ""}`}
          />
        )}
        <div
          style={{ backgroundImage: `url(${bg})` }}
          className={`bg-overlay next-bg ${transitioning ? "slide-in" : ""}`}
        />
      </div>

      {/* Wrapper konten — selalu di atas background */}
      <div className="content-wrapper">
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
              setActive(1)
            }}
            className={`btn ${active == 1 && "active"}`}
          ></div>
          
          <div
            onClick={() => {
              setActive(2)
            }}
                        className={`btn ${active === 2 && "active"}`}
          ></div>
          
          <div
            onClick={() => {
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
    </div>
  );
};

export default App;
