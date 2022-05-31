package com.abmodel.uwheels.ui.driver.apply.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.UploadedFile
import com.abmodel.uwheels.databinding.UploadedFileListItemBinding

class UploadedFileItemAdapter(
	private val onItemClicked: (UploadedFile) -> Unit,
	private val onRemoveItemClicked: (UploadedFile) -> Unit
) : ListAdapter<UploadedFile, UploadedFileItemAdapter.UploadedFileItemViewHolder>(DiffCallback) {

	class UploadedFileItemViewHolder(
		private var binding: UploadedFileListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(
			uploadedFile: UploadedFile,
			onRemoveItemClicked: (UploadedFile) -> Unit
		) {
			binding.apply {
				if (uploadedFile.mimeType.contains("image")) {
					filePreview.setImageURI(uploadedFile.uri)
				} else {
					filePreview.setImageResource(R.drawable.ic_file)
				}
				fileName.text = uploadedFile.name

				remove.setOnClickListener {
					onRemoveItemClicked(uploadedFile)
				}
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): UploadedFileItemViewHolder {
		return UploadedFileItemViewHolder(
			UploadedFileListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: UploadedFileItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.itemView.setOnClickListener {
			onItemClicked(current)
		}
		holder.bind(current, onRemoveItemClicked)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<UploadedFile>() {
			override fun areItemsTheSame(oldItem: UploadedFile, newItem: UploadedFile): Boolean {
				return oldItem.uri == newItem.uri
			}

			override fun areContentsTheSame(
				oldItem: UploadedFile,
				newItem: UploadedFile
			): Boolean {
				return oldItem == newItem
			}
		}
	}
}
