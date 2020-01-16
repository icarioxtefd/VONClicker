package org.vono.luisdtefd.vonclicker.gameMain

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import org.vono.luisdtefd.vonclicker.R

import kotlinx.android.synthetic.main.game_home_frag_drawer.drawer_layout

import org.vono.luisdtefd.vonclicker.databinding.GameHomeFragmentBinding

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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameHomeViewModel::class.java)

        //enable the drawer
        //drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

    }

}
