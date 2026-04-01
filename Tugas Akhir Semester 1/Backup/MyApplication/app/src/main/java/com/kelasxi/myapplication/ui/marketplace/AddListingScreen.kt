package com.kelasxi.myapplication.ui.marketplace

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.myapplication.model.ProductCategory
import com.kelasxi.myapplication.model.ProductCondition
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel

// ─── mapping domain ↔ API key ────────────────────────────────────
private val categoryOptions = listOf(
    ProductCategory.FURNITURE   to "furniture",
    ProductCategory.ELECTRONICS to "electronics",
    ProductCategory.CLOTHING    to "clothing",
    ProductCategory.BOOKS       to "books",
    ProductCategory.OTHERS      to "others"
)

private val conditionOptions = listOf(
    ProductCondition.LIKE_NEW to "like_new",
    ProductCondition.GOOD     to "good",
    ProductCondition.FAIR     to "fair"
)

private val categoryEmoji = mapOf(
    ProductCategory.FURNITURE   to "🪑",
    ProductCategory.ELECTRONICS to "💻",
    ProductCategory.CLOTHING    to "👗",
    ProductCategory.BOOKS       to "📚",
    ProductCategory.OTHERS      to "🛍️"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    viewModel: MarketplaceViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val isCreating     by viewModel.isCreatingListing.collectAsStateWithLifecycle()
    val createError    by viewModel.createListingError.collectAsStateWithLifecycle()
    val createSuccess  by viewModel.createListingSuccess.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Form state
    var name        by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceRaw    by remember { mutableStateOf("") }
    var stockRaw    by remember { mutableStateOf("1") }
    var category    by remember { mutableStateOf(categoryOptions[0]) }
    var condition   by remember { mutableStateOf(conditionOptions[0]) }

    // Image picker
    var imageUri    by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { imageUri = it } }

    // Validation errors
    var nameError   by remember { mutableStateOf<String?>(null) }
    var descError   by remember { mutableStateOf<String?>(null) }
    var priceError  by remember { mutableStateOf<String?>(null) }
    var stockError  by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(createError) {
        if (createError != null) {
            snackbarHostState.showSnackbar(createError!!)
            viewModel.dismissCreateListingError()
        }
    }
    LaunchedEffect(createSuccess) {
        if (createSuccess != null) {
            snackbarHostState.showSnackbar(createSuccess!!)
            viewModel.dismissCreateListingSuccess()
            onSuccess()
        }
    }

    fun validate(): Boolean {
        nameError  = if (name.isBlank()) "Nama barang wajib diisi" else null
        descError  = if (description.isBlank()) "Deskripsi wajib diisi" else null
        priceError = when {
            priceRaw.isBlank()                     -> "Harga wajib diisi"
            priceRaw.toLongOrNull() == null        -> "Harga harus berupa angka"
            priceRaw.toLong() < 1_000              -> "Harga minimal Rp 1.000"
            else                                   -> null
        }
        stockError = when {
            stockRaw.isBlank()                    -> "Stok wajib diisi"
            stockRaw.toIntOrNull() == null        -> "Stok harus berupa angka"
            stockRaw.toInt() < 1                  -> "Stok minimal 1"
            stockRaw.toInt() > 9999               -> "Stok maksimal 9999"
            else                                  -> null
        }
        return nameError == null && descError == null && priceError == null && stockError == null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Jual Barang",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep
                )
            )
        },
        containerColor = BackgroundGreen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Header card ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenDeep)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏷️", fontSize = 36.sp)
                    Column {
                        Text(
                            "Pasang Iklan Gratis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            "Isi detail barang yang ingin kamu jual",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // ── Section: Foto Barang ──────────────────────────────
            FormSectionCard(title = "📷 Foto Barang") {
                if (imageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { imagePicker.launch("image/*") }
                    ) {
                        AsyncImage(
                            model            = imageUri,
                            contentDescription = "Foto barang",
                            contentScale     = ContentScale.Crop,
                            modifier         = Modifier.fillMaxSize()
                        )
                        // Overlay hint to change
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.4f))
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Tap untuk ganti foto",
                                fontSize = 12.sp,
                                color    = Color.White
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 2.dp,
                                color = GreenDeep.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(GreenDeep.copy(alpha = 0.05f))
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint     = GreenDeep
                            )
                            Text(
                                "Tambah Foto",
                                fontWeight = FontWeight.SemiBold,
                                color      = GreenDeep,
                                fontSize   = 14.sp
                            )
                            Text(
                                "JPEG / PNG / WebP · Maks 5 MB",
                                fontSize = 11.sp,
                                color    = TextHint
                            )
                        }
                    }
                }
            }

            // ── Section: Info Barang ──────────────────────────────
            FormSectionCard(title = "📦 Info Barang") {
                // Nama barang
                FormField(label = "Nama Barang *") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = null },
                        placeholder = { Text("Contoh: Kursi Kayu Jati", color = TextHint) },
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedFieldColors(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                }

                // Deskripsi
                FormField(label = "Deskripsi *") {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it; descError = null },
                        placeholder = { Text("Ceritakan kondisi, ukuran, alasan jual, dll.", color = TextHint) },
                        isError = descError != null,
                        supportingText = descError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedFieldColors(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }

                // Harga
                FormField(label = "Harga (Rp) *") {
                    OutlinedTextField(
                        value = priceRaw,
                        onValueChange = { v ->
                            priceRaw = v.filter { it.isDigit() }
                            priceError = null
                        },
                        placeholder = { Text("Contoh: 150000", color = TextHint) },
                        isError = priceError != null,
                        supportingText = if (priceError != null) {
                            { Text(priceError!!, color = MaterialTheme.colorScheme.error) }
                        } else if (priceRaw.toLongOrNull() != null) {
                            { Text("= ${formatRupiah(priceRaw.toLong())}", color = TextSecondary) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        prefix = { Text("Rp ", color = TextSecondary, fontWeight = FontWeight.SemiBold) }
                    )
                }

                // Stok
                FormField(label = "Stok *") {
                    OutlinedTextField(
                        value = stockRaw,
                        onValueChange = { v ->
                            stockRaw = v.filter { it.isDigit() }
                            stockError = null
                        },
                        placeholder = { Text("Contoh: 5", color = TextHint) },
                        isError = stockError != null,
                        supportingText = if (stockError != null) {
                            { Text(stockError!!, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text("📦", modifier = Modifier.padding(start = 12.dp)) }
                    )
                }
            }

            // ── Section: Kategori ─────────────────────────────────
            FormSectionCard(title = "🗂️ Kategori") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Two rows of chips, max 3 per row using FlowRow-style
                    val chunked = categoryOptions.chunked(3)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chunked.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { opt ->
                                    val selected = category == opt
                                    FilterChip(
                                        selected = selected,
                                        onClick  = { category = opt },
                                        label    = {
                                            Text(
                                                "${categoryEmoji[opt.first]} ${opt.first.label}",
                                                fontSize = 12.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GreenDeep,
                                            selectedLabelColor = Color.White
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selected,
                                            borderColor = DividerColor,
                                            selectedBorderColor = GreenDeep
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Section: Kondisi ──────────────────────────────────
            FormSectionCard(title = "🔍 Kondisi Barang") {
                conditionOptions.forEach { opt ->
                    val selected = condition == opt
                    val (icon, desc) = when (opt.first) {
                        ProductCondition.LIKE_NEW -> "✨" to "Hampir tidak ada cacat, jarang dipakai"
                        ProductCondition.GOOD     -> "👍" to "Masih bagus, bekas pakai normal"
                        ProductCondition.FAIR     -> "⚠️" to "Ada cacat minor, masih berfungsi baik"
                        else                      -> "📦" to ""
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { condition = opt },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) GreenDeep.copy(alpha = 0.08f)
                            else SurfaceVariant
                        ),
                        border = if (selected)
                            BorderStroke(1.5.dp, GreenDeep) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(icon, fontSize = 22.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    opt.first.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize   = 14.sp,
                                    color      = if (selected) GreenDeep else TextPrimary
                                )
                                Text(desc, fontSize = 11.sp, color = TextHint)
                            }
                            RadioButton(
                                selected = selected,
                                onClick  = { condition = opt },
                                colors   = RadioButtonDefaults.colors(selectedColor = GreenDeep),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }

            // ── Submit button ─────────────────────────────────────
            Button(
                onClick = {
                    if (validate()) {
                        viewModel.createListing(
                            name        = name.trim(),
                            description = description.trim(),
                            price       = priceRaw.toLong(),
                            category    = category.second,
                            condition   = condition.second,
                            imageUri    = imageUri,
                            stock       = stockRaw.toIntOrNull() ?: 1
                        )
                    }
                },
                enabled  = !isCreating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color    = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Memproses...",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = Color.White
                    )
                } else {
                    Icon(
                        Icons.Outlined.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Pasang Iklan Sekarang",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = Color.White
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Reusable composables
// ─────────────────────────────────────────────────────────────────

@Composable
private fun FormSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun FormField(
    label: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            label,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            color      = TextSecondary
        )
        Spacer(Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = GreenDeep,
    focusedLabelColor    = GreenDeep,
    unfocusedBorderColor = DividerColor,
    cursorColor          = GreenDeep,
    focusedContainerColor   = SurfaceWhite,
    unfocusedContainerColor = SurfaceWhite
)

private fun formatRupiah(amount: Long): String {
    val chars     = amount.toString().reversed()
    val formatted = StringBuilder()
    for ((i, c) in chars.withIndex()) {
        if (i > 0 && i % 3 == 0) formatted.append('.')
        formatted.append(c)
    }
    return "Rp ${formatted.reverse()}"
}

// ─────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddListingScreenPreview() {
    TrashCareTheme {
        AddListingScreen()
    }
}
