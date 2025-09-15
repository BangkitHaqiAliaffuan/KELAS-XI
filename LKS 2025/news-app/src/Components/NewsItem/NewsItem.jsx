import React from "react";
import "./NewsItem.css";

import newsJpg from '../../assets/news.jpeg'
const NewsItem = ({ title, urlImage, description }) => {
  return (
    <div class="col">
      <div class="card h-100 d-inline-block">
        <img src={!urlImage ? newsJpg : urlImage} class="card-img-top h-50" alt="..." />
        <div class="card-body h-50 mt-2">
          <h5 class="card-title h-25">{title.slice(0,50)}</h5>
          <p class="card-text h-25">
            {description?description.slice(0,70):"Description Not Found"}
          </p>
          <a href="#" class="btn btn-primary ">Read More</a>
        </div>
      </div>
    </div>
  );
};

export default NewsItem;
