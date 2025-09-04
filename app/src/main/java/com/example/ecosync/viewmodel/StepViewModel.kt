package com.example.ecosync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecosync.data.DestCategory
import com.example.ecosync.data.DestinationRepo
import com.example.ecosync.ui.haversineKm
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class StepViewModel : ViewModel() {

    // --- Models ---
    data class Goal(
        val id: String,
        val name: String,
        val targetKm: Long,
        val progressKm: Double
    )

    data class Reward(
        val id: String,
        val name: String,
        val pointsRequired: Int,
        val partner: String,
        val redeemedAt: String? = null,
        val code: String? = null
    )

    // Friend now tracks points too
    data class Friend(
        val name: String,
        val steps: Int,
        val points: Int = 0   // default so old calls still compile
    )

    // Simple community grouping
    data class Community(
        val id: String,
        val name: String,
        val members: List<Friend>
    )

    data class UiState(
        val stepsToday: Int = 0,
        val isAuto: Boolean = false,
        val km: Double = 0.0,
        val co2Kg: Double = 0.0,
        val points: Int = 0,
        val dailyGoalSteps: Int = 8000,
        val originLat: Double = -35.2820,
        val originLon: Double = 149.1287,
        val goals: List<Goal> = emptyList(),
        val selectedGoalId: String? = null,
        val rewards: List<Reward> = listOf(
            Reward("bus10", "Ten percent off Bus pass", 1200, "City Transit"),
            Reward("tree1", "Plant one tree", 2000, "GreenEarth"),
            Reward("cafe5", "5 % off on any Small Latte", 500, "Local Cafe")
        ),
        // Friends leaderboard (mock)
        val friends: List<Friend> = listOf(
            Friend(name = "Mustafa", steps = 9800, points = 120),
            Friend(name = "Aisha", steps = 8600, points = 105),
            Friend(name = "Leo", steps = 7400, points = 92)
        ),
        // Communities list (seeded via a button)
        val communities: List<Community> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    // conversions
    val strideM = 0.78
    val co2KgPerKm = 0.192
    private val pointsPerStep = 1f / 100f

    private var timer: Job? = null

    fun toggleAuto() {
        val next = !_ui.value.isAuto
        _ui.value = _ui.value.copy(isAuto = next)
        if (next) start() else stop()
    }

    fun addBonusPoints(n: Int = 2) {
        val s = _ui.value
        _ui.value = s.copy(points = s.points + n)
    }

    private fun start() {
        if (timer != null) return
        timer = viewModelScope.launch {
            while (true) {
                addSteps((2..7).random())
                delay(1000)
            }
        }
    }
    private fun stop() { timer?.cancel(); timer = null }

    fun addSteps(n: Int) {
        val s = _ui.value
        val steps = s.stepsToday + n
        val km = stepsToKm(steps)
        val co2 = km * co2KgPerKm
        val points = (steps * pointsPerStep).toInt()
        val goals = s.goals.map { it.copy(progressKm = km) }
        _ui.value = s.copy(stepsToday = steps, km = km, co2Kg = co2, points = points, goals = goals)
    }

    fun setDailyGoal(newGoal: Int) {
        _ui.value = _ui.value.copy(dailyGoalSteps = newGoal.coerceAtLeast(1))
    }

    // Journey helpers
    fun setOrigin(lat: Double, lon: Double) {
        val s = _ui.value
        val updatedGoals = s.goals.map { g ->
            val dest = DestinationRepo.findByName(g.name) ?: return@map g
            val target: Long = when (dest.category) {
                DestCategory.Planets   -> dest.planetDistanceKm ?: g.targetKm
                DestCategory.Countries -> haversineKm(lat, lon, dest.lat, dest.lon).toLong()
            }
            g.copy(targetKm = target)
        }
        _ui.value = s.copy(originLat = lat, originLon = lon, goals = updatedGoals)
    }

    fun addOrSelectGoalByName(name: String) {
        val dest = DestinationRepo.findByName(name) ?: return
        val s = _ui.value
        val target: Long = when (dest.category) {
            DestCategory.Planets   -> dest.planetDistanceKm ?: 40_075L
            DestCategory.Countries -> haversineKm(s.originLat, s.originLon, dest.lat, dest.lon).toLong()
        }
        val id = name.lowercase().replace(" ", "_")
        val exists = s.goals.any { it.id == id }
        val goals = if (exists) s.goals else s.goals + Goal(id, name, target, s.km)
        _ui.value = s.copy(goals = goals, selectedGoalId = id)
    }

    fun selectGoal(id: String) {
        if (_ui.value.goals.any { it.id == id }) {
            _ui.value = _ui.value.copy(selectedGoalId = id)
        }
    }

    // Rewards helpers
    fun stepsNeededForReward(pointsRequired: Int): Int {
        val remainingPoints = (pointsRequired - _ui.value.points).coerceAtLeast(0)
        return remainingPoints * 100
    }
    fun canRedeem(pointsRequired: Int): Boolean = _ui.value.points >= pointsRequired
    fun redeem(id: String) {
        val s = _ui.value
        val reward = s.rewards.find { it.id == id } ?: return
        if (s.points < reward.pointsRequired) return

        val newCode = buildString {
            val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            repeat(8) { append(alphabet[Random.nextInt(alphabet.length)]) }
        }
        val updated = s.rewards.map {
            if (it.id == id) it.copy(
                redeemedAt = System.currentTimeMillis().toString(),
                code = newCode
            ) else it
        }
        _ui.value = s.copy(rewards = updated, points = s.points - reward.pointsRequired)
    }

    // Friends / Community helpers
    fun addFriend(name: String, steps: Int, points: Int = 0) {
        val cleanName = name.trim().ifBlank { return }
        val s = _ui.value
        _ui.value = s.copy(friends = s.friends + Friend(cleanName, steps.coerceAtLeast(0), points.coerceAtLeast(0)))
    }

    fun setupDemoCommunities() {
        val uc = Community(
            id = "uc_staff",
            name = "UC Teaching Staff",
            members = listOf(
                Friend("Mustafa", 9800, 120),
                Friend("Dr. Kim", 11250, 140),
                Friend("Sophie", 9050, 118),
                Friend("Arjun", 7800, 100)
            )
        )
        val kpmg = Community(
            id = "kpmg_account",
            name = "KPMG Account team",
            members = listOf(
                Friend("Zara", 10100, 132),
                Friend("Mateo", 9600, 125),
                Friend("Priya", 8800, 110)
            )
        )
        val s = _ui.value
        _ui.value = s.copy(communities = listOf(uc, kpmg))
    }

    private fun stepsToKm(steps: Int): Double = steps * strideM / 1000.0

    companion object { val shared = StepViewModel() }
}