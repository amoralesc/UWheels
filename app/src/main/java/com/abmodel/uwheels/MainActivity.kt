package com.abmodel.uwheels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

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

		// Retrieve NavController from the NavHostFragment
		val navHostFragment = supportFragmentManager
			.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		navController = navHostFragment.navController


	}
}
