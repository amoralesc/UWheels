package com.abmodel.uwheels.ui.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.data.model.Ride

@BindingAdapter("searchedRideList")
fun bindRecyclerView(
	recyclerView: RecyclerView,
	data: List<Ride>
) {
	val adapter = recyclerView.adapter as SearchedRideItemAdapter
	adapter.submitList(data)
}
