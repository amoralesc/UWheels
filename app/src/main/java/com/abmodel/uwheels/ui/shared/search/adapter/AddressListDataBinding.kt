package com.abmodel.uwheels.ui.shared.search.adapter

import android.location.Address
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("addressList")
fun bindRecyclerView(
	recyclerView: RecyclerView,
	data: List<Address>?
) {
	val adapter = recyclerView.adapter as AddressItemAdapter
	adapter.submitList(data)
}
