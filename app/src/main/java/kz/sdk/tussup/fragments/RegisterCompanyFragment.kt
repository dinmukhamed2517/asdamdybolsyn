package kz.sdk.tussup.fragments

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.MainActivity
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentRegisterCompanyBinding
import kz.sdk.tussup.models.Place
import javax.inject.Inject

@AndroidEntryPoint
class RegisterCompanyFragment : BaseFragment<FragmentRegisterCompanyBinding>(FragmentRegisterCompanyBinding::inflate) {
    private val args: RegisterCompanyFragmentArgs by navArgs()
    override var showBottomNavigation = false

    private var imageUri: Uri? = null

    @Inject
    lateinit var storageReference: StorageReference

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val imageResultLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.img.setImageURI(it)
            imageUri = it
            binding.textImg.isVisible = false
        }
    }

    override fun onBindView() {
        super.onBindView()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.uploadImg.setOnClickListener {
            selectEventImage()
        }

        binding.registerBtn.setOnClickListener {
            if (binding.etName.text.isNullOrEmpty() ||
                binding.etBin.text.isNullOrEmpty() || binding.etFio.text.isNullOrEmpty() ||
                binding.etEmail.text.isNullOrEmpty() || binding.etPhone.text.isNullOrEmpty() ||
                binding.etPassword.text.isNullOrEmpty()
            ) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                // Register the user using FirebaseAuth
                registerRestaurantUser()
            }
        }
    }

    private fun selectEventImage() {
        imageResultLauncher.launch("image/*")
    }

    private fun registerRestaurantUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    uploadImage { imageUrl ->
                        val placeId = firestore.collection("restaurants").document().id
                        savePlaceToFirestore(
                            id = placeId,
                            name = binding.etName.text.toString(),
                            owner = binding.etFio.text.toString(),
                            BIN = binding.etBin.text.toString(),
                            phoneNumber = binding.etPhone.text.toString(),
                            email = email,
                            img = imageUrl,
                            longitude = args.longitude.toDouble(),
                            latitude = args.latitude.toDouble(),
                            uid = user?.uid
                        )
                    }
                } else {
                    Toast.makeText(context, "Failed to register: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePlaceToFirestore(id:String,
        name: String, owner: String, BIN: String, phoneNumber: String,
        email: String, img: String, longitude: Double? = null, latitude: Double? = null, uid: String?
    ) {
        val place = Place(
            id = id,
            name = name,
            owner = owner,
            BIN = BIN,
            phoneNumber = phoneNumber,
            email = email,
            img = img,
            longitude = longitude,
            latitude = latitude,
            userId = uid
        )

        firestore.collection("restaurants")
            .document(uid ?: "")
            .set(place)
            .addOnSuccessListener {
                Toast.makeText(context, "Заведение успешно прошло на проверку", Toast.LENGTH_SHORT).show()
                switchToBusinessMode()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to create place: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun savePlaceIdToPreferences(placeId: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("placeId", placeId)
        editor.apply()
    }

    private fun uploadImage(callback: (String) -> Unit) {
        imageUri?.let { uri ->
            binding.img.setImageURI(uri)
            val ref = storageReference.child(uri.lastPathSegment ?: "temp")
            ref.putFile(uri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    callback(downloadUri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchToBusinessMode() {
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.setBusinessMode(true)
        }
    }
}
