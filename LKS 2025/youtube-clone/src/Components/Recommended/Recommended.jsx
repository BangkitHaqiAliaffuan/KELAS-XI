import React from 'react'
import './Recommended.css'
import assets from '../../assets'
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

const Recommended = () => {
    return (
        <div className='recommended'>
            {thumbnails.map((thumb, idx) => {
                const title = titles[idx % titles.length];
                const creator = creators[getRandomInt(0, creators.length - 1)];
                const views = getRandomInt(1, 999) + 'k Views';
                return (
                    <div className='side-video-list' key={idx}>
                        <img src={thumb} />
                        <div className='vid-info'>
                            <h4>{title}</h4>
                            <p>{creator}</p>
                            <p>{views}</p>
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

export default Recommended