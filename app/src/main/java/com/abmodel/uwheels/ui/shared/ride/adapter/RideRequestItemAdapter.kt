package com.abmodel.uwheels.ui.shared.ride.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.RideRequest
import com.abmodel.uwheels.databinding.RideRequestListItemBinding
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime
import com.bumptech.glide.Glide

class RideRequestItemAdapter(
	private val onAcceptClicked: (RideRequest) -> Unit,
	private val onRejectClicked: (RideRequest) -> Unit
) : ListAdapter<RideRequest, RideRequestItemAdapter.RideRequestItemViewHolder>(DiffCallback) {

	class RideRequestItemViewHolder(
		private var binding: RideRequestListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(
			rideRequest: RideRequest,
			onAcceptClicked: (RideRequest) -> Unit,
			onRejectClicked: (RideRequest) -> Unit
		) {
			binding.apply {

				Glide
					.with(itemView.context)
					.load(rideRequest.user.photoUrl)
					.placeholder(R.drawable.ic_account_circle)
					.error(R.drawable.ic_account_circle)
					.into(profilePhoto)

				name.text = name.context.getString(
					R.string.full_name, rideRequest.user.name, rideRequest.user.lastName
				)
				rating.text = rideRequest.user.rating.value.toString()

				source.text = source.context.getString(
					R.string.ride_request_address,
					rideRequest.sourceDistance,
					rideRequest.source.mainText
				)
				destination.text = destination.context.getString(
					R.string.ride_request_address,
					rideRequest.destinationDistance,
					rideRequest.destination.mainText
				)

				rideRequest.sentDate.apply {
					info.text =
						info.context.getString(
							R.string.ride_request_info,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
				}
				rideRequest.date.apply {
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

				accept.setOnClickListener {
					onAcceptClicked(rideRequest)
				}
				reject.setOnClickListener {
					onRejectClicked(rideRequest)
				}
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): RideRequestItemViewHolder {
		return RideRequestItemViewHolder(
			RideRequestListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: RideRequestItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.bind(
			current,
			onAcceptClicked,
			onRejectClicked
		)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<RideRequest>() {
			override fun areItemsTheSame(oldItem: RideRequest, newItem: RideRequest): Boolean {
				return oldItem.user.uid == newItem.user.uid
			}

			override fun areContentsTheSame(
				oldItem: RideRequest,
				newItem: RideRequest
			): Boolean {
				return oldItem == newItem
			}
		}
	}
}
