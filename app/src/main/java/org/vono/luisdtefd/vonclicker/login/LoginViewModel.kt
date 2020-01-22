package org.vono.luisdtefd.vonclicker.login

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.vono.luisdtefd.vonclicker.R
import kotlin.random.Random

class LoginViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    //  authenticationState variable based off the FirebaseUserLiveData object. By
    //  creating this variable, other classes will be able to query for whether the user is logged
    //  in or not
    val authenticationState = FirebaseUserLiveData().map {
        if (it != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}
