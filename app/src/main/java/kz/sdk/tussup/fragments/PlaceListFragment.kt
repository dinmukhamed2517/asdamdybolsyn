package kz.sdk.tussup.fragments

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.R
import kz.sdk.tussup.adapters.OfferAdapter
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentPlaceListBinding
import kz.sdk.tussup.models.Order
import javax.inject.Inject


@AndroidEntryPoint
class PlaceListFragment : BaseFragment<FragmentPlaceListBinding>(FragmentPlaceListBinding::inflate) {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var adapter: OfferAdapter

    override fun onBindView() {
        super.onBindView()

        adapter = OfferAdapter()
        binding.offerRecycler.adapter = adapter
        binding.offerRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.mapBtn.setOnClickListener {
            findNavController().navigate(R.id.action_placeListFragment_to_homeFragment)
        }

        adapter.itemClick = {
            findNavController().navigate(PlaceListFragmentDirections.actionPlaceListFragmentToOfferDetailsFragment(it))
        }

        fetchAllOrdersFromFirestore()
    }

    private fun fetchAllOrdersFromFirestore() {
        showLoading()
        firestore.collectionGroup("orders")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val orders = querySnapshot.toObjects(Order::class.java)
                adapter.submitList(orders)
                hideLoading()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch orders: ${e.message}", Toast.LENGTH_SHORT).show()
                hideLoading()
            }
    }
}