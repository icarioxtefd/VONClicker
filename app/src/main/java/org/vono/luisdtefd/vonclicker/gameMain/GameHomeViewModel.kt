package org.vono.luisdtefd.vonclicker.gameMain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class GameHomeViewModel : ViewModel() {
    var i_logOutExitString = MutableLiveData<String>()
    val logOutExitString: LiveData<String> get() = i_logOutExitString

    var i_timesSaved = MutableLiveData<Int>()
    val timesSaved: LiveData<Int> get() = i_timesSaved

    var i_timesTapped = MutableLiveData<Int>()
    val timesTapped: LiveData<Int> get() = i_timesTapped

    var i_tapMultiplier = MutableLiveData<Int>()
    val tapMultiplier: LiveData<Int> get() = i_tapMultiplier

    var i_currency = MutableLiveData<Int>()
    val currency: LiveData<Int> get() = i_currency

    var i_upgrades = MutableLiveData<HashMap<String, HashMap<String, Any>>>()
    val upgrades: LiveData<HashMap<String, HashMap<String, Any>>> get() = i_upgrades



    //constructor
    init {
        //changes whether the user is logged or not
        i_logOutExitString.value = if (isUserLogged()) "Log Out" else "Exit"
        i_timesTapped.value = 0
        i_tapMultiplier.value = 1
        i_timesSaved.value = 0
        i_currency.value = 0
        initializeUpgradesMap() //i_upgrades.value

        Log.i("GameHomeViewModel", "Init")

    }





    private fun initializeUpgradesMap() {
        var upgradesMap = HashMap<String, java.util.HashMap<String, Any>>()

        //every upgrade as a map
        var electrifyMap = HashMap<String, Any>()
        electrifyMap["bought"] = false
        electrifyMap["level"] = 0

        var directCurrentMap = HashMap<String, Any>()
        directCurrentMap["bought"] = false
        directCurrentMap["level"] = 0

        //adding every map to the root map
        upgradesMap["electrify"] = electrifyMap
        upgradesMap["directCurrent"] = directCurrentMap

        i_upgrades.value = upgradesMap
    }

    override fun onCleared() { //whenever the viewModel gets destroyed
        super.onCleared()
        Log.i("GameHomeViewModel", "Destroyed")
    }
}
