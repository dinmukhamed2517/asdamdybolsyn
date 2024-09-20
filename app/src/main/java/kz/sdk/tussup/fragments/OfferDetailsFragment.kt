package kz.sdk.tussup.fragments

import androidx.navigation.fragment.navArgs
import coil.load
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentOfferDetailsBinding



@AndroidEntryPoint
class OfferDetailsFragment : BaseFragment<FragmentOfferDetailsBinding>(FragmentOfferDetailsBinding::inflate) {

    private val args: OfferDetailsFragmentArgs by navArgs()

    override fun onBindView() {
        super.onBindView()

        val order = args.order

        binding.offerTitle.text = order.title
        binding.price.text = "${order.price} ₸"
        binding.pickUpTime.text = order.pickUpTimeFrom
        binding.placeTitle.text = order.title
        binding.address.text = order.pickUpAddress


        binding.backgroundImg.load(order.imageUrl){
            placeholder(R.drawable.placeholder
            )
        }

        binding.bookOfferBtn.setOnClickListener {

        }
    }
}