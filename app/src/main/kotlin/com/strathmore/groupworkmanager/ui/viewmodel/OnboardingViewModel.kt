package com.strathmore.groupworkmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strathmore.groupworkmanager.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the onboarding screen. Handles saving a user's name
 * into the local database.
 */
class OnboardingViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun saveUserName(name: String, onDone: () -> Unit) {
        viewModelScope.launch {
            userRepository.saveUser(name)
            onDone()
        }
    }
}