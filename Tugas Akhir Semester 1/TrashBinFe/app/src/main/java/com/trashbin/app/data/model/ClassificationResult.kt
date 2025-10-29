package com.trashbin.app.data.model

data class ClassificationResult(
    val classification: String,
    val confidence: Double,
    val probability: Double
)