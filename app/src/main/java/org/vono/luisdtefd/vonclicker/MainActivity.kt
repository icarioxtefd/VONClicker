package org.vono.luisdtefd.vonclicker

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.game_home_frag_drawer.*
import org.vono.luisdtefd.vonclicker.ui.main.MainFrag
import com.google.android.material.navigation.NavigationView



class MainActivity : AppCompatActivity() {

    //atributo; para poder usarlo en los frags
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_home_frag_drawer)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // no landscape
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrag, MainFrag.newInstance())
                .commitNow()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        navigationView = findViewById(R.id.nav_view)
        navigationView.itemIconTintList = null //for keeping the original colors of the icons

    }

    //silly fun in order to not make the user go back, since I don't want that lolol
    override fun onBackPressed() {
        //super.onBackPressed() nu-oh I'm not letting u go back
        Toast.makeText(applicationContext, "Nu-oh", Toast.LENGTH_LONG).show()
    }



}