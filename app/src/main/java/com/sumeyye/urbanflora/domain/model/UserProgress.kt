package com.sumeyye.urbanflora.domain.model

data class UserProgress(
    val id: Int = 1,
    val totalScore: Int = 0,
    val discoveredCount: Int = 0,
    val unlockedBadges: List<String> = emptyList()
)
