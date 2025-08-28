import React, { useEffect, useState } from 'react'
import './PlayVideo.css'
import assets from '../../assets'
import { API_KEY } from '../../data'

const Playvideo = ({videoId}) => {

    const [apiData, setApiData] = useState(null)

    const fetchVideoData = async ()=>{
        // fetching video data
        const videoDetails_url = `https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id=${videoId}&key=${API_KEY}`

        await fetch(videoDetails_url).then(response=>response.json()).then(data=>setApiData(data.items[0]))

        
    }


    useEffect(()=>{
        fetchVideoData()
    },[])
    console.log(apiData)

  return (
    <div className='play-video'>
        {/* {<video src={assets.video} controls autoPlay muted></video>} */}

        <iframe width="676" height="380" src={`https://www.youtube.com/embed/${videoId}?autoplay=1`} frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
        <h3>{apiData?apiData.snippet.title:"Title Here"}</h3>
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