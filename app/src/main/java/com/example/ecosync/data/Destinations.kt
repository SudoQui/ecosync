package com.example.ecosync.data

enum class DestCategory { Countries, Planets }

data class Destination(
    val name: String,
    val category: DestCategory,
    val lat: Double = 0.0,        // for countries
    val lon: Double = 0.0,        // for countries
    val planetDistanceKm: Long? = null // for planets (Earth uses circumference here)
)

object DestinationRepo {

    // Capitals for a clean demo. Add more anytime.
    private val countries = listOf(
        Destination("Palestine (Jerusalem)", DestCategory.Countries, 31.7683, 35.2137),
        Destination("France (Paris)", DestCategory.Countries, 48.8566,   2.3522),
        Destination("United Kingdom (London)", DestCategory.Countries, 51.5074,  -0.1278),
        Destination("Germany (Berlin)", DestCategory.Countries, 52.5200, 13.4050),
        Destination("Italy (Rome)", DestCategory.Countries, 41.9028,  12.4964),
        Destination("Spain (Madrid)", DestCategory.Countries, 40.4168,  -3.7038),
        Destination("United States (Washington DC)", DestCategory.Countries, 38.9072, -77.0369),
        Destination("Canada (Ottawa)", DestCategory.Countries, 45.4215, -75.6972),
        Destination("Brazil (Brasília)", DestCategory.Countries, -15.7939, -47.8828),
        Destination("Egypt (Cairo)", DestCategory.Countries, 30.0444, 31.2357),
        Destination("Pakistan (Islamabad)", DestCategory.Countries, 33.6844, 73.0479),
        Destination("China (Beijing)", DestCategory.Countries, 39.9042, 116.4074),
        Destination("Japan (Tokyo)", DestCategory.Countries, 35.6895, 139.6917)
    )

    // Planets: average Earth–planet distance in km
    // Earth uses circumference ~ 40,075 km
    private val planets = listOf(
        Destination("Mercury", DestCategory.Planets, planetDistanceKm = 91_700_000L),
        Destination("Venus",   DestCategory.Planets, planetDistanceKm = 41_400_000L),
        Destination("Earth",   DestCategory.Planets, planetDistanceKm = 40_075L),
        Destination("Mars",    DestCategory.Planets, planetDistanceKm = 78_300_000L),
        Destination("Jupiter", DestCategory.Planets, planetDistanceKm = 628_730_000L),
        Destination("Saturn",  DestCategory.Planets, planetDistanceKm = 1_275_000_000L),
        Destination("Uranus",  DestCategory.Planets, planetDistanceKm = 2_723_950_000L),
        Destination("Neptune", DestCategory.Planets, planetDistanceKm = 4_351_400_000L)
    )

    val all: List<Destination> by lazy { countries + planets }

    fun byCategory(category: DestCategory): List<Destination> =
        all.filter { it.category == category }

    fun findByName(name: String): Destination? =
        all.firstOrNull { it.name == name }
}