import React, { useEffect, useRef, useState } from "react";
import { assets } from "./assets/assets";
import "./App.css";
import { data } from "react-router-dom";
const App = () => {
  const [data, setData] = useState([]);
  const [isTick, isSetTick] = useState(false);

  const inputRef = useRef(null);

  const handleTick = () => {
    isTick ? isSetTick(false) : isSetTick(true);
  };

  const handleSend = () => {
    const value = inputRef.current.value;

    if (value.trim() !== "") {
      setData([...data, value]);
      inputRef.current.value = "";
      console.log(value);
    }
  };

  useEffect(() => {
    console.log(data);
  }, [data]);

  const handleDelete = (indexDelete)=>{
    setData(data.filter((_,index)=>index !== indexDelete))
  }

  return (
    <div className="main-container">
      <div className="logo">
        <h2>To-Do-List</h2>
      </div>

      <div className="input-container">
        <input ref={inputRef} type="text" placeholder="Add Your Text" />
        <button onClick={handleSend} className="add-button">
          ADD
        </button>
      </div>

      <div className="todo-container">
        {data.map((data, index) => {
          return (
            <div key={index} className="todo">
              <img
                src={!isTick ? assets.not_tick : assets.tick}
                onClick={handleTick}
              />

              <div className="todo-title">
                {data}
                <img style={{ cursor:"pointer" }} onClick={()=>{handleDelete(index)}} src={assets.cross} />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default App;
