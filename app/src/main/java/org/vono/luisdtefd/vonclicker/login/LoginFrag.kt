package org.vono.luisdtefd.vonclicker.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.vono.luisdtefd.vonclicker.databinding.LoginFragLayoutBinding
import org.vono.luisdtefd.vonclicker.R

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.local.LocalViewChanges
import java.util.HashMap


class LoginFrag : Fragment() {

    companion object {
        fun newInstance() = LoginFrag()

        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private val viewModel by viewModels<LoginViewModel>()
    var listenerExistingUser: ListenerRegistration? = null
    //private lateinit var viewModel: LoginViewModel

    private lateinit var binding: LoginFragLayoutBinding
    private lateinit var alertDialog: AlertDialog

    //ref the CF database and the accounts doc--------
    private val db = FirebaseFirestore.getInstance()
    lateinit var docRef: CollectionReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {

        //ref the binding
        binding = DataBindingUtil.inflate( inflater, R.layout.login_frag_layout, container, false)


        //set LCO
        binding.lifecycleOwner = this


        return binding.root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //disable the drawer, since here it's not its place
        activity!!.findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)



        //todo create alert dialog so we don't need to create it when user clicks. Whether they do it or not, we'll dispose of it anyways with the transition-------------
        alertDialog = activity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("Yes"){ dialog, id ->
                    goToGameHome()
                }
                setNegativeButton("No"){ dialog, id ->
                    //do nothing
                }
            }
            // Set other dialog properties

            // Create the AlertDialog
            builder.create()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------

        //place the observer to see when user is auth or not
        observeAuthenticationState()

        //viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                Log.i(
                    TAG,
                    "Successfully signed in user " +
                            "${getCurrentUsernameString()}!"
                )
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user, goes to the game frag
     * If there is no logged in user, shows two l
     */
    private fun observeAuthenticationState() {
        //  Use the authenticationState variable from LoginViewModel to update the UI
        //  accordingly.
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {

                LoginViewModel.AuthenticationState.AUTHENTICATED -> { //when user is logged

                    //if it's their first time logging in, create their credentials in firebase
                    createUserInCFDatabase() //this also checks whether they alr have an acc or not

                    //finally, go to the game screen
                    goToGameHome()
                }

                else -> { //when user is NOT logged, since I didn't implement another enum state
                    binding.buttonLogin.setOnClickListener { launchSignInFlow() } // go with login

                    //go without login
                    binding.buttonPlayAG.setOnClickListener {
                        alertDialog.show()
                    }

                }


            }
        })
    }

    private fun createUserInCFDatabase() {
        db.collection("accounts").whereEqualTo("id", getUserUid()).
            addSnapshotListener{ snapshots, e ->

                Log.i(TAG,
                    "Trying to create user " + "${getCurrentUsernameString()} with uid: " + getUserUid()
                )

                if(snapshots!!.isEmpty){ //if there's no acc created yet
                    //make it

                    Log.i(TAG,
                        "User doesn't exist yet, creating user " + "${getCurrentUsernameString()} with uid: " + getUserUid()
                    )

                    val data = HashMap<String, Any>()
                    data["username"] = getCurrentUsernameString()
                    data["id"] = getUserUid()

                    //add the new doc to the collection
                    db.collection("accounts").document(getCurrentUsernameString()).set(data)

                    //also, make the played_data collection with its docs
                    var userPlayedData = HashMap<String, Any>()
                    userPlayedData["timesTapped"] = 0
                    userPlayedData["currency"] = 0
                    //and add it
                    db.collection("accounts").document(getCurrentUsernameString()).collection("played_data").document("generals").set(userPlayedData)

                    //we should also add the upgrades, but, ftm, TODO upgrades as another document inside played_data docs
                }
                else{ //meaning it alr exists
                    //so don't do anything
                    Log.i(TAG,
                        "User "+ "${getCurrentUsernameString()} with uid: " + getUserUid() + " already exists."
                    )
                }
        }
    }


    private fun goToGameHome() { // silly fun just for not having all this line all over the page
        this.findNavController().navigate(LoginFragDirections.actionLoginFragToGameHomeFrag())
    }

}

//xtra functions that I made myself in order to shorten calls

fun getCurrentUsernameString() = FirebaseAuth.getInstance().currentUser?.displayName.toString()

fun getUserUid() = FirebaseAuth.getInstance().currentUser!!.uid
