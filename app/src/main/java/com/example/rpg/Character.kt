package com.example.rpg

import kotlin.random.Random

open class Character(
    val name: String,
    var health: Int = 100,
    var attackPower: Int = 15,
    var defense: Int = 5
) {
    var isBlocking = false
    var hasPowerBuff = false

    fun isAlive(): Boolean = health > 0

    fun attackTarget(target: Character): CombatResult {
        if (!isAlive() || !target.isAlive()) return CombatResult("⚠️ $name cannot attack.")

        // --- Miss chance (10%) ---
        if (Random.nextDouble() < 0.1) {
            return CombatResult("💨 $name's attack missed!")
        }

        // --- Critical chance (20%) ---
        val isCritical = Random.nextDouble() < 0.2

        // --- Base damage ---
        var damage = Random.nextInt(8, 15) + attackPower - target.defense
        if (damage < 0) damage = 0

        // --- Apply buffs ---
        if (hasPowerBuff) {
            damage = (damage * 1.5).toInt()
            hasPowerBuff = false
        }

        // --- Apply critical multiplier ---
        if (isCritical) {
            damage = (damage * 1.5).toInt()
        }

        // --- Apply block reduction ---
        if (target.isBlocking) {
            damage = (damage * 0.5).toInt()
        }

        // --- Reduce target HP ---
        target.health -= damage
        if (target.health < 0) target.health = 0

        // --- Log message ---
        val critText = if (isCritical) " 💥 Critical hit!" else ""
        val blockText = if (target.isBlocking) " 🛡️ Blocked!" else ""
        return CombatResult("⚔️ $name attacks ${target.name} for $damage damage!$critText$blockText")
    }

    fun powerStrike(target: Character): CombatResult {
        hasPowerBuff = true
        return CombatResult("⚡ $name is charging a Power Strike! Next attack will deal extra damage!")
    }

    fun healSelf(): CombatResult {
        val healAmount = Random.nextInt(10, 20)
        health = (health + healAmount).coerceAtMost(100)
        return CombatResult("💚 $name heals for $healAmount HP!")
    }

    fun block(): CombatResult {
        isBlocking = true
        return CombatResult("🛡️ $name braces for impact and is blocking incoming attacks!")
    }
}

class Player : Character("Player")
class Enemy : Character("Enemy")

data class CombatResult(val message: String)
