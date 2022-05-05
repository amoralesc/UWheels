package com.abmodel.uwheels.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.abmodel.uwheels.R

class Sensor : AppCompatActivity(), SensorEventListener {
	/*private lateinit var mSensorManager : SensorManager
	private var mLight : Sensor ?= null
	private var mAccelerometer : Sensor?= null
	private var mGyroscope : Sensor ?= null
	private var resume = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_sensor)

		mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

		mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		print("accuracy changed")
	}*/

	override fun onSensorChanged(event: SensorEvent?) {
		/*if (event != null && resume) {
			if (event.sensor.type == Sensor.TYPE_LIGHT) {
				findViewById<TextView>(R.id.light).text = event.values[0].toString()
			}

			if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
				findViewById<TextView>(R.id.acc_X).text = event.values[0].toString()
				findViewById<TextView>(R.id.acc_Y).text = event.values[1].toString()
				findViewById<TextView>(R.id.acc_Z).text = event.values[2].toString()
			}

			if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
				findViewById<TextView>(R.id.gyro_x).text = event.values[0].toString()
				findViewById<TextView>(R.id.gyro_y).text = event.values[1].toString()
				findViewById<TextView>(R.id.gyro_z).text = event.values[2].toString()
			}
		}*/
	}

	override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
		//
	}

	/*override fun onResume() {
		super.onResume()
		mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL)
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
		mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL)
	}

	override fun onPause() {
		super.onPause()
		mSensorManager.unregisterListener(this)
	}

	fun continueReading(view: View) {
		this.resume = true
	}

	fun stopReading(view: View) {
		this.resume = false
	}
*/
}