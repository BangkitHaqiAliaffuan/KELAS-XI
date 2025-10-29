# AI Waste Classification Feature

This feature allows users to verify if their waste matches the selected category by using AI-powered image recognition.

## Backend Setup (Laravel)

### 1. Python Environment Setup
1. Navigate to the `ai_model` directory:
   ```bash
   cd ai_model
   ```

2. Run the setup script for your operating system:
   - Windows: `setup.bat`
   - Linux/Mac: `chmod +x setup.sh && ./setup.sh`

3. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```

### 2. Train the Model (Optional but Recommended)
To train the model with your own dataset:

1. Organize your dataset in the following structure:
   ```
   ai_model/dataset/
   ├── train/
   │   ├── organic/
   │   └── non_organic/
   └── validation/
       ├── organic/
       └── non_organic/
   ```

2. Run the training script:
   ```bash
   python train_model.py
   ```

### 3. API Endpoint
The backend provides an endpoint at `/api/waste/classify` that accepts image uploads and returns classification results.

## Frontend Setup (Android)

### 1. API Integration
The Android app integrates with the backend via the `classifyWaste` method in the API service. The `AICameraActivity` handles image capture and sends the image to the backend for processing.

### 2. Feature Usage
1. In the Pickup Request screen, users can select a waste category
2. Press the "Verifikasi" button to launch the AI camera
3. Take a photo of the waste
4. The backend processes the image and returns the classification result
5. The app displays a success/failure message based on whether the detected waste matches the selected category

## How It Works

1. User selects a waste category and clicks "Verifikasi"
2. AICameraActivity opens with camera functionality
3. User takes a photo of their waste
4. The image is sent to the backend via the `/api/waste/classify` endpoint
5. The backend processes the image using a TensorFlow model
6. Classification results (organic/non-organic) are returned to the frontend
7. The app compares the AI classification with the user's selected category
8. Appropriate feedback is shown to the user

## Model Architecture

The AI model uses a Convolutional Neural Network (CNN) with the following layers:
- Input layer (224x224x3)
- Multiple Conv2D and MaxPooling2D layers
- Flatten layer
- Dense layers with dropout for regularization
- Output layer with sigmoid activation for binary classification

## Testing the Feature

1. Start the Laravel backend server
2. Install and run the Android app
3. Navigate to the pickup request screen
4. Select a waste category
5. Click "Verifikasi" and take a photo
6. Observe the classification result and verification status

## Notes

- The model works best with clear, well-lit images of waste items
- For demo purposes, if no trained model exists, the system uses heuristic-based detection
- In production, ensure the trained model file (`waste_model.h5`) is present in the `ai_model` directory
- The model should be trained with a diverse dataset for better accuracy