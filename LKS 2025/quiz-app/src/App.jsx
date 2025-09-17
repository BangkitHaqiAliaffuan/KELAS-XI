import React, { useEffect, useState } from "react";
import "./App.css";
import { data } from "./Assets/data.js";
import { useAsyncError } from "react-router-dom";
// import { data } from "react-router-dom";

const App = () => {
  const [index, setIndex] = useState(0);
  const [question, setQuestion] = useState(data[index]);
  // const [option, setOption] = useState(question.option1)
  // console.log(question);
  useEffect(() => {
    setQuestion(data[index]);
    setSelected(null);
  }, [index]);


  // const [answer, setAnswer] = useState(question.ans);
  const [selected, setSelected] = useState(null);
  
  const [count, setCount] = useState(0);

  const checkAnswer = (ans) => {
    if (selected == null) {
      return setSelected(ans),ans === question.ans?setCount(count+1):""
    }
  };

  const reset = ()=>{
    setIndex(0)
    setCount(0)
    setQuestion(data[0])
  }

  const loopingOption = () => {
    const items = [];

    for (let i = 0; i < 4; i++) {
      let cls = "option";

      if (selected !== null) {
        console.log("terisi");
        if (i + 1 === question.ans) {
          cls = "option right";
          console.log("true");
          
        } else if (i + 1 === selected) {
          cls = "option wrong";
          console.log("false");
        }
      }

      items.push(
        <div key={i} className={cls}>
          <div
            onClick={(e) => {
              checkAnswer(i + 1);
            }}
          >
            {question[`option${i + 1}`]}
          </div>
        </div>
      );
    }

    return items;
  };

  return (
    <div className="container">
      <div className="title">
        <h1>Quiz App</h1>
        <div className="break-line"></div>
      </div>

      {index >= 5?
        <>
       <div className="question">
        You Scored {count} out of 5
       </div>

       <div onClick={reset} className="btn reset">
        Reset
       </div>
        
       </>
       :
       <>
        <div className="question">
        {index + 1 + "."} {question.question}
      </div>
      
      <div className="answers">{index >= 5?"":loopingOption()}</div>
      <div className="info-wrapper">
        <div
          onClick={() => {
            setIndex(index + 1);
          }}
          className="btn"
        >
          Next
        </div>
        <div className="status">{index+1} of 5 questions</div>
      </div>
       </>
      }

      


    </div>
  );
};
export default App;
