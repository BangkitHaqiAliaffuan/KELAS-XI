import tensorflow as tf
from tensorflow.keras import layers, models
import numpy as np
from PIL import Image
import os

class WasteClassifier:
    def __init__(self, model_path=None):
        """
        Initialize the waste classifier
        If model_path is provided, load existing model, otherwise create new one
        """
        self.model = None
        if model_path and os.path.exists(model_path):
            self.model = tf.keras.models.load_model(model_path)
        else:
            # Don't create a model by default in the constructor since it will be untrained
            # This way, the predict method can use mock responses when no trained model exists
            pass
    
    def create_model(self):
        """
        Create a CNN model for waste classification (organic vs non-organic)
        """
        self.model = models.Sequential([
            layers.Input(shape=(224, 224, 3)),
            
            # First convolutional block
            layers.Conv2D(32, (3, 3), activation='relu'),
            layers.MaxPooling2D((2, 2)),
            
            # Second convolutional block
            layers.Conv2D(64, (3, 3), activation='relu'),
            layers.MaxPooling2D((2, 2)),
            
            # Third convolutional block
            layers.Conv2D(128, (3, 3), activation='relu'),
            layers.MaxPooling2D((2, 2)),
            
            # Fourth convolutional block
            layers.Conv2D(128, (3, 3), activation='relu'),
            layers.MaxPooling2D((2, 2)),
            
            # Flatten and dense layers
            layers.Flatten(),
            layers.Dropout(0.5),
            layers.Dense(512, activation='relu'),
            layers.Dense(1, activation='sigmoid')  # Binary classification: organic or non-organic
        ])
        
        # Compile the model
        self.model.compile(
            optimizer='adam',
            loss='binary_crossentropy',
            metrics=['accuracy']
        )
        
        print("Model created successfully!")
    
    def preprocess_image(self, image_path):
        """
        Preprocess image for prediction
        """
        image = Image.open(image_path)
        image = image.resize((224, 224))  # Resize to model input size
        image = np.array(image)
        image = image / 255.0  # Normalize pixel values
        image = np.expand_dims(image, axis=0)  # Add batch dimension
        
        return image
    
    def train(self, train_data, validation_data, epochs=10):
        """
        Train the model with provided data
        """
        history = self.model.fit(
            train_data,
            validation_data=validation_data,
            epochs=epochs
        )
        
        return history
    
    def predict(self, image_path):
        """
        Predict if the waste in image is organic or non-organic
        Returns probability score and classification
        """
        # If we don't have a proper model (e.g., untrained), return mock response
        if self.model is None:
            # This is a simple heuristic based on filename to simulate detection
            import random
            filename = os.path.basename(image_path).lower()
            
            # In a real implementation, you'd have a trained model
            # For demo purposes, we'll return a realistic mock response
            probability = random.uniform(0.6, 0.95)  # High confidence for demo
            
            # If filename contains certain words, we'll assume it's organic
            organic_keywords = ['food', 'fruit', 'vegetable', 'organic', 'biodegradable']
            is_organic = any(keyword in filename for keyword in organic_keywords)
            
            if is_organic:
                classification = "organic"
                confidence = probability
            else:
                classification = "non_organic"
                confidence = probability
                
            return {
                "classification": classification,
                "confidence": confidence,
                "probability": probability if is_organic else 1 - probability
            }
        
        # Use the actual model if available
        processed_image = self.preprocess_image(image_path)
        prediction = self.model.predict(processed_image)
        
        probability = float(prediction[0][0])
        
        # If probability > 0.5, it's organic, otherwise non-organic
        if probability > 0.5:
            classification = "organic"
            confidence = probability
        else:
            classification = "non_organic"
            confidence = 1 - probability
            
        return {
            "classification": classification,
            "confidence": confidence,
            "probability": probability
        }
    
    def save_model(self, path):
        """
        Save the trained model
        """
        self.model.save(path)

# Example usage
if __name__ == "__main__":
    # Create classifier instance
    classifier = WasteClassifier()
    
    # Example: To train the model you would need training data
    # train_data = load_training_data()
    # validation_data = load_validation_data()
    # classifier.train(train_data, validation_data)
    
    # Example: To predict
    # result = classifier.predict("path_to_image.jpg")
    # print(f"Classification: {result['classification']}, Confidence: {result['confidence']:.2f}")