package org.vono.luisdtefd.vonclicker.gameMain

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.nightonke.boommenu.BoomButtons.OnBMClickListener

import org.vono.luisdtefd.vonclicker.databinding.GameHomeFragmentBinding
import org.vono.luisdtefd.vonclicker.R
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton




class GameHomeFrag : Fragment() {

    companion object {
        fun newInstance() = GameHomeFrag()
    }

    private lateinit var viewModel: GameHomeViewModel
    private lateinit var binding: GameHomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //ref the binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_home_fragment, container, false)


        //set LCO
        binding.lifecycleOwner = this


        //boom buttons, aka fab but prettier-----------------------------------------------------------------------------------------------
        //first one + initialization
        var bmbButton = TextInsideCircleButton.Builder().normalImageRes(R.drawable.save).normalText("Save")
            .listener { index ->
                // When the boom-button corresponding this builder is clicked.
                Toast.makeText(
                    context,
                    "Clicked $index",
                    Toast.LENGTH_SHORT
                ).show()
            }

        for (i in 0 until binding.bmb.piecePlaceEnum.pieceNumber()) {
            when(i){
                1 -> {bmbButton = TextInsideCircleButton.Builder().normalImageRes(R.drawable.info).normalText("Info")
                    .listener { index ->
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(
                            context,
                            "Clicked $index",
                            Toast.LENGTH_SHORT
                        ).show()
                    }}

                2 -> {bmbButton = TextInsideCircleButton.Builder().normalImageRes(R.drawable.cancel)}

                3 -> {bmbButton = TextInsideCircleButton.Builder().normalImageRes(R.drawable.key).normalText("Log out")
                    .listener { index ->
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(
                            context,
                            "Clicked $index",
                            Toast.LENGTH_SHORT
                        ).show()

                        //logs out
                        AuthUI.getInstance().signOut(requireContext())
                        //todo saves

                        //and then gets to the main frag again
                        this.findNavController().navigate(GameHomeFragDirections.actionGameHomeFragToMainFrag())
                    }}

                4 -> {bmbButton = TextInsideCircleButton.Builder().normalImageRes(R.drawable.settings).normalText("Config")
                    .listener { index ->
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(
                            context,
                            "Clicked $index",
                            Toast.LENGTH_SHORT
                        ).show()
                    }}
            }
            binding.bmb.addBuilder(bmbButton)
        }
        //---------------------------------------------------------------------------------------------------------------------------------



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameHomeViewModel::class.java)

        //enable the drawer
        //drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

    }

}
