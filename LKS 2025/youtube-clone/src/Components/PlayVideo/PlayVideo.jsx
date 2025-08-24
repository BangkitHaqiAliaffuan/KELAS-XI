import React from 'react'
import './PlayVideo.css'
import assets from '../../assets'

const Playvideo = () => {
  return (
    <div className='play-video'>
        <video src={assets.video} controls autoPlay muted></video>
        <h3>Best Youtube Channel To Learn React JS</h3>
        <div className='play-video-info'>
            <p>1225 Views &bull; 2 days ago</p>
            <div>
                <span><img src={assets.like}/>125</span>
                <span><img src={assets.dislike}/>23</span>
                <span><img src={assets.share}/>Share</span>
                <span><img src={assets.save}/>Save</span>
            </div>
        </div>
        <hr/>
        <div className='publisher'>
            <img src={assets.jack}/>
            <div>
                <p>GreatStack</p>
                <span>1M Subscribers</span>
            </div>
            <button>Subscribe</button>
        </div>
        <div className='vid-description'>
            <p>Channel That Makes learning easy</p>
            <p>lorem impteuse tiejqwiojtio tuebnwbtwet uiehwtiuhwe</p>
            <hr/>
            <h4>1500 Comments</h4>
            <div className='comment'>
                <img src={assets.user_profile}/>
                <div>
                     <h3>Maria Adamatatrore<span>1 day ago</span></h3>
                     <p>lreomet etoo etojkoet oetojeot</p>
                     <div className='comment-action'>
                        <img src={assets.like}/>
                        <span>244</span>
                        <img src={assets.dislike}/>
                        <span>2000</span>
                     </div>
                </div>
            </div>
            <div className='comment'>
                <img src={assets.user_profile}/>
                <div>
                     <h3>Maria Adamatatrore<span>1 day ago</span></h3>
                     <p>lreomet etoo etojkoet oetojeot</p>
                     <div className='comment-action'>
                        <img src={assets.like}/>
                        <span>244</span>
                        <img src={assets.dislike}/>
                        <span>2000</span>
                     </div>
                </div>
            </div>
            <div className='comment'>
                <img src={assets.user_profile}/>
                <div>
                     <h3>Maria Adamatatrore<span>1 day ago</span></h3>
                     <p>lreomet etoo etojkoet oetojeot</p>
                     <div className='comment-action'>
                        <img src={assets.like}/>
                        <span>244</span>
                        <img src={assets.dislike}/>
                        <span>2000</span>
                     </div>
                </div>
            </div>
            <div className='comment'>
                <img src={assets.user_profile}/>
                <div>
                     <h3>Maria Adamatatrore<span>1 day ago</span></h3>
                     <p>lreomet etoo etojkoet oetojeot</p>
                     <div className='comment-action'>
                        <img src={assets.like}/>
                        <span>244</span>
                        <img src={assets.dislike}/>
                        <span>2000</span>
                     </div>
                </div>
            </div>
            <div className='comment'>
                <img src={assets.user_profile}/>
                <div>
                     <h3>Maria Adamatatrore<span>1 day ago</span></h3>
                     <p>lreomet etoo etojkoet oetojeot</p>
                     <div className='comment-action'>
                        <img src={assets.like}/>
                        <span>244</span>
                        <img src={assets.dislike}/>
                        <span>2000</span>
                     </div>
                </div>
            </div>
            <div className='comment'>
                <img src={assets.user_profile}/>
                <div>
                     <h3>Maria Adamatatrore<span>1 day ago</span></h3>
                     <p>lreomet etoo etojkoet oetojeot</p>
                     <div className='comment-action'>
                        <img src={assets.like}/>
                        <span>244</span>
                        <img src={assets.dislike}/>
                        <span>2000</span>
                     </div>
                </div>
            </div>
            <div className='comment'>
                <img src={assets.user_profile}/>
                <div>
                     <h3>Maria Adamatatrore<span>1 day ago</span></h3>
                     <p>lreomet etoo etojkoet oetojeot</p>
                     <div className='comment-action'>
                        <img src={assets.like}/>
                        <span>244</span>
                        <img src={assets.dislike}/>
                        <span>2000</span>
                     </div>
                </div>
            </div>

        </div>
    </div>
  )
}

export default Playvideo