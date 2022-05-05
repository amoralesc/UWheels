package com.abmodel.uwheels.ui.shared.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentSignUpBinding
import com.abmodel.uwheels.ui.driver.apply.BecomeDriverFragment

class SignUpFragment : Fragment() {

	companion object {
		const val TAG = "SignUpFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentSignUpBinding? = null
	private val binding get() = _binding!!

	private val signUpViewModel: SignUpViewModel by viewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentSignUpBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			signUp.setOnClickListener {
				signUpViewModel.signUp(
					name.text.toString(),
					lastName.text.toString(),
					phoneNumber.text.toString(),
					email.text.toString(),
					password.text.toString(),
					passwordAgain.text.toString()
				)
			}
		}

		signUpViewModel.signUpResult.observe(viewLifecycleOwner) { signUpResult ->
			signUpResult ?: return@observe
			signUpResult.error?.let {
				showSignUpFailed(it)
			}
			if (signUpResult.success) {
				goToBecomeDriverScreen()
			}
		}
	}

	private fun showSignUpFailed(@StringRes errorString: Int) {
		Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
	}

	private fun goToBecomeDriverScreen() {
		findNavController().navigate(
			R.id.action_signUpFragment_to_becomeDriverFragment
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
