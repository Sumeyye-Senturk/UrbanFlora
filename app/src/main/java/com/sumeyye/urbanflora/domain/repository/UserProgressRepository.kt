package com.sumeyye.urbanflora.domain.repository

import com.sumeyye.urbanflora.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface UserProgressRepository {
    fun getUserProgress(): Flow<UserProgress?>
    suspend fun saveUserProgress(progress: UserProgress)
    suspend fun updateScoreAndCount(score: Int, count: Int)
}
