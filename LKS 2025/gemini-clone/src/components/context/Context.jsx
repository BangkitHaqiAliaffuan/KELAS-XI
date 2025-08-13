import { createContext, useEffect, useState } from "react";
import runChat from "../config/gemini";

export const Context = createContext();

const ContextProvider = (props) => {
  const [input, setInput] = useState("");
  const [recentPrompt, setRecentPrompt] = useState("");
  const [prevPrompts, setPrevPrompts] = useState([]);
  const [showResult, setShowResult] = useState(false);
  const [loading, setLoading] = useState(false);
  const [resultData, setResultData] = useState("");


const delayPara = (index, nextWord) => {
  setTimeout(function(){
    setResultData(prev=>prev+nextWord)
  },75*index)
}

const newChat = ()=>{
  setLoading(false)
  setShowResult(false)
}

  const onSent = async (prompt) => {
    try {
      // Use the passed prompt, or fall back to the input state
      const textToSend = prompt || input;
      
      if (!textToSend || textToSend.trim() === "") {
        console.log("No prompt provided");
        return;
      }

      setResultData("")
      setLoading(true)
      setShowResult(true)
      let response;
      if(prompt !== undefined){
        response = await runChat(prompt)
        setRecentPrompt(prompt)
      }else{
        setPrevPrompts(prev=>[...prev, input])
        setRecentPrompt(input)
        response = await runChat(input)
      }
      setRecentPrompt(textToSend) // Use textToSend instead of input
      setPrevPrompts(prev=>[...prev,input])
      console.log("Sending prompt:", textToSend);
      
      // Add timeout and retry mechanism
      const result = await runChat(textToSend);
      
      if (!result) {
        setResultData("Sorry, no response received from the AI.");
        setLoading(false);
        return;
      }

      // Fix the formatting logic
      let responseArray = result.split("**");
      let newArray = "";

      for(let i = 0; i < responseArray.length; i++){
        if(i === 0 || i%2 !== 1){
          newArray += responseArray[i];
        } else{
          newArray += "<b>"+responseArray[i]+"</b>";
        }
      }

      // Fix line breaks
      let newResponse2 = newArray.split("*").join("<br>");
      
      let newResponseArray = newResponse2.split(" ");
      for(let i = 0; i<newResponseArray.length; i++){
        const nextWord = newResponseArray[i]
        delayPara(i,nextWord+" ")
      } // Use newResponse2, not newArray
      setLoading(false)
      setInput("")
      console.log("Response from AI:", result);
      return result;
    } catch (error) {
      console.error("Error in onSent:", error);
      setLoading(false);
      setResultData("Sorry, an error occurred while processing your request. Please try again.");
    }
  };

  // Test the function (commented out to avoid auto-calling)
  // useEffect(() => {
  //   onSent("what is react js  ");
  // }, []); // Empty dependency array means this runs only once

  const contextValue = {
    onSent,
    prevPrompts,
    setPrevPrompts,
    setRecentPrompt,
    recentPrompt,
    loading,
    setInput,
    setResultData,
    input,
    newChat,
    showResult,
    setShowResult,
    resultData
  };

  return (
    <Context.Provider value={contextValue}>{props.children}</Context.Provider>
  );
};

export default ContextProvider;
