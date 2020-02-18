package org.vono.luisdtefd.vonclicker.info

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import org.vono.luisdtefd.vonclicker.R
import org.vono.luisdtefd.vonclicker.databinding.InfoFragLayoutBinding

import org.vono.luisdtefd.vonclicker.gameMain.GameHomeViewModel


class InfoFrag : Fragment() {

    companion object {
        fun newInstance() = InfoFrag()
    }

    private lateinit var binding: InfoFragLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //get & disable the drawer
        var drawer = activity!!.findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.info_frag_layout, container, false)

        //set LCO
        binding.lifecycleOwner = this


        //set the viewModel and everything
        val viewModel = activity?.run {
            ViewModelProviders.of(this).get(GameHomeViewModel::class.java)
        }

        //ref the binding's var
        binding.viewModelFromGH = viewModel


        binding.fabGoBack.setOnClickListener{
            this.findNavController().navigate(InfoFragDirections.actionInfoFragToGameHomeFrag())
        }


        return binding.root
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//
//    }

}
