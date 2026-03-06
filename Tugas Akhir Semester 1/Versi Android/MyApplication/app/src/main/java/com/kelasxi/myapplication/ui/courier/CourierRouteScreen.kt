package com.kelasxi.myapplication.ui.courier

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.net.HttpURLConnection
import java.net.URL

// ─────────────────────────────────────────────────────────────────
// CourierRouteScreen
// Shows the fastest driving route from courier's current location
// to the pickup destination using OSRM (free, no API key needed).
// ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierRouteScreen(
    destLat: Double,
    destLng: Double,
    destAddress: String,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ── State ──────────────────────────────────────────────────────
    var courierLat by remember { mutableDoubleStateOf(0.0) }
    var courierLng by remember { mutableDoubleStateOf(0.0) }
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var distanceText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var isLoadingRoute by remember { mutableStateOf(false) }
    var routeError by remember { mutableStateOf<String?>(null) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // ── Permission launcher ────────────────────────────────────────
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getLastLocation(context) { lat, lng ->
                courierLat = lat
                courierLng = lng
                coroutineScope.launch {
                    isLoadingRoute = true
                    routeError = null
                    fetchOsrmRoute(lat, lng, destLat, destLng) { points, dist, dur, err ->
                        routePoints = points
                        distanceText = dist
                        durationText = dur
                        routeError = err
                        isLoadingRoute = false
                        // Update map
                        mapViewRef?.let { updateMapRoute(it, lat, lng, destLat, destLng, points) }
                    }
                }
            }
        }
    }

    // ── Request location + route on first composition ──────────────
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName

        val hasPerm = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPerm) {
            isLoadingRoute = true
            getLastLocation(context) { lat, lng ->
                courierLat = lat
                courierLng = lng
                coroutineScope.launch {
                    // Guard: emulator GPS not set → lat/lng will be 0.0
                    if (lat == 0.0 && lng == 0.0) {
                        routeError = "GPS belum tersedia.\n" +
                                "Jika menggunakan emulator:\n" +
                                "1. Buka Extended Controls (ikon ···)\n" +
                                "2. Pilih Location\n" +
                                "3. Masukkan koordinat lalu klik Set Location"
                        isLoadingRoute = false
                        return@launch
                    }
                    fetchOsrmRoute(lat, lng, destLat, destLng) { points, dist, dur, err ->
                        routePoints = points
                        distanceText = dist
                        durationText = dur
                        routeError = err
                        isLoadingRoute = false
                        mapViewRef?.let { updateMapRoute(it, lat, lng, destLat, destLng, points) }
                    }
                }
            }
        } else {
            permLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rute Pengiriman", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Map ────────────────────────────────────────────────
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).also { mv ->
                        mv.setTileSource(TileSourceFactory.MAPNIK)
                        mv.setMultiTouchControls(true)
                        mv.controller.setZoom(14.0)
                        mv.controller.setCenter(GeoPoint(destLat, destLng))
                        mapViewRef = mv
                    }
                },
                update = { mv ->
                    if (routePoints.isNotEmpty() && courierLat != 0.0) {
                        updateMapRoute(mv, courierLat, courierLng, destLat, destLng, routePoints)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // ── Route info bottom card ────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Destination row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = GreenDeep,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = destAddress.ifBlank { "Tujuan pickup" },
                            fontSize = 13.sp,
                            color = TextPrimary,
                            maxLines = 2,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        isLoadingRoute -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = GreenDeep
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Menghitung rute…", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                        routeError != null -> {
                            Text(
                                text = "⚠️ ${routeError}",
                                color = StatusCancelled,
                                fontSize = 13.sp
                            )
                        }
                        distanceText.isNotBlank() -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                RouteStatChip(
                                    icon = Icons.Default.Straighten,
                                    label = "Jarak",
                                    value = distanceText
                                )
                                RouteStatChip(
                                    icon = Icons.Default.Schedule,
                                    label = "Estimasi",
                                    value = durationText
                                )
                            }
                        }
                    }

                    // Retry / Re-center button
                    if (!isLoadingRoute) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isLoadingRoute = true
                                    routeError = null
                                    getLastLocation(context) { lat, lng ->
                                        courierLat = lat; courierLng = lng
                                        coroutineScope.launch {
                                            fetchOsrmRoute(lat, lng, destLat, destLng) { pts, d, t, e ->
                                                routePoints = pts
                                                distanceText = d
                                                durationText = t
                                                routeError = e
                                                isLoadingRoute = false
                                                mapViewRef?.let { updateMapRoute(it, lat, lng, destLat, destLng, pts) }
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Perbarui Rute", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = GreenDeep, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, color = TextHint)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

// ─────────────────────────────────────────────────────────────────
// Routing — OpenRouteService (primary) + OSRM fallback + straight-line
//
// Priority:
//   1. OpenRouteService API  (api.openrouteservice.org) — reliable, free 2k/day
//   2. OSRM demo server      (router.project-osrm.org)  — fallback
//   3. OSRM OSM mirror       (routing.openstreetmap.de) — fallback
//   4. Straight-line estimate (Haversine)               — always works
// ─────────────────────────────────────────────────────────────────
private suspend fun fetchOsrmRoute(
    fromLat: Double, fromLng: Double,
    toLat: Double, toLng: Double,
    onResult: (points: List<GeoPoint>, distance: String, duration: String, error: String?) -> Unit
) {
    // ── 1. OpenRouteService (POST /v2/directions/driving-car) ─────
    val orsKey = com.kelasxi.myapplication.BuildConfig.ORS_API_KEY
    if (orsKey.isNotBlank()) {
        try {
            val result = withContext(Dispatchers.IO) {
                val url  = "https://api.openrouteservice.org/v2/directions/driving-car"
                val body = """{"coordinates":[[$fromLng,$fromLat],[$toLng,$toLat]]}"""
                    .toByteArray(Charsets.UTF_8)

                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput      = true
                    setRequestProperty("Authorization", orsKey)
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("Accept", "application/json, application/geo+json")
                    connectTimeout = 12_000
                    readTimeout    = 12_000
                }
                conn.outputStream.use { it.write(body) }

                val code = conn.responseCode
                if (code != HttpURLConnection.HTTP_OK) {
                    conn.disconnect()
                    return@withContext null
                }
                val text = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                text
            } ?: throw Exception("ORS non-200")

            // ORS GeoJSON response: routes[0].geometry.coordinates (lng,lat)
            val json     = JSONObject(result)
            val route    = json.getJSONArray("routes").getJSONObject(0)
            val summary  = route.getJSONObject("summary")
            val distanceM = summary.getDouble("distance")
            val durationS = summary.getDouble("duration")

            val distText = if (distanceM >= 1000)
                "${"%.1f".format(distanceM / 1000)} km" else "${distanceM.toInt()} m"
            val durText  = if (durationS >= 3600) {
                val h = (durationS / 3600).toInt()
                val m = ((durationS % 3600) / 60).toInt()
                "${h} jam ${m} mnt"
            } else "${(durationS / 60).toInt()} mnt"

            val coords = route
                .getJSONObject("geometry")
                .getJSONArray("coordinates")
            val points = (0 until coords.length()).map { i ->
                val c = coords.getJSONArray(i)
                GeoPoint(c.getDouble(1), c.getDouble(0))   // [lng,lat] → GeoPoint(lat,lng)
            }

            onResult(points, distText, durText, null)
            return   // ✓ done

        } catch (_: Exception) {
            // ORS failed — fall through to OSRM
        }
    }

    // ── 2 & 3. OSRM public mirrors ────────────────────────────────
    val osrmEndpoints = listOf(
        "https://router.project-osrm.org",
        "https://routing.openstreetmap.de/routed-car"
    )
    for (base in osrmEndpoints) {
        try {
            val url = "$base/route/v1/driving/" +
                    "$fromLng,$fromLat;$toLng,$toLat" +
                    "?overview=full&geometries=geojson"

            val response = withContext(Dispatchers.IO) {
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 Chrome/120 Safari/537.36")
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 12_000
                    readTimeout    = 12_000
                    instanceFollowRedirects = true
                }
                val code = conn.responseCode
                if (code != HttpURLConnection.HTTP_OK) { conn.disconnect(); return@withContext null }
                val body = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                body
            } ?: continue

            val json      = JSONObject(response)
            val routes    = json.getJSONArray("routes")
            if (routes.length() == 0) continue

            val route     = routes.getJSONObject(0)
            val distanceM = route.getDouble("distance")
            val durationS = route.getDouble("duration")

            val distText = if (distanceM >= 1000)
                "${"%.1f".format(distanceM / 1000)} km" else "${distanceM.toInt()} m"
            val durText  = if (durationS >= 3600) {
                val h = (durationS / 3600).toInt()
                val m = ((durationS % 3600) / 60).toInt()
                "${h} jam ${m} mnt"
            } else "${(durationS / 60).toInt()} mnt"

            val coords = route.getJSONObject("geometry").getJSONArray("coordinates")
            val points = (0 until coords.length()).map { i ->
                val c = coords.getJSONArray(i)
                GeoPoint(c.getDouble(1), c.getDouble(0))
            }
            onResult(points, distText, durText, null)
            return

        } catch (_: Exception) { /* try next */ }
    }

    // ── 4. Straight-line fallback (Haversine) ─────────────────────
    val r     = 6_371_000.0
    val dLat  = Math.toRadians(toLat - fromLat)
    val dLon  = Math.toRadians(toLng - fromLng)
    val a     = Math.sin(dLat / 2).let { it * it } +
                Math.cos(Math.toRadians(fromLat)) *
                Math.cos(Math.toRadians(toLat)) *
                Math.sin(dLon / 2).let { it * it }
    val straightM  = 2 * r * Math.asin(Math.sqrt(a))
    val drivingM   = straightM * 1.4
    val drivingSec = drivingM / (30_000.0 / 3600.0)

    val distText = if (drivingM >= 1000)
        "~${"%.1f".format(drivingM / 1000)} km" else "~${drivingM.toInt()} m"
    val durText  = if (drivingSec >= 3600) {
        val h = (drivingSec / 3600).toInt()
        val m = ((drivingSec % 3600) / 60).toInt()
        "~${h} jam ${m} mnt"
    } else "~${(drivingSec / 60).toInt()} mnt"

    onResult(
        listOf(GeoPoint(fromLat, fromLng), GeoPoint(toLat, toLng)),
        distText, durText,
        "Server rute tidak tersedia — menampilkan estimasi jarak lurus"
    )
}

// ─────────────────────────────────────────────────────────────────
// Update map overlays: courier marker, destination marker, polyline
// ─────────────────────────────────────────────────────────────────
private fun updateMapRoute(
    mv: MapView,
    fromLat: Double, fromLng: Double,
    toLat: Double, toLng: Double,
    routePoints: List<GeoPoint>
) {
    mv.overlays.clear()

    // Route polyline
    if (routePoints.isNotEmpty()) {
        val polyline = Polyline(mv).apply {
            setPoints(routePoints)
            outlinePaint.color = android.graphics.Color.parseColor("#2E7D32")
            outlinePaint.strokeWidth = 10f
        }
        mv.overlays.add(polyline)
    }

    // Courier marker (start)
    val courierMarker = Marker(mv).apply {
        position = GeoPoint(fromLat, fromLng)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = "Posisi Kurir"
    }
    mv.overlays.add(courierMarker)

    // Destination marker
    val destMarker = Marker(mv).apply {
        position = GeoPoint(toLat, toLng)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = "Tujuan Pickup"
    }
    mv.overlays.add(destMarker)

    // Zoom to show both markers
    if (routePoints.isNotEmpty()) {
        val box = BoundingBox.fromGeoPoints(routePoints)
        mv.zoomToBoundingBox(box.increaseByScale(1.3f), true, 80)
    }

    mv.invalidate()
}

// ─────────────────────────────────────────────────────────────────
// Get last known location from FusedLocationProvider
// ─────────────────────────────────────────────────────────────────
// ─────────────────────────────────────────────────────────────────
// Get courier's current GPS location.
// Uses getCurrentLocation() (active fix) with lastLocation fallback.
// lastLocation on emulator returns null if GPS has never been used,
// causing "0.0,0.0" or the emulator's default San Francisco coords.
// ─────────────────────────────────────────────────────────────────
private fun getLastLocation(context: Context, onResult: (Double, Double) -> Unit) {
    try {
        val client = com.google.android.gms.location.LocationServices
            .getFusedLocationProviderClient(context)

        // Priority: BALANCED gives a quick network/cell fix even on emulator
        val priority = com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
        val cts = com.google.android.gms.tasks.CancellationTokenSource()

        client.getCurrentLocation(priority, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onResult(location.latitude, location.longitude)
                } else {
                    // Fallback: use cached lastLocation
                    client.lastLocation.addOnSuccessListener { last ->
                        if (last != null) {
                            onResult(last.latitude, last.longitude)
                        }
                        // if still null → emulator has no GPS fix at all,
                        // caller's courierLat/Lng stays 0.0 and OSRM will
                        // show the "set GPS in emulator" error message below
                    }
                }
            }
            .addOnFailureListener {
                // Fallback to lastLocation on any error
                client.lastLocation.addOnSuccessListener { last ->
                    if (last != null) onResult(last.latitude, last.longitude)
                }
            }
    } catch (_: SecurityException) {}
}
