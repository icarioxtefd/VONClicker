package org.vono.luisdtefd.vonclicker.gameMain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class GameHomeViewModel : ViewModel() {
    var i_logOutExitString = MutableLiveData<String>()
    val logOutExitString: LiveData<String> get() = i_logOutExitString

    var i_timesTapped = MutableLiveData<Int>()
    val timesTapped: LiveData<Int> get() = i_timesTapped

    //constructor
    init {
        //changes whether the user is logged or not
        i_logOutExitString.value = if (FirebaseAuth.getInstance().currentUser != null) "Log Out" else "Exit"

        //todo get all their values from the database acc's played_data things (y luego ponerlas en el frag para su visualizacion)


    }

}
