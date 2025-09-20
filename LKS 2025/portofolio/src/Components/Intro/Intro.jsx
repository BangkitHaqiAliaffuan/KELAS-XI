import React from "react";
import { assets } from "../../assets/assets";
import { Link } from "react-scroll";
import './Intro.css'
const Intro = () => {
  return (
    <section id="intro">
      <div className="intro-content">
        <span className="hello">Hello,</span>
        <span className="intro-text">
          I'm
        <span className="intro-name"> Haqi</span>
          <br />
          Full Stack Dev
        </span>

        <p className="intro-para">
          Saya seorang pengembang full-stack yang berfokus pada pembuatan aplikasi<br/>
          web yang responsif dan mudah digunakan.  Saya menikmati merancang
        </p>

        <Link>
            <button className="btn"><img src={assets.hireme}/>Hire Me</button>
        </Link>
      </div>
      <img src={assets.image} className="bg" />
    </section>
  );
};

export default Intro;
