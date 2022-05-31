package com.abmodel.uwheels.ui.driver.apply.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.data.model.UploadedFile

@BindingAdapter("uploadedFileList")
fun bindRecyclerView(
	recyclerView: RecyclerView,
	data: List<UploadedFile>?
) {
	val adapter = recyclerView.adapter as UploadedFileItemAdapter
	adapter.submitList(data)
}
