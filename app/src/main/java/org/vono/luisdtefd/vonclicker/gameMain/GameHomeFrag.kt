package org.vono.luisdtefd.vonclicker.gameMain

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

import org.vono.luisdtefd.vonclicker.databinding.GameHomeFragmentBinding
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.rbddevs.splashy.Splashy

import org.vono.luisdtefd.vonclicker.MainActivity
import org.vono.luisdtefd.vonclicker.login.getCurrentUsernameString
import org.vono.luisdtefd.vonclicker.login.getUserUid

import java.util.HashMap
import com.skydoves.elasticviews.ElasticAnimation
import android.os.Handler
import android.os.SystemClock.sleep
import kotlinx.android.synthetic.main.game_home_fragment.*
import kotlinx.coroutines.*
import org.vono.luisdtefd.vonclicker.R


class GameHomeFrag : Fragment() {

    companion object {
        fun newInstance() = GameHomeFrag()
    }

    //lateinit
    private lateinit var viewModel: GameHomeViewModel
    private lateinit var binding: GameHomeFragmentBinding
    private lateinit var navController: NavController

    //ref the CF database and the accounts doc--------
    private val db = FirebaseFirestore.getInstance()
    lateinit var collRef: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get the safe arg from loginfrag and make a val with it
        val args = GameHomeFragArgs.fromBundle(arguments!!)
        val firstTimeLog = args.firstTimeLog

        //call splashy (library splash screen), but only if they are logged, just in case
        if(isUserLogged())
            setSplashy()

        //get the ref of the navController
        navController = this.findNavController()

        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_home_fragment, container, false)

        //set LCO
        binding.lifecycleOwner = this

        //set the viewModel and everything
        viewModel = ViewModelProviders.of(this).get(GameHomeViewModel::class.java)

        //change the behavior of onBackPressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { navController.popBackStack(R.id.mainFrag, false) }

        //get & enable the drawer
        var drawer = activity!!.findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)


        //check whether you got here as a guest or as an alr registered user.
        if (FirebaseAuth.getInstance().currentUser != null) { //if there's a user, gotta pull all their saved data, and make few changes
            if (! firstTimeLog) {
                Log.i("GameHomeFrag", "Trying to fetch data from FBCF...")
                loadDataFromFB()
            }
            else {
                Log.i("GameHomeFrag", "First Time logged in value: $firstTimeLog")
                Log.i("GameHomeFrag", "Bro it's their first time, I'm not loading 0s")
            }
        }//else; user is a guest, so don't do anything, just create a new game without loading anything


        //set the listener to the image
        binding.imageToTap.setOnClickListener() {
            ElasticAnimation.Builder().setView(binding.imageToTap).setScaleX(0.85f).setScaleY(0.85f).setDuration(100)
                .setOnFinishListener {
                    // Do something after duration time
                }.doAction()

            viewModel.i_timesTapped.value = viewModel.timesTapped.value!! + 1
            viewModel.i_currency.value = viewModel.currency.value!! + viewModel.tapMultiplier.value!!
            //Toast.makeText(context, "Tapped, tapped times total: " + viewModel.i_timesTapped.value, Toast.LENGTH_SHORT).show()
        }

        //observer for the viewText
        viewModel.currency.observe(this.viewLifecycleOwner, Observer {// this.viewLifecycleOwner?? <- weird
            binding.textViewCurrency.text = "Currency: ${viewModel.currency.value.toString()}"
        })

        //observer for the electrify updates -> tappingMultiplier, depending on the level of the electrify upgrade
        viewModel.upgrades.observe(this, Observer {

            //observer of electrify upgrade
            when (viewModel.upgrades.value!!["electrify"]!!["level"].toString()){
                "0" -> { viewModel.i_tapMultiplier.value = 1 }
                "1" -> { viewModel.i_tapMultiplier.value = 3 }
                "2" -> { viewModel.i_tapMultiplier.value = 6 }
            }


            //observer of directCurrent upgrade
            if (viewModel.upgrades.value!!["directCurrent"]!!["bought"] == true){

                var jjob: Job? = null
                when (viewModel.upgrades.value!!["directCurrent"]!!["level"].toString()){
                    "1" -> {
                        jjob = GlobalScope.launch(Dispatchers.Default){ // launch outside (Default) the while with the sleep, so it doesn't freeze the actual screen
                            while (true) {
                                sleep(5000)
                                withContext(Dispatchers.Main){ // and perform the click then in the actual screen (Main)
                                    binding.imageToTap.performClick()
                                }
                            }
                        }
                    }
                    "2" -> {
                        jjob?.cancel() //cancels the said when upgrading and cancels this when (todo happens again after coming back from infoFrag) and It's a bug, it doesn't seem to work at all.
                        jjob = GlobalScope.launch(Dispatchers.Default){ // same as above
                            while (true) {
                                sleep(2500)
                                withContext(Dispatchers.Main){ // same as above
                                    binding.imageToTap.performClick()
                                }
                            }
                        }
                    }
                }
            }

        })

        //OnClick for the drawer's items
        (activity as MainActivity).navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_electrify -> {
                   //create the necessary map and check which values you have to change, then replace the liveData var
                    val activatedElectrifyMap = HashMap<String, Any>()

                    if (viewModel.upgrades.value!!["electrify"]!!["bought"] == false){
                        if (viewModel.currency.value!! >= 50){
                            activatedElectrifyMap["bought"] = true
                            activatedElectrifyMap["level"] = 1

                            viewModel.i_currency.value = viewModel.currency.value!! - 50
                        }
                        else{ //NEM to buy
                            activatedElectrifyMap["bought"] = false
                            activatedElectrifyMap["level"] = 0
                        }
                    }
                    else{
                        if (viewModel.currency.value!! >= 100) {
                            if (viewModel.upgrades.value!!["electrify"]!!["level"] as Int == 1){
                                activatedElectrifyMap["bought"] = true
                                activatedElectrifyMap["level"] = 2

                                viewModel.i_currency.value = viewModel.currency.value!! - 100
                            }
                            else{ //max level atm 2; so you'd still be 2
                                activatedElectrifyMap["bought"] = true
                                activatedElectrifyMap["level"] = 2
                            }
                        }
                        else{ //NEM to buy
                            if (viewModel.upgrades.value!!["electrify"]!!["level"] as Int == 2){
                                activatedElectrifyMap["bought"] = true
                                activatedElectrifyMap["level"] = 2
                            }
                            else{
                                activatedElectrifyMap["bought"] = true
                                activatedElectrifyMap["level"] = 1
                            }
                        }
                    }

                    //Note: .replace needs min API SDK 24
                    viewModel.i_upgrades.value!!.replace("electrify", activatedElectrifyMap)
                    viewModel.i_upgrades.value = viewModel.i_upgrades.value // NECESSARY since if not, the observer DOESN'T get the change and won't work (still don't know why tho)

                    Toast.makeText(context, "Electrify tapped, now: " + viewModel.upgrades.value!!.get("electrify"), Toast.LENGTH_SHORT).show()
                }
                R.id.menu_directCurrent -> {
                    //same as before
                    val activatedDCMap = HashMap<String, Any>()

                    if (viewModel.upgrades.value!!["directCurrent"]!!["bought"] == false){
                        if (viewModel.currency.value!! >= 150){
                            activatedDCMap["bought"] = true
                            activatedDCMap["level"] = 1

                            viewModel.i_currency.value = viewModel.currency.value!! - 150
                        }
                        else{ //NEM to buy
                            activatedDCMap["bought"] = false
                            activatedDCMap["level"] = 0
                        }
                    }
                    else{
                        if (viewModel.currency.value!! >= 300) {
                            if (viewModel.upgrades.value!!["directCurrent"]!!["level"].toString() == "1"){
                                activatedDCMap["bought"] = true
                                activatedDCMap["level"] = 2

                                viewModel.i_currency.value = viewModel.currency.value!! - 300
                            }
                            else{ //max level atm 2; so you'd still be 2
                                activatedDCMap["bought"] = true
                                activatedDCMap["level"] = 2
                            }
                        }
                        else{ //NEM to buy
                            if (viewModel.upgrades.value!!["directCurrent"]!!["level"].toString() == "2"){
                                activatedDCMap["bought"] = true
                                activatedDCMap["level"] = 2
                            }
                            else{
                                activatedDCMap["bought"] = true
                                activatedDCMap["level"] = 1
                            }
                        }
                    }

                    //Note: .replace needs min API SDK 24
                    viewModel.i_upgrades.value!!.replace("directCurrent", activatedDCMap)
                    viewModel.i_upgrades.value = viewModel.i_upgrades.value // NECESSARY; same as before

                    Toast.makeText(context, "DC tapped, now: " + viewModel.upgrades.value!!.get("directCurrent"), Toast.LENGTH_SHORT).show()
                }
            }
            true
        }


        //add the buttons
        addBoomButtonsMenu()



        //return the root
        return binding.root
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) { //everything in the code using the viewModel should go here; according to the AndroidStudio IDE, but not to Google
//        super.onActivityCreated(savedInstanceState)
//    }

    private fun addBoomButtonsMenu() {
        //boom buttons, aka fab but prettier-----------------------------------------------------------------------------------------------
        //first one + initialization
        var bmbButton =
            TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.save).imagePadding(Rect(20, 20, 20, 20))
                .normalText("Save").textPadding(Rect(0, 8, 0, 0)).textSize(11)
                .normalColor(R.color.usuallygrey).shadowColor(R.color.usuallyblack)
                .unable(! isUserLogged()).unableImageRes(R.drawable.save).unableColor(android.R.color.black) //unable the button if there's no logged user
                .listener { index ->
                    // When the boom-button corresponding this builder is clicked.
                    print("Clicked $index button")

                    Toast.makeText(context,"Saving...",Toast.LENGTH_SHORT).show()
                    Log.i("GameHomeFrag", "Saving data to FBCF...")

                    viewModel.i_timesSavedManually.value = viewModel.timesSavedManually.value!! + 1
                    saveDataToFB()

                }

        for (i in 0 until binding.bmb.piecePlaceEnum.pieceNumber()) {
            when (i) {
                1 -> {
                    bmbButton = TextInsideCircleButton.Builder()
                        .normalImageRes(R.drawable.info).imagePadding(Rect(20, 20, 20, 20))
                        .normalText("Info").textPadding(Rect(0, 8, 0, 0)).textSize(11)
                        .normalColor(R.color.usuallygrey).shadowColor(R.color.usuallyblack)
                        .listener { index ->
                            // When the boom-button corresponding this builder is clicked.

                            //save before going to info frag, since it'll load again because the fragment gets destroyed when navigating and created back when returning (this is one todo)
                            viewModel.i_timesSavedByApp.value = viewModel.timesSavedByApp.value!! + 1
                            saveDataToFB()
                            //go to the info frag, passing the args as safe args (order comes from the navigation's TEXT, NOT in design
                            navController.navigate(GameHomeFragDirections.actionGameHomeFragToInfoFrag(
                                viewModel.timesTapped.value!!,
                                viewModel.timesSavedByApp.value!!,
                                viewModel.timesSavedManually.value!!,
                                viewModel.currency.value!!,
                                viewModel.tapMultiplier.value!!,
                                viewModel.upgrades.value!!["electrify"]!!["bought"] as Boolean,
                                viewModel.upgrades.value!!["electrify"]!!["level"].toString(),
                                viewModel.upgrades.value!!["directCurrent"]!!["bought"] as Boolean,
                                viewModel.upgrades.value!!["directCurrent"]!!["level"].toString())
                            ) //Note: as Int cast gives cannot cast error
                            print("Clicked $index button")
                        }
                }

                2 -> {
                    bmbButton = TextInsideCircleButton.Builder()
                        .normalImageRes(R.drawable.cancel).imagePadding(Rect(30, 35, 30, 30))
                        .normalColor(android.R.color.black)
                }

                3 -> {
                    bmbButton = TextInsideCircleButton.Builder()
                        .normalImageRes(R.drawable.key).imagePadding(Rect(20, 20, 20, 20))
                        .normalText(viewModel.logOutExitString.value).textPadding(Rect(0, 8, 0, 0)).textSize(11)
                        .normalColor(R.color.usuallygrey).shadowColor(R.color.usuallyblack)
                        .listener { index ->
                            // When the boom-button corresponding this builder is clicked.
                            print("Clicked $index button")

                            //logs out; if -> just in case
                            if(viewModel.logOutExitString.value == "Log Out"){

                                viewModel.i_timesSavedByApp.value = viewModel.timesSavedByApp.value!! + 1
                                saveDataToFB() //save, in case they forgot to save by themselves

                                AuthUI.getInstance().signOut(requireContext()) //logs out the user
                                //FirebaseAuth.getInstance().signOut()
                            }
                            //and then gets to the main frag again
                            requireActivity().onBackPressed()
                        }
                }

                4 -> {
                    bmbButton = TextInsideCircleButton.Builder()
                        .normalImageRes(R.drawable.settings).imagePadding(Rect(20, 20, 20, 20))
                        .normalText("Config").textPadding(Rect(0, 8, 0, 0)).textSize(11)
                        .normalColor(R.color.usuallygrey).shadowColor(R.color.usuallyblack)
                        .listener { index ->
                            // When the boom-button corresponding this builder is clicked.
                            print("Clicked $index button")
                            Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            binding.bmb.addBuilder(bmbButton)
        }
        //---------------------------------------------------------------------------------------------------------------------------------





    }

    private fun saveDataToFB() {
        db.collection("accounts").whereEqualTo("id", getUserUid())
            .addSnapshotListener { snapshots, e ->

                db.collection("accounts").whereEqualTo("id", getUserUid())
                    .addSnapshotListener { snapshots, e ->

                        Log.i(
                            "GameHomeFrag",
                            "Trying to save data for user " + "${getCurrentUsernameString()} with uid: " + getUserUid()
                        )

                        //also, make the played_data collection with its docs
                        var userPlayedData = HashMap<String, Any>()
                        userPlayedData["timesTapped"] = viewModel.timesTapped.value!!
                        userPlayedData["tapMultiplier"] = viewModel.tapMultiplier.value!!
                        userPlayedData["currency"] = viewModel.currency.value!!
                        userPlayedData["timesSavedManually"] = viewModel.timesSavedManually.value!!
                        userPlayedData["timesSavedByApp"] = viewModel.timesSavedByApp.value!!

                        //add the root map from the viewModel
                        userPlayedData["upgrades"] = viewModel.i_upgrades.value!!

                        //and add/set it
                        db.collection("accounts").document(getCurrentUsernameString())
                            .collection("played_data").document("generals").update(userPlayedData)
                    }
            }
    }

    private fun loadDataFromFB() {

        //create the args to be filled
        var docId: String?
        var timesTapped: Long?
        var tapMultiplier: Long?
        var currency: Long?
        var timesSavedManually: Long?
        var timesSavedByApp: Long?
        var upgrades: HashMap<String, HashMap<String, Any>>?


        //get the reference of the whole collection
        collRef = db.collection("accounts").document(getCurrentUsernameString()).collection("played_data")
        //add the snapshot listener
        collRef.addSnapshotListener { snapshots, e ->
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> { //si se añade en tiempo real + la primera carga
                        docId = dc.document.id //general's doc (name)

                        //assign the values to the vars
                        timesTapped = dc.document.getLong("timesTapped") //firestore doesn't have getInt() so well, let's hope this will do
                        tapMultiplier = dc.document.getLong("tapMultiplier")
                        currency = dc.document.getLong("currency")
                        timesSavedManually = dc.document.getLong("timesSavedManually")
                        timesSavedByApp = dc.document.getLong("timesSavedByApp")
                        upgrades = dc.document.get("upgrades") as HashMap<String, HashMap<String, Any>>?

                        //add them to the viewModel
                        viewModel.i_timesTapped.value = timesTapped!!.toInt()
                        viewModel.i_tapMultiplier.value = tapMultiplier!!.toInt()
                        viewModel.i_currency.value = currency!!.toInt()
                        viewModel.i_timesSavedManually.value = timesSavedManually!!.toInt()
                        viewModel.i_timesSavedByApp.value = timesSavedByApp!!.toInt()
                        viewModel.i_upgrades.value = upgrades
                    }

                    DocumentChange.Type.REMOVED -> { //si se modifica en tiempo real; cambios
                        //rellenar en caso de necesidad
                    }

                    DocumentChange.Type.MODIFIED -> { //si se modifica en tiempo real; cambios
                        //hacer lo mismo que en added, de ser necesario; pero que el id sea el mismo
                    }
                }
            }
        }
    }

    private fun setSplashy(){
        Splashy(activity!!)
            .setLogo(R.drawable.blue_thunder)
            .setTitle("")//.setTitleColor(R.color.colorPrimaryDark)
            .setSubTitle("Loading your data...!").setSubTitleColor(R.color.colorPrimaryDark)
            //.showProgress(true).setProgressColor(R.color.colorPrimary)
            .setBackgroundResource(R.color.usuallyblack)
            .setAnimation(Splashy.Animation.GLOW_LOGO, 1500)
            //.setAnimation(Splashy.Animation.SLIDE_IN_LEFT_RIGHT, 1500)
            .setFullScreen(true)
            .setTime(3000)
            .show()
    }


    //--------------overrides

    override fun onDestroy() {
        super.onDestroy()

//        if(isUserLogged()) { //only if user logged
//            viewModel.i_timesSavedByApp.value = viewModel.timesSavedByApp.value!! + 1
//            saveDataToFB() //save on destroy, so if they exit, at least you save their things before this happening. TODO if you kill the app by "swiping" it doesn't seem to work.
//        }

    }

}

// know whether there's a logged user or not
fun isUserLogged(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
}