package com.sumeyye.urbanflora.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sumeyye.urbanflora.data.local.db.UrbanFloraDatabase
import com.sumeyye.urbanflora.data.repository.UserProgressRepositoryImpl
import com.sumeyye.urbanflora.domain.model.UserProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = UrbanFloraDatabase.getDatabase(application)
    private val userProgressRepository = UserProgressRepositoryImpl(db.userProgressDao())

    val userProgress: StateFlow<UserProgress?> = userProgressRepository
        .getUserProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
