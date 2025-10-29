@echo off
REM Setup script for the AI waste classification model on Windows

echo Setting up AI waste classification environment...

REM Create virtual environment if it doesn't exist
if not exist "venv" (
    echo Creating virtual environment...
    python -m venv venv
)

REM Activate virtual environment
call venv\Scripts\activate.bat

echo Installing Python dependencies...
pip install -r requirements.txt

echo AI waste classification environment setup complete!
echo To train the model, run: python train_model.py
echo Note: Training requires a dataset organized in the following structure:
echo   dataset/
echo     train/
echo       organic/
echo       non_organic/
echo     validation/
echo       organic/
echo       non_organic/
pause