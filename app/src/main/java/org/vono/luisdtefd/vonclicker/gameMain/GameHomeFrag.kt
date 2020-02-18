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
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import org.vono.luisdtefd.vonclicker.databinding.GameHomeFragmentBinding
import org.vono.luisdtefd.vonclicker.R
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.rbddevs.splashy.Splashy

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
    lateinit var docRef: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //call splashy (library splash screen), but only if they are logged
        if(isUserLogged())
            setSplashy()

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
            Log.i("GameHomeFrag", "Trying to fetch data from FBCF...")
            loadDataFromFB()
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
                            if(isUserLogged()){
                                AuthUI.getInstance().signOut(requireContext())
                            }
                            //and then gets to the main frag again
                            this.findNavController().navigate(GameHomeFragDirections.actionGameHomeFragToMainFrag())
                            this.onDestroy() //destroy the fragment so the viewModel get's destroyed too; reason behind this is avoiding unlimited stackpiles of the same viewModels
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
        docRef =
            db.collection("accounts").document(getCurrentUsernameString()).collection("played_data")
                .document("generals")
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val timesTapped = documentSnapshot.getLong("timesTapped") //firestore doesn't have getInt() so well, let's hope this will do
            val tapMultiplier = documentSnapshot.getLong("tapMultiplier")
            val currency = documentSnapshot.getLong("currency")
            val timesSavedManually = documentSnapshot.getLong("timesSavedManually")
            val timesSavedByApp = documentSnapshot.getLong("timesSavedByApp")
            val upgrades = documentSnapshot.get("upgrades") as? HashMap<String, HashMap<String, Any>>

            viewModel.i_timesTapped.value = timesTapped!!.toInt()
            viewModel.i_tapMultiplier.value = tapMultiplier!!.toInt()
            viewModel.i_currency.value = currency!!.toInt()
            viewModel.i_timesSavedManually.value = timesSavedManually!!.toInt()
            viewModel.i_timesSavedByApp.value = timesSavedByApp!!.toInt()
            viewModel.i_upgrades.value = upgrades

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
            .setTime(4000)
            .show()
    }


    //--------------overrides


    override fun onDestroy() {
        super.onDestroy()

        if(isUserLogged()) { //only if user logged
            viewModel.i_timesSavedByApp.value = viewModel.timesSavedByApp.value!! + 1 //todo lo hace dos veces porque lo llamo desde el logout button
            saveDataToFB() //save on destroy, so if they exit, at least you save their things before this happening. TODO if you kill the app by "swiping" it doesn't seem to work.
        }

    }


}

// know whether there's a logged user or not
fun isUserLogged(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
}