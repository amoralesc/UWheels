package com.abmodel.uwheels.ui.shared.chat.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.data.model.Chat

@BindingAdapter("chatList")
fun bindRecyclerView(
	recyclerView: RecyclerView,
	data: List<Chat>?
) {
	val adapter = recyclerView.adapter as ChatItemAdapter
	adapter.submitList(data)
}
