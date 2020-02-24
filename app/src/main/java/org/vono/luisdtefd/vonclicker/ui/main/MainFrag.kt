package org.vono.luisdtefd.vonclicker.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.main_frag_layout.*
import org.vono.luisdtefd.vonclicker.databinding.MainFragLayoutBinding
import tyrantgit.explosionfield.ExplosionField
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import org.vono.luisdtefd.vonclicker.R


class MainFrag : Fragment() {

    companion object {
        fun newInstance() = MainFrag()
    }

    lateinit var binding: MainFragLayoutBinding
    lateinit var explosionField: ExplosionField

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {

        //ref the binding
        binding  = DataBindingUtil.inflate( inflater, R.layout.main_frag_layout, container, false )

        //set LCO
        binding.lifecycleOwner = this

        //silly animation
        explosionField = ExplosionField.attach2Window(activity)

        //just for removing the default image
        binding.imageViewAnim.setImageDrawable(null)

        //navigating to login frag
        binding.botonJugar.setOnClickListener{
            explosionField.explode(botonJugar)
            this.findNavController().navigate(MainFragDirections.actionMainFragToLoginFrag())
        }

        //get the activity's things so the drawer is there
        activity!!.findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //need to do the animation here since onCreate() won't do
        binding.imageViewAnim.setBackgroundResource(R.drawable.main_frag_bg_animation)
        val anim = binding.imageViewAnim.background as AnimationDrawable
        anim.start()
    }

    //no necessary view model since the code in here in kind of simple
    //and won't necessarily get any better by adding one anyways
}
