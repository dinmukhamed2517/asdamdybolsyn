package kz.sdk.tussup.onboarding

import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentViewPagerBinding

class ViewPagerFragment :
    BaseFragment<FragmentViewPagerBinding>(FragmentViewPagerBinding::inflate) {

    override var showBottomNavigation: Boolean = false
    override fun onBindView() {
        super.onBindView()
        val fragmentList = arrayListOf(
            FirstFragment(),
            SecondFragment(),
            ThirdFragment()
        )
        val adapter =
            ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

    }

}