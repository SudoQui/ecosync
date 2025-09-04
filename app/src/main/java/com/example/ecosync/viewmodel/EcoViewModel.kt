package com.example.ecosync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

class EcoViewModel : ViewModel() {

    data class Appliance(
        val id: String,
        val name: String,
        val watts: Int,
        val kWhToday: Double,
        val isOn: Boolean
    )

    data class UiState(
        val connected: Boolean = false,
        val solarWatts: Int = 0,
        val houseWatts: Int = 0,
        val batterySoc: Int = 62,
        val appliances: List<Appliance> = listOf(
            Appliance("lights", "Lights", 120, 0.35, true),
            Appliance("hvac", "HVAC", 850, 4.9, true),
            Appliance("fridge", "Fridge", 90, 1.1, true),
            Appliance("washer", "Washer", 0, 0.7, false),
            Appliance("ev", "EV Charger", 0, 0.0, false),
            Appliance("custom", "Custom", 30, 0.2, true)
        )
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    private var ticker: Job? = null

    fun connect() {
        if (_ui.value.connected) return
        _ui.value = _ui.value.copy(connected = true)
        startStreaming()
    }

    fun disconnect() {
        _ui.value = _ui.value.copy(connected = false)
        ticker?.cancel()
        ticker = null
    }

    fun toggle(id: String) {
        val s = _ui.value
        val list = s.appliances.map {
            if (it.id == id) it.copy(isOn = !it.isOn) else it
        }
        _ui.value = s.copy(appliances = list)
    }

    private fun startStreaming() {
        if (ticker != null) return
        ticker = viewModelScope.launch {
            while (_ui.value.connected) {
                stepOnce()
                delay(1000)
            }
        }
    }

    private fun stepOnce() {
        val s = _ui.value

        // Simulate solar curve: daytime 0 to 4 kW with some jitter
        val solar = (Random.nextInt(800, 3800) + Random.nextInt(-150, 150)).coerceIn(0, 4200)

        // Update appliances: if on, wander a bit, if off, small standby
        val newAppliances = s.appliances.map { a ->
            val base = when (a.id) {
                "hvac" -> if (a.isOn) 700 + Random.nextInt(-80, 180) else 10
                "fridge" -> if (a.isOn) 70 + Random.nextInt(-10, 30) else 5
                "washer" -> if (a.isOn) 500 + Random.nextInt(-50, 120) else 2
                "ev" -> if (a.isOn) 2200 + Random.nextInt(-200, 300) else 3
                "lights" -> if (a.isOn) 100 + Random.nextInt(-20, 40) else 2
                else -> if (a.isOn) 30 + Random.nextInt(-10, 20) else 1
            }.coerceAtLeast(0)
            val kwh = a.kWhToday + base / 1000.0 / 3600.0 // add one second worth
            a.copy(watts = base, kWhToday = kwh)
        }

        val house = newAppliances.sumOf { it.watts }
        val net = solar - house

        // Battery SOC moves a tiny bit based on net
        val socDelta = when {
            net > 300 -> +1
            net < -300 -> -1
            else -> 0
        }
        val newSoc = (s.batterySoc + socDelta).coerceIn(5, 95)

        _ui.value = s.copy(
            solarWatts = solar,
            houseWatts = house,
            batterySoc = newSoc,
            appliances = newAppliances
        )
    }

    fun netWatts(): Int = _ui.value.solarWatts - _ui.value.houseWatts

    fun co2SavedKgToday(gridFactorKgPerKWh: Double = 0.7): Double {
        // If solar exceeds house, approximate exported energy as co2 avoided
        val s = _ui.value
        val exportW = max(0, s.solarWatts - s.houseWatts)
        val exportKWhPerSec = exportW / 1000.0 / 3600.0
        // rough cumulative is not tracked per second here, so estimate from appliance kWh share
        val totalKWh = s.appliances.sumOf { it.kWhToday }
        val solarFrac = if (s.solarWatts + s.houseWatts == 0) 0.0
        else s.solarWatts.toDouble() / (s.solarWatts + s.houseWatts).toDouble()
        val solarKWhToday = totalKWh * solarFrac
        return solarKWhToday * gridFactorKgPerKWh
    }

    companion object { val shared = EcoViewModel() }
}