#!/bin/bash
# Setup script for the AI waste classification model

echo "Setting up AI waste classification environment..."

# Create virtual environment if it doesn't exist
if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    python -m venv venv
fi

# Activate virtual environment
source venv/bin/activate  # On Windows: venv\Scripts\activate

echo "Installing Python dependencies..."
pip install -r requirements.txt

echo "AI waste classification environment setup complete!"
echo "To train the model, run: python train_model.py"
echo "Note: Training requires a dataset organized in the following structure:"
echo "  dataset/"
echo "    train/"
echo "      organic/"
echo "      non_organic/"
echo "    validation/"
echo "      organic/"
echo "      non_organic/"