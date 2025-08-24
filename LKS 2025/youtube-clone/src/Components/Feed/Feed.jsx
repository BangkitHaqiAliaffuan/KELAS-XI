import React from 'react'
import './Feed.css'
import assets from '../../assets'
import { Link } from 'react-router-dom';


const Feed = () => {
    // Sample data for YouTube videos
    const videoData = [
        {
            thumbnail: assets.thumbnail1,
            title: 'Belajar React dari Nol',
            creator: 'Programmer Indonesia',
            info: '15k Views • 2 days ago',
        },
        {
            thumbnail: assets.thumbnail2,
            title: 'Tutorial Vite JS',
            creator: 'CodeWithAdit',
            info: '10k Views • 1 day ago',
        },
        {
            thumbnail: assets.thumbnail3,
            title: 'Mengenal CSS Grid',
            creator: 'WebDev ID',
            info: '8k Views • 3 days ago',
        },
        {
            thumbnail: assets.thumbnail4,
            title: 'Tips UI/UX Design',
            creator: 'DesignPro',
            info: '20k Views • 5 days ago',
        },
        {
            thumbnail: assets.thumbnail5,
            title: 'JavaScript Async Await',
            creator: 'JS Mastery',
            info: '12k Views • 4 days ago',
        },
        {
            thumbnail: assets.thumbnail6,
            title: 'Membuat Website Portfolio',
            creator: 'PortofolioKu',
            info: '7k Views • 6 days ago',
        },
        {
            thumbnail: assets.thumbnail7,
            title: 'React Props & State',
            creator: 'React School',
            info: '18k Views • 2 days ago',
        },
        {
            thumbnail: assets.thumbnail8,
            title: 'Deploy ke Netlify',
            creator: 'DeployMania',
            info: '5k Views • 1 day ago',
        },
        {
            thumbnail: assets.thumbnail1,
            title: 'Belajar React dari Nol',
               creator: 'Programmer Indonesia',
            info: '15k Views • 2 days ago',
        },
        {
            thumbnail: assets.thumbnail2,
            title: 'Tutorial Vite JS',
            creator: 'CodeWithAdit',
            info: '10k Views • 1 day ago',
        },
        {
            thumbnail: assets.thumbnail3,
            title: 'Mengenal CSS Grid',
            creator: 'WebDev ID',
            info: '8k Views • 3 days ago',
        },
        {
            thumbnail: assets.thumbnail4,
            title: 'Tips UI/UX Design',
            creator: 'DesignPro',
            info: '20k Views • 5 days ago',
        },
        {
            thumbnail: assets.thumbnail5,
            title: 'JavaScript Async Await',
            creator: 'JS Mastery',
            info: '12k Views • 4 days ago',
        },
        {
            thumbnail: assets.thumbnail6,
            title: 'Membuat Website Portfolio',
            creator: 'PortofolioKu',
            info: '7k Views • 6 days ago',
        },
        {
            thumbnail: assets.thumbnail7,
            title: 'React Props & State',
            creator: 'React School',
            info: '18k Views • 2 days ago',
        },
        {
            thumbnail: assets.thumbnail8,
            title: 'Deploy ke Netlify',
            creator: 'DeployMania',
            info: '5k Views • 1 day ago',
        },
    ];

    return (
        <Link to={`video/20/4324`} className='feed'>
            {videoData.map((video, idx) => (
                <div className='card' key={idx}>
                    <img src={video.thumbnail} alt={video.title} />
                    <h2>{video.title}</h2>
                    <h3>{video.creator}</h3>
                    <p>{video.info}</p>
                </div>
            ))}
        </Link>
    );
}

export default Feed