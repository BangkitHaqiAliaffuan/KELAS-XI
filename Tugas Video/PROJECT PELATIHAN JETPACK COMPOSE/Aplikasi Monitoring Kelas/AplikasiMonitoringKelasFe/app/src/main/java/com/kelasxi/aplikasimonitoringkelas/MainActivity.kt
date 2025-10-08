package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.viewmodel.AuthViewModel
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading
    val loginSuccess by viewModel.loginSuccess
    val errorMessage by viewModel.errorMessage
    val user by viewModel.user
    val token by viewModel.token
    
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    // Handle login success
    LaunchedEffect(loginSuccess) {
        if (loginSuccess && user != null && token != null) {
            // Simpan data login
            sharedPrefManager.saveLoginData(
                token = token!!,
                userId = user!!.id,
                name = user!!.name,
                email = user!!.email,
                role = user!!.role
            )
            
            // Navigate berdasarkan role dari database
            val intent = when (user!!.role.lowercase()) {
                "siswa" -> Intent(context, SiswaActivity::class.java)
                "guru" -> Intent(context, KurikulumActivity::class.java) // Mapping guru ke kurikulum
                "admin" -> Intent(context, AdminActivity::class.java)
                else -> {
                    Toast.makeText(context, "Role tidak dikenali: ${user!!.role}", Toast.LENGTH_LONG).show()
                    null
                }
            }
            
            intent?.let { 
                context.startActivity(it)
                if (context is ComponentActivity) {
                    context.finish()
                }
            }
            
            // Reset state
            viewModel.resetLoginState()
        }
    }
    
    // Handle error message
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // School Logo
        Image(
            painter = painterResource(id = R.drawable.logo_sekolah),
            contentDescription = "Logo Sekolah",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp)
        )
        
        // Title
        Text(
            text = "Aplikasi Monitoring Kelas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Masuk dengan akun Anda",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Email Field dengan validasi ketat
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = it.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(it).matches()
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError,
            supportingText = {
                if (emailError) {
                    Text(
                        text = "Format email tidak valid",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field dengan validasi
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = it.isNotEmpty() && it.length < 6
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError,
            supportingText = {
                if (passwordError) {
                    Text(
                        text = "Password minimal 6 karakter",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Login Button dengan validasi ketat
        Button(
            onClick = {
                // Reset error states
                emailError = false
                passwordError = false
                
                // Validasi input
                var hasError = false
                
                if (email.isEmpty()) {
                    Toast.makeText(context, "Email harus diisi", Toast.LENGTH_SHORT).show()
                    hasError = true
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = true
                    hasError = true
                }
                
                if (password.isEmpty()) {
                    Toast.makeText(context, "Password harus diisi", Toast.LENGTH_SHORT).show()
                    hasError = true
                } else if (password.length < 6) {
                    passwordError = true
                    hasError = true
                }
                
                // Jika tidak ada error, lakukan login ke server Laravel
                if (!hasError) {
                    viewModel.login(email.trim(), password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() && !emailError && !passwordError
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }
        
        // Status text
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Memvalidasi dengan server...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Info akun test
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Akun untuk Testing:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Admin: admin@sekolah.com", fontSize = 12.sp)
                Text("Guru: siti.guru@sekolah.com", fontSize = 12.sp)
                Text("Siswa: andi.siswa@sekolah.com", fontSize = 12.sp)
                Text("Password: password123", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AplikasiMonitoringKelasTheme {
        LoginScreen()
    }
}