package org.vono.luisdtefd.vonclicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import kotlinx.android.synthetic.main.game_home_frag_drawer.*
import org.vono.luisdtefd.vonclicker.ui.main.MainFrag

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_home_frag_drawer)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrag, MainFrag.newInstance())
                .commitNow()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    //silly fun in order to not make the user go back, since I don't want that lolol
    override fun onBackPressed() {
        //super.onBackPressed() nu-oh I'm not letting u go back
        Toast.makeText(applicationContext, "Nu-oh", Toast.LENGTH_LONG).show()
    }

}