package com.kelas.balancebook.ui.settings

import android.content.Intent
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.PopupMenu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kelas.balancebook.LoginActivity
import com.kelas.balancebook.R
import com.kelas.balancebook.data.local.SessionManager
import com.kelas.balancebook.data.remote.ApiClient
import com.kelas.balancebook.data.remote.UpdateSettingsRequest
import com.kelas.balancebook.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val localUser = SessionManager.getUser(requireContext())
        binding.tvUserName.text = localUser?.name ?: "-"
        binding.tvUserEmail.text = localUser?.email ?: "-"
        setCurrencySubtitle(localUser?.currency)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rowCurrency.setOnClickListener {
            val popup = PopupMenu(requireContext(), binding.rowCurrency)
            popup.menu.add("IDR")
            popup.menu.add("USD")
            popup.menu.add("EUR")
            popup.setOnMenuItemClickListener {
                val selected = it.title.toString()
                updateSettings(currency = selected)
                Toast.makeText(requireContext(), "Currency: ${it.title}", Toast.LENGTH_SHORT).show()
                true
            }
            popup.show()
        }

        binding.rowExport.setOnClickListener {
            exportCsv()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        refreshProfileAndSettings()
    }

    private fun refreshProfileAndSettings() {
        lifecycleScope.launch {
            runCatching {
                val api = ApiClient.service(requireContext())
                val me = api.me().user
                val settings = api.settings().settings

                binding.tvUserName.text = me.name
                binding.tvUserEmail.text = me.email
                setCurrencySubtitle(settings.currency)

                SessionManager.saveUser(requireContext(), me)
            }
        }
    }

    private fun updateSettings(currency: String? = null, name: String? = null) {
        lifecycleScope.launch {
            runCatching {
                val response = ApiClient.service(requireContext()).updateSettings(
                    UpdateSettingsRequest(name = name, currency = currency)
                )
                SessionManager.saveUser(requireContext(), response.user)
                setCurrencySubtitle(response.user.currency)
            }.onFailure {
                Toast.makeText(requireContext(), "Gagal update pengaturan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportCsv() {
        lifecycleScope.launch {
            runCatching {
                val response = ApiClient.service(requireContext()).exportCsv()
                if (!response.isSuccessful) error("Export gagal")
                val bytes = response.body()?.bytes() ?: error("File CSV kosong")
                val fileName = "balancebook-transactions-${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())}.csv"
                val savedPath = saveCsvToStorage(fileName, bytes)
                Toast.makeText(requireContext(), "Export berhasil: $savedPath", Toast.LENGTH_LONG).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Gagal export data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveCsvToStorage(fileName: String, bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val context = requireContext().applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: error("Gagal membuat file di Downloads")

            resolver.openOutputStream(uri)?.use { output ->
                output.write(bytes)
                output.flush()
            } ?: error("Gagal menulis file")

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            uri.toString()
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
            val file = File(dir, fileName)
            file.writeBytes(bytes)
            file.absolutePath
        }
    }

    private fun setCurrencySubtitle(currency: String?) {
        val code = (currency ?: "IDR").uppercase(Locale.US)
        val label = when (code) {
            "USD" -> "USD ($) - US Dollar"
            "EUR" -> "EUR (€) - Euro"
            else -> "IDR (Rp) - Indonesian Rupiah"
        }
        binding.tvCurrencySub.text = label
    }

    private fun logout() {
        lifecycleScope.launch {
            runCatching {
                ApiClient.service(requireContext()).logout()
            }

            FirebaseAuth.getInstance().signOut()

            val webClientId = getString(R.string.google_web_client_id)
            if (!webClientId.startsWith("YOUR_WEB_CLIENT_ID")) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(webClientId)
                    .build()

                runCatching {
                    GoogleSignIn.getClient(requireContext(), gso).signOut()
                }
            }

            SessionManager.clear(requireContext())
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
