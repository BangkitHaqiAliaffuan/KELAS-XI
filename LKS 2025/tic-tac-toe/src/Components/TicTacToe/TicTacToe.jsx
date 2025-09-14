import React, { useReducer, useRef, useState } from "react";
import "./TicTacToe.css";
import crossIcon from "../Assets/cross.png";
import circleIcon from "../Assets/circle.png";
let data = ["", "", "", "", "", "", "", "", ""];

const TicTacToe = () => {
  let [count, setCount] = useState(0);
  let [lockData, setLockData] = useState(false);
  let titleRef = useRef(null);

  let box1 = useRef(null);
  let box2 = useRef(null);
  let box3 = useRef(null);
  let box4 = useRef(null);
  let box5 = useRef(null);
  let box6 = useRef(null);
  let box7 = useRef(null);
  let box8 = useRef(null);
  let box9 = useRef(null);

  let box_array = [box1, box2, box3, box4, box5, box6, box7, box8, box9];

  const toggle = (e, num) => {
    if (lockData) {
      return 0;
    }

    if (count % 2 == 0) {
      e.target.innerHTML = `<img src="${crossIcon}"/>`;
      data[num] = "x";
      setCount(++count);
    } else {
      e.target.innerHTML = `<img src="${circleIcon}"/>`;
      data[num] = "o";
      setCount(++count);
    }
    checkWin();
  };

  const checkWin = (board) => {
    const winPatterns = [
      [0, 1, 2],
      [3, 4, 5],
      [6, 7, 8], // rows
      [0, 3, 6],
      [1, 4, 7],
      [2, 5, 8], // columns
      [0, 4, 8],
      [2, 4, 6], // diagonals
    ];
    return winPatterns.some(
      (pattern) =>
        board[pattern[0]] &&
        board[pattern[0]] === board[pattern[1]] &&
        board[pattern[1]] === board[pattern[2]]
    );
  };

  const reset = () => {
    setLockData(false);
    data = ["", "", "", "", "", "", "", "", ""];
    titleRef.current.innerHTML = "Tic Tac Toe <span>React</span>";

    box_array.map((e) => {
      e.current.innerHTML = "";
    });
  };

  const won = (winner) => {
    setLockData(true);
    console.log(winner);
    if (winner === "x") {
      titleRef.current.innerHTML = `Congratulations: <img src="${crossIcon}"/> Win`;
    } else {
      titleRef.current.innerHTML = `Congratulations: <img src="${circleIcon}"/> Win`;
    }
  };

  return (
    <div className="container">
      <h1 className="title" ref={titleRef}>
        Tic Tac Toe <span>React</span>
      </h1>
      {/* <img src={crossIcon}/> */}
      <div className="board">
        <div className="row1">
          <div
            className="box"
            ref={box1}
            onClick={(e) => {
              toggle(e, 0);
            }}
          >
            1
          </div>
          <div
            className="box"
            ref={box2}
            onClick={(e) => {
              toggle(e, 1);
            }}
          ></div>
          <div
            className="box"
            ref={box3}
            onClick={(e) => {
              toggle(e, 2);
            }}
          ></div>
        </div>
        <div className="row2">
          <div
            className="box"
            ref={box4}
            onClick={(e) => {
              toggle(e, 3);
            }}
          >
            2
          </div>
          <div
            className="box"
            ref={box5}
            onClick={(e) => {
              toggle(e, 4);
            }}
          ></div>
          <div
            className="box"
            ref={box6}
            onClick={(e) => {
              toggle(e, 5);
            }}
          ></div>
        </div>
        <div className="row3">
          <div
            className="box"
            ref={box7}
            onClick={(e) => {
              toggle(e, 6);
            }}
          >
            3
          </div>
          <div
            className="box"
            ref={box8}
            onClick={(e) => {
              toggle(e, 7);
            }}
          ></div>
          <div
            className="box"
            ref={box9}
            onClick={(e) => {
              toggle(e, 8);
            }}
          ></div>
        </div>
      </div>
      <button
        className="reset"
        onClick={() => {
          reset();
        }}
      >
        reset
      </button>
    </div>
  );
};

export default TicTacToe;
