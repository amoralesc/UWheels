package com.abmodel.uwheels.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.databinding.HostedRideListItemBinding
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime

class HostedRideItemAdapter(
	private val onItemClicked: (Ride) -> Unit
) : ListAdapter<Ride, HostedRideItemAdapter.HostedRideItemViewHolder>(DiffCallback) {

	class HostedRideItemViewHolder(
		private var binding: HostedRideListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(ride: Ride) {
			binding.apply {
				wheelsType.text = ride.wheelsType
				source.text = ride.source.mainText
				destination.text = ride.destination.mainText
				capacity.text = capacity.context.getString(
					R.string.hosted_ride_capacity,
					ride.currentCapacity,
					ride.totalCapacity
				)
				requests.text = requests.context.getString(
					R.string.hosted_ride_requests,
					ride.requests.size
				)

				ride.date.apply {
					date.text =
						date.context.getString(
							R.string.ride_date,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
				}
				ride.creationDate.apply {
					info.text =
						info.context.getString(
							R.string.hosted_ride_info,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
				}
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): HostedRideItemViewHolder {
		return HostedRideItemViewHolder(
			HostedRideListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: HostedRideItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.itemView.setOnClickListener {
			onItemClicked(current)
		}
		holder.bind(current)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<Ride>() {
			override fun areItemsTheSame(oldItem: Ride, newItem: Ride): Boolean {
				return oldItem.id == newItem.id
			}

			override fun areContentsTheSame(
				oldItem: Ride,
				newItem: Ride
			): Boolean {
				return oldItem == newItem
			}
		}
	}
}
