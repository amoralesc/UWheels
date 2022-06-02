package com.abmodel.uwheels.ui.shared.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.databinding.FragmentProfileDetailsBinding
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.shared.data.SharedViewModelFactory
import com.bumptech.glide.Glide

class ProfileDetailsFragment : Fragment() {

	// Binding objects to access the view elements
	private var _binding: FragmentProfileDetailsBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels {
		SharedViewModelFactory(requireActivity().application)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentProfileDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			editProfile.setOnClickListener {
				onEditProfilePressed()
			}
			logout.setOnClickListener {
				onLogoutPressed()
			}

			FirebaseAuthRepository.getInstance().getLoggedInUser().let { user ->
				email.text = user.email
				name.text = user.name
				lastName.text = user.lastName
				phoneNumber.text = user.phone
				isDriver.isChecked = user.isDriver

				// Set the profile photo
				if (user.photoUrl != null) {
					Glide.with(requireContext())
						.load(user.photoUrl)
						.placeholder(R.drawable.ic_account_circle)
						.error(R.drawable.ic_account_circle)
						.into(profilePhoto)
				}
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun onEditProfilePressed() {
		findNavController().navigate(
			R.id.action_profileDetailsFragment_to_editProfileFragment
		)
	}

	private fun onLogoutPressed() {
		sharedViewModel.stopChatsUpdates()
		sharedViewModel.stopNotificationsUpdates()
		sharedViewModel.stopUserRidesUpdates()

		FirebaseAuthRepository.getInstance().logout()
		findNavController().navigate(
			R.id.action_profileDetailsFragment_to_loginFragment
		)
	}
}