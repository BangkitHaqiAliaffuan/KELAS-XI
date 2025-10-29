import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import os

def create_model():
    """
    Create a CNN model for waste classification (organic vs non-organic)
    """
    model = models.Sequential([
        layers.Input(shape=(224, 224, 3)),
        
        # First convolutional block
        layers.Conv2D(32, (3, 3), activation='relu'),
        layers.BatchNormalization(),
        layers.MaxPooling2D((2, 2)),
        
        # Second convolutional block
        layers.Conv2D(64, (3, 3), activation='relu'),
        layers.BatchNormalization(),
        layers.MaxPooling2D((2, 2)),
        
        # Third convolutional block
        layers.Conv2D(128, (3, 3), activation='relu'),
        layers.BatchNormalization(),
        layers.MaxPooling2D((2, 2)),
        
        # Fourth convolutional block
        layers.Conv2D(128, (3, 3), activation='relu'),
        layers.BatchNormalization(),
        layers.MaxPooling2D((2, 2)),
        
        # Flatten and dense layers
        layers.Flatten(),
        layers.Dropout(0.5),
        layers.Dense(512, activation='relu'),
        layers.Dense(1, activation='sigmoid')  # Binary classification: organic or non-organic
    ])
    
    # Compile the model
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
        loss='binary_crossentropy',
        metrics=['accuracy', 'precision', 'recall']
    )
    
    return model

def train_model():
    """
    Train the waste classification model
    Note: This function assumes you have training data organized in the following structure:
    - dataset/
        - train/
            - organic/
            - non_organic/
        - validation/
            - organic/
            - non_organic/
    """
    # Define paths to your training and validation datasets
    train_dir = os.path.join(os.path.dirname(__file__), 'dataset', 'train')
    validation_dir = os.path.join(os.path.dirname(__file__), 'dataset', 'validation')
    
    # Check if dataset exists
    if not os.path.exists(train_dir) or not os.path.exists(validation_dir):
        print("Training and validation datasets not found. Please organize your data in the following structure:")
        print("- ai_model/dataset/train/organic/")
        print("- ai_model/dataset/train/non_organic/")
        print("- ai_model/dataset/validation/organic/")
        print("- ai_model/dataset/validation/non_organic/")
        return

    # Image data generators with augmentation
    train_datagen = ImageDataGenerator(
        rescale=1./255,
        rotation_range=20,
        width_shift_range=0.2,
        height_shift_range=0.2,
        horizontal_flip=True,
        zoom_range=0.2,
        shear_range=0.2,
        fill_mode='nearest'
    )

    validation_datagen = ImageDataGenerator(rescale=1./255)

    # Create data generators
    train_generator = train_datagen.flow_from_directory(
        train_dir,
        target_size=(224, 224),
        batch_size=32,
        class_mode='binary'  # Binary classification
    )

    validation_generator = validation_datagen.flow_from_directory(
        validation_dir,
        target_size=(224, 224),
        batch_size=32,
        class_mode='binary'
    )

    # Create model
    model = create_model()
    
    # Print model summary
    model.summary()

    # Train the model
    history = model.fit(
        train_generator,
        epochs=20,  # Adjust as needed
        validation_data=validation_generator,
        callbacks=[
            tf.keras.callbacks.EarlyStopping(patience=5, restore_best_weights=True),
            tf.keras.callbacks.ReduceLROnPlateau(factor=0.2, patience=3)
        ]
    )

    # Save the trained model
    model_path = os.path.join(os.path.dirname(__file__), 'waste_model.h5')
    model.save(model_path)
    print(f"Model saved to {model_path}")

    return model, history

if __name__ == "__main__":
    trained_model, training_history = train_model()