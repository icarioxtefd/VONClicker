package org.vono.luisdtefd.vonclicker.info

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.vono.luisdtefd.vonclicker.R
import org.vono.luisdtefd.vonclicker.databinding.InfoFragLayoutBinding

import org.vono.luisdtefd.vonclicker.gameMain.GameHomeViewModel


class InfoFrag : Fragment() {

    companion object {
        fun newInstance() = InfoFrag()
    }

    //lateinit
    private lateinit var binding: InfoFragLayoutBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ref the navController
        navController = this.findNavController()

        //get & disable the drawer
        var drawer = activity!!.findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.info_frag_layout, container, false)

        //set LCO
        binding.lifecycleOwner = this

        //get the parcelable from GHF and add the values to the textViews


        //change the behaviour when user presses back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { navController.popBackStack(R.id.gameHomeFrag, false) }

        return binding.root
    }
}
