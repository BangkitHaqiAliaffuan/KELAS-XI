package com.kelasxi.myapplication.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.kelasxi.myapplication.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// ─────────────────────────────────────────────────────────────────
// MapPickerScreen
// Full-screen osmdroid map. User can pan/pinch; the crosshair pin
// stays fixed at the centre. On "Konfirmasi", we reverse-geocode the
// centre point via Nominatim (free, no key needed) and push the
// result back through NavController.savedStateHandle.
// ─────────────────────────────────────────────────────────────────

/** Keys used to pass the result back to the calling screen. */
object MapPickerResult {
    const val KEY_LAT     = "map_picker_lat"
    const val KEY_LNG     = "map_picker_lng"
    const val KEY_ADDRESS = "map_picker_address"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    navController: NavController,
    initialLat: Double = -6.2088,   // Jakarta default
    initialLng: Double = 106.8456
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ── State ──────────────────────────────────────────────────────
    var centerLat by remember { mutableDoubleStateOf(initialLat) }
    var centerLng by remember { mutableDoubleStateOf(initialLng) }
    var resolvedAddress by remember { mutableStateOf("Memuat alamat…") }
    var isGeocoding by remember { mutableStateOf(false) }
    var isConfirming by remember { mutableStateOf(false) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // ── Location permission ────────────────────────────────────────
    val hasLocationPerm = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            moveToCurrentLocation(context, mapViewRef) { lat, lng ->
                centerLat = lat; centerLng = lng
                coroutineScope.launch { reverseGeocode(lat, lng) { resolvedAddress = it } }
            }
        }
    }

    // ── Initial geocode for the default centre ─────────────────────
    LaunchedEffect(Unit) {
        reverseGeocode(centerLat, centerLng) { resolvedAddress = it }
        // configure osmdroid user-agent once
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // ── Debounce geocode when user stops scrolling ─────────────────
    // Nominatim rate limit: 1 req/second per IP.
    // Wait 1200 ms before calling — prevents 429 on emulator (shared IP).
    LaunchedEffect(centerLat, centerLng) {
        isGeocoding = true
        delay(1200L)  // debounce — cancels if lat/lng changes again within 1.2s
        reverseGeocode(centerLat, centerLng) {
            resolvedAddress = it
            isGeocoding = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Lokasi", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── osmdroid MapView ───────────────────────────────────
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).also { mv ->
                        mv.setTileSource(TileSourceFactory.MAPNIK)
                        mv.setMultiTouchControls(true)
                        mv.controller.setZoom(16.0)
                        mv.controller.setCenter(GeoPoint(centerLat, centerLng))

                        // Listen for map scroll / zoom to update center state
                        mv.addMapListener(object : org.osmdroid.events.MapListener {
                            override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                                val center = mv.mapCenter
                                centerLat = center.latitude
                                centerLng = center.longitude
                                return false
                            }
                            override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean {
                                val center = mv.mapCenter
                                centerLat = center.latitude
                                centerLng = center.longitude
                                return false
                            }
                        })

                        mapViewRef = mv
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // ── Fixed crosshair pin at centre ──────────────────────
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Pin",
                    tint = GreenDeep,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = (-24).dp)   // tip of pin sits at exact center
                )
            }

            // ── GPS button ─────────────────────────────────────────
            FloatingActionButton(
                onClick = {
                    if (hasLocationPerm) {
                        moveToCurrentLocation(context, mapViewRef) { lat, lng ->
                            centerLat = lat; centerLng = lng
                        }
                    } else {
                        permLauncher.launch(arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ))
                    }
                },
                containerColor = Color.White,
                contentColor = GreenDeep,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 160.dp)
                    .size(48.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = "Lokasi saya")
            }

            // ── Bottom sheet: address + confirm ───────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Alamat Dipilih",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextHint
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = GreenDeep,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isGeocoding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = GreenDeep
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = resolvedAddress,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isConfirming = true
                            // Save result into back-stack entry and pop
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.apply {
                                    set(MapPickerResult.KEY_LAT, centerLat)
                                    set(MapPickerResult.KEY_LNG, centerLng)
                                    set(MapPickerResult.KEY_ADDRESS, resolvedAddress)
                                }
                            navController.popBackStack()
                        },
                        enabled = !isGeocoding && !isConfirming,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isConfirming) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Konfirmasi Lokasi", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Nominatim reverse-geocoding (free, no API key required)
// GET https://nominatim.openstreetmap.org/reverse?lat=&lon=&format=json
//
// Nominatim rate limit: 1 request/second per IP.
// On emulator, all instances share the same public IP → 429 is common.
// Solution: debounce 1200 ms in LaunchedEffect + retry up to 3× on 429.
// ─────────────────────────────────────────────────────────────────
private suspend fun reverseGeocode(lat: Double, lng: Double, onResult: (String) -> Unit) {
    val maxRetries = 3
    var lastError = "Tidak dapat memuat alamat"

    for (attempt in 1..maxRetries) {
        try {
            val address = withContext(Dispatchers.IO) {
                val urlStr = "https://nominatim.openstreetmap.org/reverse" +
                        "?lat=$lat&lon=$lng&format=json&accept-language=id"
                val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "TrashCareApp/1.0 (android)")
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 10000
                    readTimeout = 10000
                    instanceFollowRedirects = true
                }

                val responseCode = conn.responseCode

                // 429 Too Many Requests — Nominatim rate limit hit (common on emulator)
                if (responseCode == 429) {
                    conn.disconnect()
                    return@withContext null  // null = should retry
                }

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    conn.disconnect()
                    return@withContext "Lokasi tidak dikenali"
                }

                val json = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val obj = JSONObject(json)

                // Nominatim returns {"error":"Unable to geocode"} when nothing found
                if (obj.has("error")) {
                    return@withContext "Lokasi tidak dikenali"
                }

                val displayName = obj.optString("display_name", "")
                val addr = obj.optJSONObject("address")

                if (addr != null) {
                    // Build a short, readable Indonesian address
                    val road = addr.optString("road").takeIf { it.isNotBlank() }
                    val suburb = addr.optString("neighbourhood").takeIf { it.isNotBlank() }
                        ?: addr.optString("suburb").takeIf { it.isNotBlank() }
                        ?: addr.optString("quarter").takeIf { it.isNotBlank() }
                    val city = addr.optString("city").takeIf { it.isNotBlank() }
                        ?: addr.optString("town").takeIf { it.isNotBlank() }
                        ?: addr.optString("municipality").takeIf { it.isNotBlank() }
                        ?: addr.optString("county").takeIf { it.isNotBlank() }
                        ?: addr.optString("village").takeIf { it.isNotBlank() }
                    val state = addr.optString("state").takeIf { it.isNotBlank() }

                    listOfNotNull(road, suburb, city, state)
                        .joinToString(", ")
                        .ifBlank { displayName }
                } else {
                    displayName
                }
            }

            if (address != null) {
                // Success — got a valid response
                onResult(address.ifBlank { "Lokasi tidak dikenali" })
                return
            }

            // null means 429 — wait before retrying (exponential backoff: 2s, 4s, 8s)
            if (attempt < maxRetries) {
                delay(2000L * attempt)
            } else {
                lastError = "Lokasi tidak dikenali"
            }

        } catch (e: Exception) {
            lastError = "Tidak dapat memuat alamat"
            if (attempt < maxRetries) delay(2000L * attempt)
        }
    }

    onResult(lastError)
}

// ─────────────────────────────────────────────────────────────────
// Move map to device's current location using FusedLocationProvider
// ─────────────────────────────────────────────────────────────────
private fun moveToCurrentLocation(
    context: Context,
    mapView: MapView?,
    onLocation: (Double, Double) -> Unit
) {
    try {
        val fusedClient = com.google.android.gms.location.LocationServices
            .getFusedLocationProviderClient(context)
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val gp = GeoPoint(location.latitude, location.longitude)
                mapView?.controller?.animateTo(gp)
                onLocation(location.latitude, location.longitude)
            }
        }
    } catch (_: SecurityException) {
        // Permission was revoked between the check and the call
    }
}
