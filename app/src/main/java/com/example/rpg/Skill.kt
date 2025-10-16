package com.example.rpg

data class Skill(
    val id: Int,
    val name: String,
    val cooldownMillis: Long,
    val power: Int = 0, // Damage or heal amount
    val type: SkillType
)

enum class SkillType {
    ATTACK, BLOCK, HEAL, POWER_STRIKE
}
