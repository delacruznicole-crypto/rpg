package com.example.rpg

data class SkillResult(
    val message: String,
    val damageDealt: Int = 0,
    val healed: Int = 0,
    val isCritical: Boolean = false
)
