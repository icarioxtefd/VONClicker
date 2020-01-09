package org.ieselcaminas.luisdaniel.proyectosinnombre.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import org.ieselcaminas.luisdaniel.proyectosinnombre.R
import org.ieselcaminas.luisdaniel.proyectosinnombre.databinding.LoginFragLayoutBinding

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





        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
