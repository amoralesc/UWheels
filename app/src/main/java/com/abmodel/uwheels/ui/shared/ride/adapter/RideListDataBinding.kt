package com.abmodel.uwheels.ui.shared.ride.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.model.RideRequest

@BindingAdapter("rideList")
fun bindRecyclerView(
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

@BindingAdapter("rideRequestList")
fun bindRideRequestRecyclerView(
	recyclerView: RecyclerView,
	data: List<RideRequest>?
) {
	val adapter = recyclerView.adapter as RideRequestItemAdapter
	adapter.submitList(data)
}
