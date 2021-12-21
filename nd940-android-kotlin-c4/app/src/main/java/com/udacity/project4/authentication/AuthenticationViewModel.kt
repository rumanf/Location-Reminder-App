package com.udacity.project4.authentication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlin.random.Random

class AuthenticationViewModel: ViewModel() {


    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLivaData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }


}
