package com.abmodel.uwheels.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.Message
import com.abmodel.uwheels.databinding.MessageListItemBinding

class MessageItemAdapter(
	private val userId: String
) : ListAdapter<Message, MessageItemAdapter.MessageItemViewHolder>(DiffCallback) {

	class MessageItemViewHolder(
		private var binding: MessageListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message, userId: String) {

			binding.apply {
				name.text = message.name
				this.message.text = message.message
				date.text = message.date

				if (userId == message.uid) {
					// Set backgorund color of card
					card.setCardBackgroundColor(
						binding.root.context.getColor(
							R.color.color_primary
						)
					)

					card.rootView.setPadding(100, 0, 0, 0)
				} else {
					card.rootView.setPadding(0, 0, 100, 0)
				}
			}

		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): MessageItemViewHolder {
		return MessageItemViewHolder(
			MessageListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: MessageItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.bind(current, userId)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<Message>() {
			override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
				return oldItem.uid == newItem.uid && oldItem.name == newItem.name &&
						oldItem.message == newItem.message && oldItem.date == newItem.date
			}

			override fun areContentsTheSame(
				oldItem: Message,
				newItem: Message
			): Boolean {
				return oldItem == newItem
			}
		}
	}
}
