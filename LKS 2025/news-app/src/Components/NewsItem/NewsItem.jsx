import React from "react";
import "./NewsItem.css";

import newsJpg from '../../assets/news.jpeg'
const NewsItem = ({ title, urlImage }) => {
  return (
    <div class="col">
      <div class="card h-100 d-inline-block">
        <img src={!urlImage ? newsJpg : urlImage} class="card-img-top h-50" alt="..." />
        <div class="card-body h-50">
          <h5 class="card-title h-25">{title.slice(0,50)}</h5>
          <p class="card-text h-">
            This is a longer card with supporting text below as a natural
            lead-in to additional content. This content is a little bit longer.
          </p>
          <a href="#" class="btn btn-primary ">Read More</a>
        </div>
      </div>
    </div>
  );
};

export default NewsItem;
