import React, { useEffect, useState } from "react";
import "./PlayVideo.css";
import assets from "../../assets";
import { API_KEY, value_converter } from "../../data";
import moment from "moment";
import { data, useParams } from "react-router-dom";
const Playvideo = () => {
  const [apiData, setApiData] = useState(null);
  const [channelData, setChannelData] = useState(null);
  const [commentData, setCommentData] = useState([]);

  const {videoId} = useParams()

  const fetchVideoData = async () => {
    // fetching video data
    const videoDetails_url = `https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id=${videoId}&key=${API_KEY}`;

    await fetch(videoDetails_url)
      .then((response) => response.json())
      .then((data) => setApiData(data.items[0]));
  };

  const fetchOtherData = async () => {

    const channelData_url = `https://youtube.googleapis.com/youtube/v3/channels?part=snippet%2CcontentDetails%2Cstatistics&id=${apiData.snippet.channelId}&key=${API_KEY}`;

    await fetch(channelData_url)
      .then((response) => response.json())
      .then((data) => setChannelData(data.items[0]));

    const commentData_url = `https://youtube.googleapis.com/youtube/v3/commentThreads?part=snippet%2Creplies&maxResults=50&videoId=${videoId}&key=${API_KEY}`;

    await fetch(commentData_url)
      .then((res) => res.json())
      .then((data) => setCommentData(data.items));
  };

  useEffect(() => {
    fetchVideoData();
  }, [videoId]);

  useEffect(() => {
    fetchOtherData();
  }, [apiData]);

//   console.log(commentData);

  return (
    <div className="play-video">
      {/* {<video src={assets.video} controls autoPlay muted></video>} */}

      <iframe
        width="676"
        height="380"
        src={`https://www.youtube.com/embed/${videoId}?autoplay=1`}
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
        referrerpolicy="strict-origin-when-cross-origin"
        allowfullscreen
      ></iframe>
      <h3>{apiData ? apiData.snippet.title : "Title Here"}</h3>
      <div className="play-video-info">
        <p>
          {apiData ? value_converter(apiData.statistics.viewCount) : "16k"}{" "}
          Views &bull;{" "}
          {apiData
            ? moment(apiData.snippet.publishedAt).fromNow()
            : "2 Days Ago"}{" "}
        </p>
        <div>
          <span>
            <img src={assets.like} />
            {apiData
              ? value_converter(apiData.statistics.likeCount)
              : "Not Found"}
          </span>
          <span>
            <img src={assets.dislike} />
            {apiData
              ? value_converter(apiData.statistics.dislikeCount)
              : "Not Found"}
          </span>
          <span>
            <img src={assets.share} />
            Share
          </span>
          <span>
            <img src={assets.save} />
            Save
          </span>
        </div>
      </div>
      <hr />
      <div className="publisher">
        <img
          src={
            channelData
              ? channelData.snippet.thumbnails.default.url
              : "notfound"
          }
        />
        <div>
          <p>{apiData ? apiData.snippet.channelTitle : "Not Found"}</p>
          <span>
            {channelData
              ? value_converter(channelData.statistics.subscriberCount)
              : "Not Found"}{" "}
            Subscribers
          </span>
        </div>
        <button>Subscribe</button>
      </div>
      <div className="vid-description">
        <p>{apiData ? apiData.snippet.description.slice(0, 250) : "tes"}</p>

        <hr />
        <h4>
          {apiData
            ? value_converter(apiData.statistics.commentCount)
            : "comment not found"}{" "}
          Comments
        </h4>
        {commentData.map((item,index)=>{
            return (
            <div key={index} className="comment">
          <img src={item?item.snippet.topLevelComment.snippet.authorProfileImageUrl:''} />
          <div>
            <h3>
              {item?item.snippet.topLevelComment.snippet.authorDisplayName.replace("@",''):"tes"}<span>{item?moment(item.snippet.topLevelComment.snippet.publishedAt).fromNow():''}</span>
            </h3>
            <p>{item?item.snippet.topLevelComment.snippet.textDisplay:''}</p>
            <div className="comment-action">
              <img src={assets.like} />
              <span>{item?value_converter(item.snippet.topLevelComment.snippet.likeCount):''}</span>
              <img src={assets.dislike} />
              <span>2000</span>
            </div>
          </div>
        </div>
            )
        })}
        
      
      </div>
    </div>
  );
};

export default Playvideo;
