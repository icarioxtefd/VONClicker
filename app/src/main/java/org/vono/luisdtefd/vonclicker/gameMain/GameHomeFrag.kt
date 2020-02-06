package org.vono.luisdtefd.vonclicker.gameMain

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import org.vono.luisdtefd.vonclicker.databinding.GameHomeFragmentBinding
import org.vono.luisdtefd.vonclicker.R
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import org.vono.luisdtefd.vonclicker.login.LoginFrag
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

        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_home_fragment, container, false)


        //set LCO
        binding.lifecycleOwner = this



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) { //everything in the code using the viewModel should go here
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameHomeViewModel::class.java)

        //enable the drawer
        activity!!.findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        //set the listener to the image
        binding.imageToTap.setOnClickListener {
            // Toast.makeText(context, "Tap", Toast.LENGTH_SHORT).show()
            viewModel.i_timesTapped.value = viewModel.i_timesTapped.value!! + 1
            viewModel.i_currency.value = viewModel.i_currency.value!! + 1
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

        //check whether you got here as a guest or as an alr registered user
        if(FirebaseAuth.getInstance().currentUser != null){ //if there's a user, gotta pull all their saved data, and make few changes
            // todo modify the loading code in order to load everything (not only currency and tapped times)
            loadDataFromFB()
        }
        else{
            //user is a guest, so don't do anything, just create a new game without loading anything
        }

    }

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

                    // todo modify the saving code in order to save everything (not only currency and tapped times)
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
                            Toast.makeText(
                                context,
                                "Clicked $index",
                                Toast.LENGTH_SHORT
                            ).show()
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
                            Toast.makeText(
                                context,
                                "Clicked $index",
                                Toast.LENGTH_SHORT
                            ).show()

                            //logs out;
                            AuthUI.getInstance().signOut(requireContext())
                            //todo saves again, just in case u didn't save, but only if you're logged, use the string, not the user
                            //todo the idea is making a function that checks AND saves, so I could use it in both places, here and in the save button

                            //and then gets to the main frag again
                            this.findNavController()
                                .navigate(GameHomeFragDirections.actionGameHomeFragToMainFrag())
                        }
                }

                4 -> {
                    bmbButton = TextInsideCircleButton.Builder()
                        .normalImageRes(R.drawable.settings).imagePadding(Rect(20, 20, 20, 20))
                        .normalText("Config").textPadding(Rect(0, 8, 0, 0)).textSize(11)
                        .normalColor(R.color.usuallygrey).shadowColor(R.color.usuallyblack)
                        .listener { index ->
                            // When the boom-button corresponding this builder is clicked.
                            Toast.makeText(
                                context,
                                "Clicked $index",
                                Toast.LENGTH_SHORT
                            ).show()
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
                        userPlayedData["timesTapped"] = viewModel.currency.value!!.toInt()
                        userPlayedData["currency"] = viewModel.currency.value!!.toInt()

                        //and add it
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
            val currency =
                documentSnapshot.getLong("currency") //firestore doesn't have getInt() so well, let's hope this will do
            val timesTapped = documentSnapshot.getLong("timesTapped")
            viewModel.i_currency.value = currency!!.toInt()
            viewModel.i_timesTapped.value = timesTapped!!.toInt()
        }
    }


}



// know whether there's a logged user or not
fun isUserLogged(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
}