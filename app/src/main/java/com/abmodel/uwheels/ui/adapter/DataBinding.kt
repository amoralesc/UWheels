package com.abmodel.uwheels.ui.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.data.model.*

@BindingAdapter("uploadedFileList")
fun bindUploadedFileRecyclerView(
	recyclerView: RecyclerView,
	data: List<UploadedFile>?
) {
	val adapter = recyclerView.adapter as UploadedFileItemAdapter
	adapter.submitList(data)
}

@BindingAdapter("addressList")
fun bindAddressRecyclerView(
	recyclerView: RecyclerView,
	data: List<CustomAddress>?
) {
	val adapter = recyclerView.adapter as AddressItemAdapter
	adapter.submitList(data)
}

@BindingAdapter("rideList")
fun bindRideRecyclerView(
	recyclerView: RecyclerView,
	data: List<Ride>?
) {
	val adapter = recyclerView.adapter as RideItemAdapter
	adapter.submitList(data)
}

@BindingAdapter("hostedRideList")
fun bindHostedRideRecyclerView(
	recyclerView: RecyclerView,
	data: List<Ride>?
) {
	val adapter = recyclerView.adapter as HostedRideItemAdapter
	adapter.submitList(data)
}

@BindingAdapter("searchedRideList")
fun bindSearchedRideRecyclerView(
	recyclerView: RecyclerView,
	data: List<Ride>?
) {
	val adapter = recyclerView.adapter as SearchedRideItemAdapter
	adapter.submitList(data)
}

@BindingAdapter("rideRequestList")
fun bindRideRequestRecyclerView(
	recyclerView: RecyclerView,
	data: List<RideRequest>?
) {
	val adapter = recyclerView.adapter as RideRequestItemAdapter
	adapter.submitList(data)
}

@BindingAdapter("chatList")
fun bindChatRecyclerView(
	recyclerView: RecyclerView,
	data: List<Chat>?
) {
	val adapter = recyclerView.adapter as ChatItemAdapter
	adapter.submitList(data)
}
