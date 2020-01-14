package org.vono.luisdtefd.vonclicker.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import org.vono.luisdtefd.vonclicker.databinding.LoginFragLayoutBinding
import org.vono.luisdtefd.vonclicker.R

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_frag_layout.view.*


class LoginFrag : Fragment() {

    companion object {
        fun newInstance() = LoginFrag()

        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private val viewModel by viewModels<LoginViewModel>()
    //private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginFragLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {

        //ref the binding
        binding = DataBindingUtil.inflate( inflater, R.layout.login_frag_layout, container, false)


        //set LCO
        binding.lifecycleOwner = this

        //todo preguntar:
        //por qué no se ven los iconos en el menú
        //se podría hacer en plan que se vea centrado y con el fondo oscuro, o sea, que no fuese un desplegable?
        //esta cosa va en el viewmodel???
        //cómo quitar lo de arriba "by viewmodels" y usar el onActivityCreated instead???
        //NO VA?? Aunque t registras y demás bien, no cambia luego nada en lo del observerAuthState

        //login things-------------------------------------
        binding.buttonLogin.setOnClickListener{
            launchSignInFlow()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
                            "${FirebaseAuth.getInstance().currentUser?.displayName}!"
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
     * If there is a logged in user: (1) show a logout button and (2) display their name.
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        val factToDisplay = viewModel.getFactToDisplay(requireContext())

        //  Use the authenticationState variable from LoginViewModel to update the UI
        //  accordingly.
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.buttonPlayAG.visibility = View.GONE
                    binding.buttonLogin.text = "Log out"
                    //change to log out
                    binding.buttonLogin.setOnClickListener {
                        // TODO implement logging out user in next step
                    }

                    // If the user is logged in,
                    // you can customize the welcome message they see by
                    // utilizing the getFactWithPersonalization() function provided
                    binding.welcomeText.text = getFactWithPersonalization(factToDisplay)

                }
                else -> {
                    // if there is no logged-in user,
                    // auth_button should display Login and
                    // launch the sign in screen when clicked.
                    binding.buttonLogin.text = "Log in"
                    binding.buttonLogin.setOnClickListener { launchSignInFlow() }
                    binding.welcomeText.text = factToDisplay
                }
            }
        })


        //   If there is a logged-in user, authButton should display Logout. If the
        //   user is logged in, you can customize the welcome message by utilizing
        //   getFactWithPersonalition(). I



        //  If there is no logged in user, authButton should display Login and launch the sign
        //  in screen when clicked. There should also be no personalization of the message
        //  displayed.


    }


    private fun getFactWithPersonalization(fact: String): String {
        return String.format(
            resources.getString(
                R.string.welcome_message_authed,
                FirebaseAuth.getInstance().currentUser?.displayName,
                Character.toLowerCase(fact[0]) + fact.substring(1)
            )
        )
    }

}
