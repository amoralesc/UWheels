package com.abmodel.uwheels.util

import java.text.SimpleDateFormat
import java.util.*

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
