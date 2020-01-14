package org.vono.luisdtefd.vonclicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.vono.luisdtefd.vonclicker.ui.main.MainFrag

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrag, MainFrag.newInstance())
                .commitNow()
        }
    }

}
