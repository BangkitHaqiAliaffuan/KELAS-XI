package com.kelasxi.simpleweatherapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class untuk response dari API cuaca
 * Sesuai dengan struktur response dari OpenWeatherMap API atau WeatherAPI
 */
data class WeatherResponse(
    @SerializedName("location")
    val location: Location,
    @SerializedName("current")
    val current: CurrentWeather
)

/**
 * Data class untuk response forecast dari WeatherAPI
 */
data class ForecastResponse(
    @SerializedName("location")
    val location: Location,
    @SerializedName("current")
    val current: CurrentWeather,
    @SerializedName("forecast")
    val forecast: Forecast
)

/**
 * Data class untuk forecast container
 */
data class Forecast(
    @SerializedName("forecastday")
    val forecastDays: List<ForecastDay>
)

/**
 * Data class untuk data forecast harian
 */
data class ForecastDay(
    @SerializedName("date")
    val date: String,
    @SerializedName("date_epoch")
    val dateEpoch: Long,
    @SerializedName("day")
    val day: DayWeather,
    @SerializedName("astro")
    val astro: Astro,
    @SerializedName("hour")
    val hours: List<HourWeather>
)

/**
 * Data class untuk data cuaca harian
 */
data class DayWeather(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,
    @SerializedName("mintemp_c")
    val minTempC: Double,
    @SerializedName("avgtemp_c")
    val avgTempC: Double,
    @SerializedName("condition")
    val condition: WeatherCondition,
    @SerializedName("maxwind_kph")
    val maxWindKph: Double,
    @SerializedName("totalprecip_mm")
    val totalPrecipMm: Double,
    @SerializedName("avghumidity")
    val avgHumidity: Int,
    @SerializedName("uv")
    val uvIndex: Double
)

/**
 * Data class untuk data astronomi
 */
data class Astro(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String,
    @SerializedName("moonrise")
    val moonrise: String,
    @SerializedName("moonset")
    val moonset: String,
    @SerializedName("moon_phase")
    val moonPhase: String
)

/**
 * Data class untuk data cuaca per jam
 */
data class HourWeather(
    @SerializedName("time_epoch")
    val timeEpoch: Long,
    @SerializedName("time")
    val time: String,
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("condition")
    val condition: WeatherCondition,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("precip_mm")
    val precipMm: Double,
    @SerializedName("chance_of_rain")
    val chanceOfRain: Int
)

/**
 * Data class untuk informasi lokasi
 */
data class Location(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("localtime")
    val localTime: String
)

/**
 * Data class untuk informasi cuaca saat ini
 */
data class CurrentWeather(
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("condition")
    val condition: WeatherCondition,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("uv")
    val uvIndex: Double,
    @SerializedName("precip_mm")
    val precipMm: Double
)

/**
 * Data class untuk kondisi cuaca
 */
data class WeatherCondition(
    @SerializedName("text")
    val text: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("code")
    val code: Int
)

/**
 * Sealed class untuk mengelola state UI
 */
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val weather: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

/**
 * Sealed class untuk mengelola forecast UI state
 */
sealed class ForecastUiState {
    object Loading : ForecastUiState()
    data class Success(val forecast: ForecastResponse) : ForecastUiState()
    data class Error(val message: String) : ForecastUiState()
}