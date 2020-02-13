package org.vono.luisdtefd.vonclicker.gameMain

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import org.vono.luisdtefd.vonclicker.databinding.GameHomeFragmentBinding
import org.vono.luisdtefd.vonclicker.R
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.rbddevs.splashy.Splashy
import kotlinx.android.synthetic.main.game_home_frag_drawer.*
import org.vono.luisdtefd.vonclicker.login.LoginFrag
import org.vono.luisdtefd.vonclicker.login.getCurrentUsernameString
import org.vono.luisdtefd.vonclicker.login.getUserUid
import java.lang.Exception
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

        //call splashy (library splash screen)
        setSplashy()

        //enable the drawer
        activity!!.findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_home_fragment, container, false)


        //set LCO
        binding.lifecycleOwner = this


        //set the viewModel and everything
        viewModel = ViewModelProviders.of(this).get(GameHomeViewModel::class.java)


        Handler().postDelayed({  //make the loading wait 2 secs, so it has enough time to init the observers, save to FB, and load from FB if necessary. THIS IS A MUST, IT WILL CRASH OTHERWISE

            //check whether you got here as a guest or as an alr registered user.
            if(FirebaseAuth.getInstance().currentUser != null){ //if there's a user, gotta pull all their saved data, and make few changes
                Log.i("GameHomeFrag", "Trying to fetch data from FBCF...")
                loadDataFromFB()
            }
            else{
                //user is a guest or it's their first time logging in, so don't do anything, just create a new game without loading anything
            }

        }, 3000)


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

                    //Toast.makeText(context,"Clicked $index",Toast.LENGTH_SHORT).show()
                    Toast.makeText(context,"Saving...",Toast.LENGTH_SHORT).show()
                    Log.i("GameHomeFrag", "Saving data to FBCF...")

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
                            //Toast.makeText(context,"Clicked $index", Toast.LENGTH_SHORT).show()
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
                            //Toast.makeText(context, "Clicked $index", Toast.LENGTH_SHORT).show()

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
                            //Toast.makeText(context,"Clicked $index",Toast.LENGTH_SHORT).show()
                        }
                }
            }
            binding.bmb.addBuilder(bmbButton)
        }
        //---------------------------------------------------------------------------------------------------------------------------------


    }

    private fun saveDataToFB() {
        //add one saving, personal purposes.
        viewModel.i_timesSaved.value = viewModel.i_timesSaved.value!! + 1

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
                        userPlayedData["timesSaved"] = viewModel.timesSaved.value!!

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
            val timesSaved = documentSnapshot.getLong("timesSaved")
            val upgrades = documentSnapshot.get("upgrades") as? HashMap<String, HashMap<String, Any>>

            viewModel.i_timesTapped.value = timesTapped!!.toInt()
            viewModel.i_tapMultiplier.value = tapMultiplier!!.toInt()
            viewModel.i_currency.value = currency!!.toInt()
            viewModel.i_timesSaved.value = timesSaved!!.toInt()
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
            saveDataToFB() //save on destroy, so if they exit, at least you save their things before this happening. TODO if you kill the app by "swiping" it doesn't seem to work.
        }

    }

    //onClick method for the drawer items
//    override fun | this.nav_view. | onNavigationItemSelectedListener { menuItem ->
//        when (menuItem.itemId) {
//            R.id.menu_electrify -> {
//
//            }
//            R.id.menu_directCurrent -> {
//
//            }
//        }
//        true
//    }



}



// know whether there's a logged user or not
fun isUserLogged(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
}

