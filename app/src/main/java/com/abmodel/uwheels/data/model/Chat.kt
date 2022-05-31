package com.abmodel.uwheels.data.model

data class Chat(
	val id: String,
	val name: String,
	val date: CustomDate,
	val source: CustomAddress,
	val destination: CustomAddress,
)