package com.kelasxi.weatherapp.utils

import com.kelasxi.weatherapp.model.*

object DummyData {
    
    fun createDummyWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            cityName = "Jakarta",
            main = Main(
                temperature = 28.5,
                feelsLike = 32.0,
                tempMin = 25.0,
                tempMax = 32.0,
                pressure = 1013,
                humidity = 65
            ),
            weather = listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "cerah",
                    icon = "01d"
                )
            ),
            wind = Wind(
                speed = 3.5,
                degree = 180
            ),
            sys = Sys(
                country = "ID",
                sunrise = 1693526415, // 06:00
                sunset = 1693569615   // 18:00
            ),
            coord = Coord(
                longitude = 106.8456,
                latitude = -6.2088
            )
        )
    }
    
    fun createCloudyWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            cityName = "Bandung",
            main = Main(
                temperature = 22.0,
                feelsLike = 24.0,
                tempMin = 20.0,
                tempMax = 25.0,
                pressure = 1015,
                humidity = 78
            ),
            weather = listOf(
                Weather(
                    id = 803,
                    main = "Clouds",
                    description = "mendung",
                    icon = "04d"
                )
            ),
            wind = Wind(
                speed = 2.1,
                degree = 225
            ),
            sys = Sys(
                country = "ID",
                sunrise = 1693526415,
                sunset = 1693569615
            ),
            coord = Coord(
                longitude = 107.6191,
                latitude = -6.9175
            )
        )
    }
    
    fun createRainyWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            cityName = "Bogor",
            main = Main(
                temperature = 24.0,
                feelsLike = 27.0,
                tempMin = 22.0,
                tempMax = 26.0,
                pressure = 1010,
                humidity = 85
            ),
            weather = listOf(
                Weather(
                    id = 500,
                    main = "Rain",
                    description = "hujan ringan",
                    icon = "10d"
                )
            ),
            wind = Wind(
                speed = 1.8,
                degree = 90
            ),
            sys = Sys(
                country = "ID",
                sunrise = 1693526415,
                sunset = 1693569615
            ),
            coord = Coord(
                longitude = 106.7886,
                latitude = -6.5944
            )
        )
    }
}
