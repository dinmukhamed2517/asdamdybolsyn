package kz.sdk.tussup.fragments

import android.content.Context
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.MainActivity
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentLoginRestaurantBinding
import javax.inject.Inject


@AndroidEntryPoint
class LoginRestaurantFragment:BaseFragment<FragmentLoginRestaurantBinding>(FragmentLoginRestaurantBinding::inflate) {


    @Inject
    lateinit var firestore: FirebaseFirestore
    override var showBottomNavigation: Boolean = false

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onBindView() {
        super.onBindView()

        binding.loginBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            loginRestaurant(email, password)
        }
    }
    private fun loginRestaurant(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let{
                        fetchAndSavePlaceIdForUser(it.uid)
                    }
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    switchToBusinessMode()
                } else {
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun switchToBusinessMode() {
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.setBusinessMode(true)
        }
    }

    private fun fetchAndSavePlaceIdForUser(uid: String) {
        firestore.collection("restaurants")
            .whereEqualTo("userId", uid)  //
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val placeId = documents.documents[0].id  // Get the first matching place document
                    savePlaceIdToPreferences(placeId)  // Store placeId in SharedPreferences
                } else {
                    Toast.makeText(context, "No place found for the user", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch place ID: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePlaceIdToPreferences(placeId: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("placeId", placeId)
        editor.apply()
    }

}