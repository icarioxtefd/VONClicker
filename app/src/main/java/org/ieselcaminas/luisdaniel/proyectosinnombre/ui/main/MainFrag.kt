package org.ieselcaminas.luisdaniel.proyectosinnombre.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.ieselcaminas.luisdaniel.proyectosinnombre.R
import org.ieselcaminas.luisdaniel.proyectosinnombre.databinding.MainFragLayoutBinding

class MainFrag : Fragment() {

    companion object {
        fun newInstance() = MainFrag()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {

        //ref the binding
        val binding: MainFragLayoutBinding = DataBindingUtil.inflate(
            inflater, R.layout.main_frag_layout, container, false)


        //set LCO
        binding.lifecycleOwner = this



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
