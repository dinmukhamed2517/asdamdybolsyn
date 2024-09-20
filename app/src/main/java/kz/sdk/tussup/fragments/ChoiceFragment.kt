package kz.sdk.tussup.fragments

import androidx.navigation.fragment.findNavController
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentChoiceBinding

class ChoiceFragment:BaseFragment<FragmentChoiceBinding>(FragmentChoiceBinding::inflate) {
    override var showBottomNavigation: Boolean = false

    override fun onBindView() {
        super.onBindView()
        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_choiceFragment_to_loginRestaurantFragment)
        }
        binding.registerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_choiceFragment_to_chooseAddressFragment)

        }
    }

}