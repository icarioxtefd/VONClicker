package org.ieselcaminas.luisdaniel.proyectosinnombre

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    val splash_time : Long = 2000 // 3000 = 1 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            startActivity(Intent(this,MainActivity::class.java))

            // close this activity
            finish()

            //change how it's seen the transition
            overridePendingTransition(R.anim.fade_out_fade_in, R.anim.fade_in_fade_out)
        }, splash_time)

    }
}
