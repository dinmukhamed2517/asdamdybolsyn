package kz.sdk.tussup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kz.sdk.tussup.adapters.OfferAdapter
import kz.sdk.tussup.databinding.FragmentMapBottomSheetBinding
import kz.sdk.tussup.models.Order
import kz.sdk.tussup.models.Place
import kz.sdk.tussup.utils.LoadingDialogFragment

class MapBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMapBottomSheetBinding
    private lateinit var offerAdapter: OfferAdapter
    private var loadingDialog: LoadingDialogFragment? = null

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val place = arguments?.getParcelable<Place>("location")

        if (place != null) {
            setupRecyclerView()
            fetchOffersForPlace(place.id)
        } else {
            Toast.makeText(requireContext(), "Place data is missing", Toast.LENGTH_SHORT).show()
        }
    }

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }
        loadingDialog?.show(parentFragmentManager, "loading")
    }

    fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun setupRecyclerView() {
        offerAdapter = OfferAdapter()
        binding.offerRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.offerRecycler.adapter = offerAdapter
    }

    private fun fetchOffersForPlace(placeId: String?) {
        showLoading()
        if (placeId != null) {
            firestore.collection("restaurants")
                .document(placeId)
                .collection("orders")
                .get()
                .addOnSuccessListener { documents ->
                    val offers = documents.toObjects(Order::class.java)
                    if (offers.isEmpty()) {
                        binding.noBookingCard.visibility = View.VISIBLE
                    } else {
                        binding.noBookingCard.visibility = View.GONE
                        offerAdapter.submitList(offers)
                    }
                    hideLoading()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    hideLoading()
                }
        } else {
            Toast.makeText(requireContext(), "Invalid Place ID", Toast.LENGTH_SHORT).show()
            hideLoading()
        }
    }
}