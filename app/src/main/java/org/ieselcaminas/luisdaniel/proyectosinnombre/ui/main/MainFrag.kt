package org.ieselcaminas.luisdaniel.proyectosinnombre.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import org.ieselcaminas.luisdaniel.proyectosinnombre.R
import org.ieselcaminas.luisdaniel.proyectosinnombre.databinding.MainFragLayoutBinding
import java.util.*
import kotlin.concurrent.schedule

class MainFrag : Fragment() {

    companion object {
        fun newInstance() = MainFrag()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {

        //ref the binding
        val binding: MainFragLayoutBinding = DataBindingUtil.inflate(
            inflater, R.layout.main_frag_layout, container, false)

        //set LCO
        binding.lifecycleOwner = this

        //navigating to login frag
        binding.botonJugar.setOnClickListener{
            this.findNavController().navigate(MainFragDirections.actionMainFragToLoginFrag())
        }

        return binding.root
    }

    //no necessary view model since the code in here in kind of simple
    //and won't necessarily get any better by adding one anyways
}
