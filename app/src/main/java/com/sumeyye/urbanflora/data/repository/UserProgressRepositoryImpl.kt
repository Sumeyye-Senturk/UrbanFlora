package com.sumeyye.urbanflora.data.repository

import com.sumeyye.urbanflora.data.local.dao.UserProgressDao
import com.sumeyye.urbanflora.data.local.entity.UserProgressEntity
import com.sumeyye.urbanflora.domain.model.UserProgress
import com.sumeyye.urbanflora.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProgressRepositoryImpl(
    private val userProgressDao: UserProgressDao
) : UserProgressRepository {

    override fun getUserProgress(): Flow<UserProgress?> {
        return userProgressDao.getUserProgress().map { it?.toDomain() }
    }

    override suspend fun saveUserProgress(progress: UserProgress) {
        userProgressDao.insertOrUpdate(UserProgressEntity.fromDomain(progress))
    }

    override suspend fun updateScoreAndCount(score: Int, count: Int) {
        userProgressDao.updateScoreAndCount(score, count)
    }
}
