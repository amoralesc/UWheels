package com.abmodel.uwheels.util

import com.abmodel.uwheels.data.model.CustomDate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Formats a given time in the form HH:MM a.m./p.m.
 *
 * @param hour Hour of the day (0-23)
 * @param minute Minute of the hour (0-59)
 * @return Formatted time string
 */
fun formatTime(hour: Int, minute: Int): String {
	// A.M.
	val amPm = if (hour < 12) "a.m." else "p.m."

	return String.format(
		"%02d:%02d %s",
		if (hour <= 12) hour else hour - 12,
		minute,
		amPm
	)
}

/**
 * Formats a date given in millis in the form dd/MM/YYYY.
 *
 * @param millis Date in millis from epoch
 * @return Formatted date string
 */
fun formatDateFromMillis(millis: Long): String {
	val calendar = Calendar
		.getInstance(TimeZone.getTimeZone("UTC"))
	calendar.timeInMillis = millis
	calendar.add(Calendar.DATE, 1)

	val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

	return formatter.format(calendar.time)
}

fun CustomDate.compareDates(other: CustomDate): Int {
	val calendar1 = Calendar.getInstance()
	calendar1.timeInMillis = this.millis!!
	calendar1.set(Calendar.HOUR_OF_DAY, this.hour!!)
	calendar1.set(Calendar.MINUTE, this.minute!!)

	val calendar2 = Calendar.getInstance()
	calendar2.timeInMillis = other.millis!!
	calendar2.set(Calendar.HOUR_OF_DAY, other.hour!!)
	calendar2.set(Calendar.MINUTE, other.minute!!)

	return calendar1.compareTo(calendar2)
}

fun getCurrentDateAsCustomDate(): CustomDate {
	val calendar = Calendar.getInstance()
	calendar.timeInMillis = System.currentTimeMillis()

	return CustomDate(
		calendar.timeInMillis - calendar.timeInMillis % (1000 * 60 * 60 * 24),
		calendar.get(Calendar.HOUR_OF_DAY),
		calendar.get(Calendar.MINUTE),
	)
}

fun CustomDate.difference(other: CustomDate): Long {
	val calendar1 = Calendar.getInstance()
	calendar1.timeInMillis = this.millis!!
	calendar1.set(Calendar.HOUR_OF_DAY, this.hour!!)
	calendar1.set(Calendar.MINUTE, this.minute!!)

	val calendar2 = Calendar.getInstance()
	calendar2.timeInMillis = other.millis!!
	calendar2.set(Calendar.HOUR_OF_DAY, other.hour!!)
	calendar2.set(Calendar.MINUTE, other.minute!!)

	return abs(calendar1.timeInMillis - calendar2.timeInMillis)
}