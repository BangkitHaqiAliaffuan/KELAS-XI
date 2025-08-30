import React, { useEffect, useState } from 'react'
import './Recommended.css'
import assets from '../../assets'
import { API_KEY, value_converter } from '../../data';
import { Link } from 'react-router-dom';
const titles = [
    'Belajar React',
    'Tutorial Vite',
    'CSS Grid Mudah',
    'UI/UX Design',
    'Async Await JS',
    'Website Portfolio',
    'React Props & State',
    'Deploy ke Netlify',
    'Tips Coding',
    'Mengenal Hooks',
    'Responsive Layout',
    'Dark Mode CSS',
];
const creators = [
    'Programmer Indonesia',
    'CodeWithAdit',
    'WebDev ID',
    'DesignPro',
    'JS Mastery',
    'PortofolioKu',
    'React School',
    'DeployMania',
    'DevTips',
    'HookMaster',
    'LayoutPro',
    'CSS Ninja',
];
const thumbnails = [
    assets.thumbnail1,
    assets.thumbnail2,
    assets.thumbnail3,
    assets.thumbnail4,
    assets.thumbnail5,
    assets.thumbnail6,
    assets.thumbnail7,
    assets.thumbnail8,
    assets.thumbnail2,
    assets.thumbnail4,
    assets.thumbnail1,
    assets.thumbnail1,
];

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

const Recommended = ({categoryId}) => {
    const[apiData,setApiData] = useState([])
    const fetchData = async ()=>{
        const relatedVideo_url = `https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&chart=mostPopular&maxResults=45&regionCode=US&videoCategoryId=${categoryId}&key=${API_KEY}`

        await fetch(relatedVideo_url).then(res=>res.json()).then(data=>setApiData(data.items))
    }

    useEffect(()=>{
        fetchData()
    },[])

    console.log(apiData)

    return (
        <div className='recommended'>
            {apiData.map((item, idx) => {
                return (
                    <Link to={`/video/${item.snippet.categoryId}/${item.id}`} key={idx} className='side-video-list'>
                        <img src={item?item.snippet.thumbnails.medium.url:''} />
                        <div className='vid-info'>
                            <h4>{item?item.snippet.title:''}</h4>
                            <p>{item?item.snippet.channelTitle:''}</p>
                            <p>{item?value_converter(item.statistics.viewCount):''}</p>
                        </div>
                    </Link>
                );
            })}
        </div>
    );
}

export default Recommended