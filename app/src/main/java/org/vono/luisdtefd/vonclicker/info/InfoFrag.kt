package org.vono.luisdtefd.vonclicker.info

import android.annotation.SuppressLint
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

class InfoFrag : Fragment() {

    companion object {
        fun newInstance() = InfoFrag()
    }

    //lateinit
    private lateinit var binding: InfoFragLayoutBinding
    private lateinit var navController: NavController

    @SuppressLint("SetTextI18n") //pls let me concatenate strings with values thank you
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get the data from the safeargs passed from the GHF
        val args = InfoFragArgs.fromBundle(requireArguments())

        //ref the navController
        navController = this.findNavController()

        //get & disable the drawer
        var drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.info_frag_layout, container, false)

        //set LCO
        binding.lifecycleOwner = this

        //add the values to the textViews
        binding.textViewCurrency.text = "Actual Currency: " + args.currency.toString()
        binding.textViewTimesTapped.text = "Total Times Tapped: " + args.timesTapped.toString()
        binding.textViewTapMultiplier.text = "Active Tap Multiplier: " + args.tapMultiplier.toString()
        binding.textViewTimesSavedManually.text = "Times that you saved manually: " + args.timesSavedManually.toString()
        binding.textViewTimesSavedByApp.text = "Times that the app saves for yourself: " + args.timesSavedByApp.toString()
        binding.textViewElectrify.text = "Electrify Upgrade: Bought -> " + args.electrifyBought + " | Level: " + args.electrifyLevel.toString()
        binding.textViewDirectCurrent.text = "DC Upgrade: Bought -> " + args.directCurrentBought + " | Level: " + args.directCurrentLevel.toString()

        //change the behaviour when user presses back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { navController.popBackStack(R.id.gameHomeFrag, false) }

        return binding.root
    }
}
