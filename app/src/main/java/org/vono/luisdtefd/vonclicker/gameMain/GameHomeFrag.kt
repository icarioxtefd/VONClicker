package org.vono.luisdtefd.vonclicker.gameMain

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

import org.vono.luisdtefd.vonclicker.databinding.GameHomeFragmentBinding
import org.vono.luisdtefd.vonclicker.R
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton

import org.vono.luisdtefd.vonclicker.MainActivity
import org.vono.luisdtefd.vonclicker.login.getCurrentUsernameString
import org.vono.luisdtefd.vonclicker.login.getUserUid

import java.util.HashMap




class GameHomeFrag : Fragment() {

    companion object {
        fun newInstance() = GameHomeFrag()
    }

    private lateinit var viewModel: GameHomeViewModel
    private lateinit var binding: GameHomeFragmentBinding

    //ref the CF database and the accounts doc--------
    private val db = FirebaseFirestore.getInstance()
    lateinit var collRef: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //get & enable the drawer
        var drawer = activity!!.findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_home_fragment, container, false)


        //set LCO
        binding.lifecycleOwner = this


        //set the viewModel and everything
        viewModel = ViewModelProviders.of(this).get(GameHomeViewModel::class.java)


        //check whether you got here as a guest or as an alr registered user.
        if (FirebaseAuth.getInstance().currentUser != null) { //if there's a user, gotta pull all their saved data, and make few changes
            val args = GameHomeFragArgs.fromBundle(arguments!!)
            if (args.firstTimeLog) {
                Log.i("GameHomeFrag", "Trying to fetch data from FBCF...")
                loadDataFromFB()
            }
            else
                Log.i("GameHomeFrag", "Bro it's their first time, I'm not loading 0s")
        } else
        //user is a guest, so don't do anything, just create a new game without loading anything



        //set the listener to the image
        binding.imageToTap.setOnClickListener {
            // Toast.makeText(context, "Tap", Toast.LENGTH_SHORT).show()
            viewModel.i_timesTapped.value = viewModel.timesTapped.value!! + 1
            viewModel.i_currency.value = viewModel.currency.value!! + 1
            //Toast.makeText(context, "Tapped, tapped times total: " + viewModel.i_timesTapped.value, Toast.LENGTH_SHORT).show()
        }

        //observer for the viewText
        viewModel.currency.observe(this, Observer {
            it?.let {
                binding.textViewCurrency.text = "Currency: " + viewModel.currency.value.toString()
            }
        })


        //OnClick for the drawer's items
        (activity as MainActivity).navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_electrify -> {

                }
                R.id.menu_directCurrent -> {

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

                            //go to the info frag
                            this.findNavController().navigate(GameHomeFragDirections.actionGameHomeFragToInfoFrag())
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
                                //AuthUI.getInstance().signOut(requireContext())
                                FirebaseAuth.getInstance().signOut()
                            }
                            //and then gets to the main frag again
                            this.findNavController().popBackStack()
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



//            .get().addOnSuccessListener { documentSnapshot ->
//            val timesTapped = documentSnapshot.getLong("timesTapped") //firestore doesn't have getInt() so well, let's hope this will do
//            val tapMultiplier = documentSnapshot.getLong("tapMultiplier")
//            val currency = documentSnapshot.getLong("currency")
//            val timesSavedManually = documentSnapshot.getLong("timesSavedManually")
//            val timesSavedByApp = documentSnapshot.getLong("timesSavedByApp")
//            val upgrades = documentSnapshot.get("upgrades") as? HashMap<String, HashMap<String, Any>>
//
//            viewModel.i_timesTapped.value = timesTapped!!.toInt()
//            viewModel.i_tapMultiplier.value = tapMultiplier!!.toInt()
//            viewModel.i_currency.value = currency!!.toInt()
//            viewModel.i_timesSavedManually.value = timesSavedManually!!.toInt()
//            viewModel.i_timesSavedByApp.value = timesSavedByApp!!.toInt()
//            viewModel.i_upgrades.value = upgrades

        }
    }

    //--------------overrides


    override fun onDestroy() {
        super.onDestroy()

        if(isUserLogged()) { //only if user logged
            viewModel.i_timesSavedByApp.value = viewModel.timesSavedByApp.value!! + 1
            saveDataToFB() //save on destroy, so if they exit, at least you save their things before this happening. TODO if you kill the app by "swiping" it doesn't seem to work.
        }

    }

}

// know whether there's a logged user or not
fun isUserLogged(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
}