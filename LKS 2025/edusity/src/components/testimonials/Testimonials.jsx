import React, { useRef } from "react";

import "./Testimonials.css";
import { assets } from "../../assets/assets";
const Testimonials = () => {

    const slider = useRef()
    let tx = 0


    const slideForward = ()=>{
        if(tx> -50){
            tx -= 25
        }

        slider.current.style.transform = `translateX(${tx}%)`
    }
    const slideBackward = ()=>{
        if(tx < 0){
            tx += 25
        }

        slider.current.style.transform = `translateX(${tx}%)`
    }

  return (
    <div className="testimonials">
      <img src={assets.next_icon} className="next-btn" onClick={slideForward}/>
      <img src={assets.back_icon} className="back-btn" onClick={slideBackward}/>

      <div className="slider">
        <ul ref={slider}>
          <li>
            <div className="slide">
              <div className="user-info">
                <img src={assets.user_1} />
                <div>
                  <h3>Emily Johnson</h3>
                  <span>Computer Science Graduate, Class of 2023</span>
                </div>
              </div>
              <p>Choosing this university was the best decision of my life. The professors are incredibly supportive, and the hands-on learning approach helped me land my dream job at a tech startup right after graduation. The networking opportunities here are unparalleled!</p>
            </div>
          </li>
          <li>
            <div className="slide">
              <div className="user-info">
                <img src={assets.user_2} />
                <div>
                  <h3>Michael Rodriguez</h3>
                  <span>Business Administration, Class of 2022</span>
                </div>
              </div>
              <p>The university's emphasis on practical skills and real-world applications made all the difference. I was able to start my own consulting firm just one year after graduation, thanks to the entrepreneurship program and mentorship I received here.</p>
            </div>
          </li>
          <li>
            <div className="slide">
              <div className="user-info">
                <img src={assets.user_3} />
                <div>
                  <h3>Sarah Thompson</h3>
                  <span>Psychology Major, Current Student</span>
                </div>
              </div>
              <p>As an international student, I was nervous about adapting to a new environment. However, the university's inclusive community and excellent support services made me feel at home immediately. The research opportunities are amazing!</p>
            </div>
          </li>
          <li>
            <div className="slide">
              <div className="user-info">
                <img src={assets.user_4} />
                <div>
                  <h3>David Chen</h3>
                  <span>Engineering Graduate, Class of 2021</span>
                </div>
              </div>
              <p>The state-of-the-art facilities and industry connections at this university are exceptional. I completed several internships during my studies, which gave me invaluable experience and helped me secure a position at a Fortune 500 company.</p>
            </div>
          </li>
        </ul>
      </div>
    </div>
  );
};

export default Testimonials;
