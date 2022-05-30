package com.abmodel.uwheels.ui.shared.search.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.data.model.CustomAddress

@BindingAdapter("addressList")
fun bindRecyclerView(
	recyclerView: RecyclerView,
	data: List<CustomAddress>?
) {
	val adapter = recyclerView.adapter as AddressItemAdapter
	adapter.submitList(data)
}
