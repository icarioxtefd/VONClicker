package org.vono.luisdtefd.vonclicker.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import org.vono.luisdtefd.vonclicker.databinding.LoginFragLayoutBinding
import org.vono.luisdtefd.vonclicker.R


class LoginFrag : Fragment() {

    companion object {
        fun newInstance() = LoginFrag()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {

        //ref the binding
        val binding: LoginFragLayoutBinding = DataBindingUtil.inflate(
            inflater, R.layout.login_frag_layout, container, false)


        //set LCO
        binding.lifecycleOwner = this



        //login things--------------------------------------------------
        //create a popUp menu
        val popUp = PopupMenu(context, binding.buttonLogin)

        //inflate the menu layout
        popUp.menuInflater.inflate(R.menu.popup_login_menu, popUp.menu)

        //set click listener for it
        popUp.setOnMenuItemClickListener {
            //get (inlined) it's id
            when (it!!.itemId){
                R.id.google_login -> {

                }

                R.id.email_login -> {

                }
            }
            //still don't know why is this necessary but just addint it lol
            true
        }

        //so, when pressing the login button...
        binding.buttonLogin.setOnClickListener{
            popUp.show()    //show the popUp
        }
        //---------------------------------------------------------------

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
