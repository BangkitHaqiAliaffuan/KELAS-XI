import React, { useEffect, useState } from "react";
import "./NewsBoard.css";
import NewsItem from "../NewsItem/NewsItem";
import { API_KEY } from "../../../data";

const NewsBoard = ({category}) => {
  const [data, setData] = useState([]);
  const [hasData, setHasData] = useState(false);
  const [loading, setLoading] = useState(true);




  const fetchData = async () => {
    try {
      setLoading(true);
      let url = `https://newsapi.org/v2/top-headlines?country=us&category=${category}&apiKey=${API_KEY}`;
      const response = await fetch(url);
      const result = await response.json();
      
      if (result.articles) {
        setData(result.articles);
        setHasData(true);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [category]); // Empty dependency array memastikan hanya dipanggil sekali

  // Log data setelah state update
  useEffect(() => {
    console.log(data);
  }, [data]);

  if (loading) {
    return <div className="text-center">Loading...</div>;
  }

  // console.log(category);

  return (
    <div>
      <div className="title fs-1 text-center mb-5 mt-5">
        Top <span className="text-danger">Headlines</span>
      </div>

      <div className="row row-cols-1 row-cols-md-4 g-4 p-5">
        {hasData && data.length > 0 ? (
          data.map((article, index) => (
            <NewsItem key={index} description={article.description} title={article.title} urlImage={article.urlToImage} />
          ))
        ) : (
          <div className="text-center">No news available</div>
        )}
      </div>
    </div>
  );
};

export default NewsBoard;