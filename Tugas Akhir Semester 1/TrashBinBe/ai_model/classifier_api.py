import sys
import json
import os
from waste_classifier import WasteClassifier

def main():
    if len(sys.argv) != 2:
        print(json.dumps({"error": "Usage: python classifier_api.py <image_path>"}))
        return

    image_path = sys.argv[1]
    
    # Check if file exists
    if not os.path.exists(image_path):
        print(json.dumps({"error": "Image file does not exist"}))
        return

    try:
        # Load pre-trained model (in production, you would load a trained model)
        model_path = os.path.join(os.path.dirname(__file__), 'waste_model.h5')
        if os.path.exists(model_path):
            classifier = WasteClassifier(model_path=model_path)
        else:
            # Use untrained model which has mock functionality for demo purposes
            classifier = WasteClassifier()

        # Perform prediction
        result = classifier.predict(image_path)
        
        # Output result as JSON
        print(json.dumps(result))
        
    except ImportError as e:
        # Handle import errors (e.g., TensorFlow not installed)
        print(json.dumps({"error": f"Missing dependencies: {str(e)}"}), file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(json.dumps({"error": f"Prediction failed: {str(e)}"}), file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()