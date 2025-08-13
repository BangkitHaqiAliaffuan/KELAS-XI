import { GoogleGenerativeAI } from "@google/generative-ai";

// Fetch API key from environment variables
const API_KEY = import.meta.env.VITE_GEMINI_API_KEY;

// Initialize the GoogleGenerativeAI model with the API key
const genAI = new GoogleGenerativeAI(API_KEY);

// Function to run the chat with a given prompt
const runChat = async (prompt) => {
    try {
        // Validate API key
        if (!API_KEY) {
            throw new Error("API key is missing. Please check your .env file.");
        }

        // Validate prompt
        if (!prompt || prompt.trim() === "") {
            throw new Error("Prompt is required and cannot be empty.");
        }

        console.log("API Key status:", API_KEY ? "✓ Present" : "✗ Missing");
        console.log("Prompt to send:", prompt);

        const model = genAI.getGenerativeModel({ 
            model: "gemini-1.5-flash",
            // Add generation config to handle potential issues
            generationConfig: {
                temperature: 0.7,
                topP: 1,
                maxOutputTokens: 2048,
            }
        });

        // Add timeout wrapper
        const timeoutPromise = new Promise((_, reject) => {
            setTimeout(() => reject(new Error('Request timeout')), 30000); // 30 second timeout
        });

        const generatePromise = model.generateContent(prompt);
        
        const result = await Promise.race([generatePromise, timeoutPromise]);
        const response = await result.response;
        const text = response.text();
        
        console.log("✓ Response received successfully");
        return text;
    } catch (error) {
        console.error("Error generating content:", error);
        
        // More specific error messages
        if (error.message.includes('Failed to fetch')) {
            return "Network error: Please check your internet connection and try again.";
        } else if (error.message.includes('quota')) {
            return "API quota exceeded. Please try again later or use a different API key.";
        } else if (error.message.includes('timeout')) {
            return "Request timed out. Please try again.";
        }
        
        return "Sorry, something went wrong while generating the response.";
    }
}


export default runChat;

