package com.abmodel.uwheels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.abmodel.uwheels.util.CHANNEL_ID

/**
 * Single activity application that uses a NavHostFragment
 * to support navigation between fragments.
 * Has action bar with up navigation button. (Back button)
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

	private lateinit var navController: NavController


	override fun onCreate(savedInstanceState: Bundle?) {
		// Install the splash screen
		// TODO: The splash screen can be further customized
		// to be shown while the app is loading data.
		// val splashScreen = installSplashScreen()

		super.onCreate(savedInstanceState)
		createNotificationChannel()

		// Retrieve NavController from the NavHostFragment
		val navHostFragment = supportFragmentManager
			.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		navController = navHostFragment.navController
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = getString(R.string.channel_name)
			val descriptionText = getString(R.string.channel_description)
			val importance = NotificationManager.IMPORTANCE_HIGH
			val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
				description = descriptionText
			}
			// Register the channel with the system
			val notificationManager: NotificationManager =
				getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channel)
		}
	}
}
