package org.vono.luisdtefd.vonclicker.gameMain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class GameHomeViewModel : ViewModel() {
    var i_logOutExitString = MutableLiveData<String>()
    val logOutExitString: LiveData<String> get() = i_logOutExitString

    //constructor
    init {
        //changes whether the user is logged or not
        i_logOutExitString.value = if (FirebaseAuth.getInstance().currentUser != null) "Log Out" else "Exit"
    }

}
