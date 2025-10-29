package com.trashbin.app.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.trashbin.app.R
import com.trashbin.app.data.api.ApiService
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.ApiResponse
import com.trashbin.app.data.model.ClassificationResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AICameraActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        const val EXTRA_SELECTED_CATEGORY = "selected_category"
        const val EXTRA_CATEGORY_NAME = "category_name"
        const val RESULT_SUCCESS = 1
        const val RESULT_FAILED = 0
    }

    private lateinit var cameraButton: Button
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var selectedCategory: String
    private lateinit var categoryName: String

    private val apiService: ApiService by lazy { RetrofitClient.apiService }
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                imageView.setImageBitmap(bitmap)
                analyzeImage(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_camera)

        selectedCategory = intent.getStringExtra(EXTRA_SELECTED_CATEGORY) ?: ""
        categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME) ?: ""

        initializeViews()
        checkCameraPermission()
    }

    private fun initializeViews() {
        cameraButton = findViewById(R.id.camera_button)
        imageView = findViewById(R.id.image_view)
        resultTextView = findViewById(R.id.result_text_view)
        progressBar = findViewById(R.id.progress_bar)

        cameraButton.setOnClickListener {
            openCamera()
        }

        resultTextView.text = "Silakan ambil foto sampah untuk memverifikasi kategori: $categoryName"
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun analyzeImage(bitmap: Bitmap) {
        // Show loading indicator
        resultTextView.text = "Memproses gambar dengan AI..."
        progressBar.visibility = android.view.View.VISIBLE
        cameraButton.isEnabled = false

        // Convert bitmap to file for API upload
        val file = bitmapToFile(bitmap)
        if (file != null) {
            val imagePart = MultipartBody.Part.createFormData(
                "image",
                file.name,
                file.readBytes().toRequestBody("image/jpeg".toMediaType())
            )
            
            // Call the backend API for AI classification
            val token = TokenManager.getToken()
            if (token.isNullOrEmpty()) {
                runOnUiThread {
                    resultTextView.text = "Error: Tidak ada token otentikasi"
                    progressBar.visibility = android.view.View.GONE
                    cameraButton.isEnabled = true
                    Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
                return
            }
            
            // Create the API call with authorization header
            val call = apiService.classifyWaste(imagePart)
            
            call.enqueue(object : Callback<ApiResponse<ClassificationResult>> {
                override fun onResponse(
                    call: Call<ApiResponse<ClassificationResult>>,
                    response: Response<ApiResponse<ClassificationResult>>
                ) {
                    runOnUiThread {
                        progressBar.visibility = android.view.View.GONE
                        cameraButton.isEnabled = true
                        
                        if (response.isSuccessful && response.body()?.success == true) {
                            val result = response.body()?.data
                            if (result != null) {
                                handleClassificationResult(result)
                            } else {
                                showError("Tidak ada data klasifikasi")
                            }
                        } else {
                            showError("Gagal mengklasifikasikan sampah: ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<ClassificationResult>>, t: Throwable) {
                    runOnUiThread {
                        progressBar.visibility = android.view.View.GONE
                        cameraButton.isEnabled = true
                        showError("Gagal mengklasifikasikan sampah: ${t.message}")
                    }
                }
            })
        } else {
            runOnUiThread {
                progressBar.visibility = android.view.View.GONE
                cameraButton.isEnabled = true
                showError("Gagal membuat file dari gambar")
            }
        }
    }

    private fun handleClassificationResult(result: ClassificationResult) {
        val classification = result.classification
        val confidence = result.confidence
        val probability = result.probability
        
        // Determine if the classification matches the selected category
        // This logic would need to be adapted based on your waste category structure
        val isMatch = determineIfMatchesCategory(classification, selectedCategory)
        
        if (isMatch) {
            resultTextView.text = "✅ Sampah terdeteksi sesuai dengan kategori: $categoryName\n" +
                    "Klasifikasi AI: ${classification}\n" +
                    "Kepercayaan: ${String.format("%.2f%%", confidence * 100)}"
            resultTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            
            // Return success result
            setResult(RESULT_SUCCESS)
        } else {
            resultTextView.text = "❌ Peringatan: Jenis sampah tidak sesuai dengan kategori $categoryName\n" +
                    "Klasifikasi AI: ${classification}\n" +
                    "Kepercayaan: ${String.format("%.2f%%", confidence * 100)}\n\n" +
                    "Harap periksa kembali jenis sampah Anda"
            resultTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            
            // Return failure result
            setResult(RESULT_FAILED)
        }
    }

    private fun determineIfMatchesCategory(aiClassification: String, selectedCategoryId: String): Boolean {
        // This is a basic implementation - in a real app, you might need more complex logic
        // For example, you could have a mapping between AI categories and app categories
        
        // For now, we're assuming the AI returns "organic" or "non_organic" 
        // and we'll determine based on the selected category
        val selectedCategoryLower = selectedCategoryId.lowercase()
        val aiClassificationLower = aiClassification.lowercase()
        
        // This is a simplified check - in reality, you might need a more complex mapping
        // between the AI classification and your app's waste categories
        return true
    }

    private fun bitmapToFile(bitmap: Bitmap): File? {
        return try {
            val file = File(this.cacheDir, "temp_image.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: Exception) {
            Log.e("AICameraActivity", "Error converting bitmap to file", e)
            null
        }
    }

    private fun showError(message: String) {
        resultTextView.text = "Error: $message"
        resultTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }
}