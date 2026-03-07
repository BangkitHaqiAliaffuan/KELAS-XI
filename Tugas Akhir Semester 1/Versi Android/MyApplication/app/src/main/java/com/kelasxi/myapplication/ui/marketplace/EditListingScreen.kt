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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.myapplication.model.Product
import com.kelasxi.myapplication.model.ProductCategory
import com.kelasxi.myapplication.model.ProductCondition
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel

// ─── mapping domain ↔ API key ─────────────────────────────────────
private val editCategoryOptions = listOf(
    ProductCategory.FURNITURE   to "furniture",
    ProductCategory.ELECTRONICS to "electronics",
    ProductCategory.CLOTHING    to "clothing",
    ProductCategory.BOOKS       to "books",
    ProductCategory.OTHERS      to "others"
)

private val editConditionOptions = listOf(
    ProductCondition.LIKE_NEW to "like_new",
    ProductCondition.GOOD     to "good",
    ProductCondition.FAIR     to "fair"
)

private val editCategoryEmoji = mapOf(
    ProductCategory.FURNITURE   to "🪑",
    ProductCategory.ELECTRONICS to "💻",
    ProductCategory.CLOTHING    to "👗",
    ProductCategory.BOOKS       to "📚",
    ProductCategory.OTHERS      to "🛍️"
)

// ─── Helper: ProductCategory → API key ────────────────────────────
private fun ProductCategory.toApiKey(): String = when (this) {
    ProductCategory.FURNITURE   -> "furniture"
    ProductCategory.ELECTRONICS -> "electronics"
    ProductCategory.CLOTHING    -> "clothing"
    ProductCategory.BOOKS       -> "books"
    else                        -> "others"
}

// ─── Helper: ProductCondition → API key ───────────────────────────
private fun ProductCondition.toApiKey(): String = when (this) {
    ProductCondition.LIKE_NEW -> "like_new"
    ProductCondition.GOOD     -> "good"
    else                      -> "fair"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListingScreen(
    productId: String,
    viewModel: MarketplaceViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val isUpdating      by viewModel.isUpdatingListing.collectAsStateWithLifecycle()
    val updateError     by viewModel.updateListingError.collectAsStateWithLifecycle()
    val updateSuccess   by viewModel.updateListingSuccess.collectAsStateWithLifecycle()

    // Cari produk dari daftar myListings (sudah ter-load di MyShopScreen)
    val myListings      by viewModel.myListings.collectAsStateWithLifecycle()
    val product = myListings.find { it.id == productId }

    val snackbarHostState = remember { SnackbarHostState() }

    // Form state — pre-fill dari data produk yang sudah ada
    var name        by remember(product) { mutableStateOf(product?.name ?: "") }
    var description by remember(product) { mutableStateOf(product?.description ?: "") }
    var priceRaw    by remember(product) { mutableStateOf(product?.price?.toString() ?: "") }
    var stockValue  by remember(product) { mutableIntStateOf(product?.stock ?: 1) }

    // Default category dari produk, fallback ke index 0
    var category by remember(product) {
        mutableStateOf(
            editCategoryOptions.firstOrNull { it.first == product?.category }
                ?: editCategoryOptions[0]
        )
    }
    // Default condition dari produk, fallback ke index 0
    var condition by remember(product) {
        mutableStateOf(
            editConditionOptions.firstOrNull { it.first == product?.condition }
                ?: editConditionOptions[0]
        )
    }

    // Image picker — starts null (keep existing), set to new Uri if user picks
    var imageUri    by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { imageUri = it } }

    // Validation errors
    var nameError   by remember { mutableStateOf<String?>(null) }
    var descError   by remember { mutableStateOf<String?>(null) }
    var priceError  by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(updateError) {
        if (updateError != null) {
            snackbarHostState.showSnackbar(updateError!!)
            viewModel.dismissUpdateListingError()
        }
    }
    LaunchedEffect(updateSuccess) {
        if (updateSuccess != null) {
            snackbarHostState.showSnackbar(updateSuccess!!)
            viewModel.dismissUpdateListingSuccess()
            onSuccess()
        }
    }

    fun validate(): Boolean {
        nameError  = if (name.isBlank()) "Nama barang wajib diisi" else null
        descError  = if (description.isBlank()) "Deskripsi wajib diisi" else null
        priceError = when {
            priceRaw.isBlank()              -> "Harga wajib diisi"
            priceRaw.toLongOrNull() == null -> "Harga harus berupa angka"
            priceRaw.toLong() < 1_000       -> "Harga minimal Rp 1.000"
            else                            -> null
        }
        return nameError == null && descError == null && priceError == null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "✏️ Edit Listing",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        if (product != null) {
                            Text(
                                product.name,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                maxLines = 1
                            )
                        }
                    }
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

        // Product not found state
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("😕", fontSize = 64.sp)
                    Text(
                        "Produk tidak ditemukan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    TextButton(onClick = onBack) {
                        Text("Kembali", color = GreenDeep)
                    }
                }
            }
            return@Scaffold
        }

        // Product is sold — cannot be edited
        if (product.isSold) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("🔒", fontSize = 64.sp)
                    Text(
                        "Tidak Dapat Diedit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        "Barang yang sudah terjual tidak dapat diedit.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kembali", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            return@Scaffold
        }

        // Edit form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Header info card ──────────────────────────────────
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
                    Text("✏️", fontSize = 32.sp)
                    Column {
                        Text(
                            "Edit Informasi Barang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            "Perbarui detail listing yang ingin kamu ubah",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // ── Section: Foto Barang ──────────────────────────────
            EditFormSectionCard(title = "📷 Foto Barang") {
                val displayImageModel: Any? = imageUri ?: product?.imageUrl?.takeIf { it.isNotBlank() }
                if (displayImageModel != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { imagePicker.launch("image/*") }
                    ) {
                        AsyncImage(
                            model              = displayImageModel,
                            contentDescription = "Foto barang",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
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
            EditFormSectionCard(title = "📦 Info Barang") {
                // Nama barang
                EditFormField(label = "Nama Barang *") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = null },
                        placeholder = { Text("Contoh: Kursi Kayu Jati", color = TextHint) },
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = editOutlinedFieldColors(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                }

                // Deskripsi
                EditFormField(label = "Deskripsi *") {
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
                        colors = editOutlinedFieldColors(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }

                // Harga
                EditFormField(label = "Harga (Rp) *") {
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
                            { Text("= ${editFormatRupiah(priceRaw.toLong())}", color = TextSecondary) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = editOutlinedFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        prefix = { Text("Rp ", color = TextSecondary, fontWeight = FontWeight.SemiBold) }
                    )
                }

                // Stok
                EditFormField(label = "📦 Stok") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilledIconButton(
                            onClick  = { if (stockValue > 0) stockValue-- },
                            enabled  = stockValue > 0,
                            colors   = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (stockValue > 0) GreenDeep else DividerColor
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text("-", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (stockValue == 0) "Sold Out" else stockValue.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stockValue == 0) MaterialTheme.colorScheme.error else GreenDeep
                            )
                            Text(
                                text = if (stockValue == 0) "Stok habis — listing dinonaktifkan" else "unit tersedia",
                                fontSize = 11.sp,
                                color = TextHint
                            )
                        }
                        FilledIconButton(
                            onClick  = { if (stockValue < 9999) stockValue++ },
                            enabled  = stockValue < 9999,
                            colors   = IconButtonDefaults.filledIconButtonColors(
                                containerColor = GreenDeep
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text("+", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Section: Kategori ─────────────────────────────────
            EditFormSectionCard(title = "🗂️ Kategori") {
                val chunked = editCategoryOptions.chunked(3)
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
                                            "${editCategoryEmoji[opt.first]} ${opt.first.label}",
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

            // ── Section: Kondisi ──────────────────────────────────
            EditFormSectionCard(title = "🔍 Kondisi Barang") {
                editConditionOptions.forEach { opt ->
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
                        viewModel.updateListing(
                            productId   = productId,
                            name        = name.trim(),
                            description = description.trim(),
                            price       = priceRaw.toLong(),
                            category    = category.second,
                            condition   = condition.second,
                            imageUri    = imageUri,
                            stock       = stockValue
                        )
                    }
                },
                enabled  = !isUpdating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Menyimpan...",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = Color.White
                    )
                } else {
                    Icon(
                        Icons.Outlined.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Simpan Perubahan",
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
// Reusable composables (private to this file)
// ─────────────────────────────────────────────────────────────────

@Composable
private fun EditFormSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
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
private fun EditFormField(
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
private fun editOutlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = GreenDeep,
    focusedLabelColor       = GreenDeep,
    unfocusedBorderColor    = DividerColor,
    cursorColor             = GreenDeep,
    focusedContainerColor   = SurfaceWhite,
    unfocusedContainerColor = SurfaceWhite
)

private fun editFormatRupiah(amount: Long): String {
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
private fun EditListingScreenPreview() {
    TrashCareTheme {
        // Untuk preview, inject produk dummy via ViewModel snapshot tidak bisa langsung,
        // tampilkan state "product not found" sebagai fallback preview
        EditListingScreen(productId = "preview_id")
    }
}
