package com.example.rpg

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    // --- Characters ---
    private val player = Player()
    private val enemy = Enemy()

    // --- UI State ---
    val playerHealth = mutableIntStateOf(player.health)
    val enemyHealth = mutableIntStateOf(enemy.health)

    val combatLogs = mutableStateListOf<String>()

    // --- Cooldowns (milliseconds) ---
    val playerAttackRemaining = mutableLongStateOf(0L)
    val playerBlockRemaining = mutableLongStateOf(0L)
    val playerHealRemaining = mutableLongStateOf(0L)
    val playerPowerStrikeRemaining = mutableLongStateOf(0L)

    val enemyAttackRemaining = mutableLongStateOf(0L)
    val enemyBlockRemaining = mutableLongStateOf(0L)
    val enemyHealRemaining = mutableLongStateOf(0L)
    val enemyPowerStrikeRemaining = mutableLongStateOf(0L)

    // --- Coroutine jobs for timers ---
    private val jobs = mutableListOf<Job>()

    init {
        startBattle()
    }

    private fun startBattle() {
        // 🔹 Player skill loops (slightly randomized cooldowns)
        jobs += loopSkillTimer(
            cooldownState = playerAttackRemaining,
            cooldownTime = (2700L..3200L).random()
        ) { attack(player, enemy) }

        jobs += loopSkillTimer(
            cooldownState = playerBlockRemaining,
            cooldownTime = (5500L..6500L).random()
        ) { block(player) }

        jobs += loopSkillTimer(
            cooldownState = playerHealRemaining,
            cooldownTime = (7500L..8500L).random()
        ) { heal(player) }

        jobs += loopSkillTimer(
            cooldownState = playerPowerStrikeRemaining,
            cooldownTime = (9500L..10500L).random()
        ) { powerStrike(player, enemy) }

        // 🔹 Enemy skill loops (identical base cooldowns for fairness)
        jobs += loopSkillTimer(
            cooldownState = enemyAttackRemaining,
            cooldownTime = (2700L..3200L).random()
        ) { attack(enemy, player) }

        jobs += loopSkillTimer(
            cooldownState = enemyBlockRemaining,
            cooldownTime = (5500L..6500L).random()
        ) { block(enemy) }

        jobs += loopSkillTimer(
            cooldownState = enemyHealRemaining,
            cooldownTime = (7500L..8500L).random()
        ) { heal(enemy) }

        jobs += loopSkillTimer(
            cooldownState = enemyPowerStrikeRemaining,
            cooldownTime = (9500L..10500L).random()
        ) { powerStrike(enemy, player) }
    }


    private fun loopSkillTimer(
        cooldownState: androidx.compose.runtime.MutableLongState,
        cooldownTime: Long,
        action: suspend () -> Unit
    ) = viewModelScope.launch {
        var remaining = cooldownTime
        while (player.isAlive() && enemy.isAlive()) {
            if (remaining > 0) {
                delay(100L)
                remaining -= 100L
                cooldownState.longValue = remaining
            } else {
                action()
                remaining = cooldownTime
                cooldownState.longValue = remaining
            }
        }
    }

    // --- Skill Actions ---

    private suspend fun attack(attacker: Character, target: Character) {
        if (!attacker.isAlive() || !target.isAlive()) return
        delay(300L)
        val result = attacker.attackTarget(target)
        updateHealth()
        addLog(result.message)
        checkGameEnd()
    }

    private suspend fun powerStrike(attacker: Character, target: Character) {
        if (!attacker.isAlive() || !target.isAlive()) return
        delay(300L)
        val result = attacker.powerStrike(target)
        updateHealth()
        addLog(result.message)
    }

    private suspend fun heal(character: Character) {
        if (!character.isAlive()) return
        delay(300L)
        val result = character.healSelf()
        updateHealth()
        addLog(result.message)
    }

    private suspend fun block(character: Character) {
        if (!character.isAlive()) return
        delay(300L)
        val result = character.block()
        addLog(result.message)
        delay(2000L) // block duration
        character.isBlocking = false
    }

    private fun updateHealth() {
        playerHealth.intValue = player.health
        enemyHealth.intValue = enemy.health
    }

    private fun addLog(message: String) {
        combatLogs.add(0, message) // newest on top
        if (combatLogs.size > 100) if (combatLogs.isNotEmpty()) {
            combatLogs.removeAt(combatLogs.lastIndex)
        }
    }

    private fun checkGameEnd() {
        if (!player.isAlive() || !enemy.isAlive()) {
            val winner = if (player.isAlive()) "🎉 Player Wins!" else "💀 Enemy Wins!"
            addLog(winner)
            stopAllTimers()
        }
    }

    // --- Utility functions ---

    fun stopAllTimers() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }

    fun resetGame() {
        stopAllTimers()
        combatLogs.clear()
        player.health = 100
        enemy.health = 100
        player.isBlocking = false
        enemy.isBlocking = false
        updateHealth()
        startBattle()
        addLog("🔄 Game reset! The battle begins anew!")
    }
}

