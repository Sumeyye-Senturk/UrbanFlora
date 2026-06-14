package com.sumeyye.urbanflora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sumeyye.urbanflora.domain.model.UserProgress

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalScore: Int,
    val discoveredCount: Int,
    val unlockedBadges: List<String>
) {
    fun toDomain(): UserProgress {
        return UserProgress(
            id = id,
            totalScore = totalScore,
            discoveredCount = discoveredCount,
            unlockedBadges = unlockedBadges
        )
    }

    companion object {
        fun fromDomain(progress: UserProgress): UserProgressEntity {
            return UserProgressEntity(
                id = progress.id,
                totalScore = progress.totalScore,
                discoveredCount = progress.discoveredCount,
                unlockedBadges = progress.unlockedBadges
            )
        }
    }
}
