package com.abmodel.uwheels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController

/**
 * Single activity application that uses a NavHostFragment
 * to support navigation between fragments.
 * Has action bar with up navigation button. (Back button)
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

	private lateinit var navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Retrieve NavController from the NavHostFragment
		val navHostFragment = supportFragmentManager
			.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		navController = navHostFragment.navController

		// Set up the action bar for use with the NavController
		setupActionBarWithNavController(this, navController)
	}

	/**
	 * Handle Up navigation from the action bar.
	 */
	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp() || super.onSupportNavigateUp()
	}
}
