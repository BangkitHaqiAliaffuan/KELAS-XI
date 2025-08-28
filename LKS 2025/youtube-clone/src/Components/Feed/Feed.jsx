import React, { useEffect, useState } from "react";
import "./Feed.css";
import assets from "../../assets";
import { Link } from "react-router-dom";
import { API_KEY, value_converter } from "../../data";
import moment from "moment"
const Feed = ({category}) => {
  // Sample data for YouTube videos

  const [data, setData] = useState([]);

  
  
  const fetchData = async () => {
    const videoList_url = `https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&chart=mostPopular&maxResults=50&regionCode=US&videoCategoryId=${category}&key=${API_KEY}`;

    await fetch(videoList_url).then((response) => response.json()).then((data) => setData(data.items)).catch(error=>console.log(error));
  };

  
  useEffect(() => {
      fetchData();
    }, [category]);
  return (
    <div className="feed">
      {data.map((video, idx) => {
        return (
          <Link key={idx} to={`video/${video.snippet.categoryId}/${video.id}`} className="feed">
            <div className="card" >
              <img src={video.snippet.thumbnails.medium.url} alt={video.title} />
              <h2>{video.snippet.title}</h2>
              <h3>{video.snippet.channelTitle}</h3>
              <p>{value_converter(video.statistics.viewCount)} Views &bull; {moment(video.snippet.publishedAt).fromNow()}</p>
            </div>
          </Link>
        );
      })}
    </div>
  );
};

export default Feed;
