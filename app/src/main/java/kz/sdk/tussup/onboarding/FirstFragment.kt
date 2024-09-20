package kz.sdk.tussup.onboarding

import androidx.navigation.fragment.findNavController
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentFirstBinding

class FirstFragment:BaseFragment<FragmentFirstBinding>(FragmentFirstBinding::inflate){

    override var showBottomNavigation = false
    override fun onBindView() {
        super.onBindView()
        binding.skipBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_viewPagerFragment_to_homeFragment
            )
        }

    }
}