package kz.sdk.tussup.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentCreateOfferBinding
import kz.sdk.tussup.models.Order
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class CreateOfferFragment : BaseFragment<FragmentCreateOfferBinding>(FragmentCreateOfferBinding::inflate) {
    private var imageUri: Uri? = null
    private lateinit var selectedDate: String
    private lateinit var selectedTimeFrom: String
    private lateinit var selectedTimeUntil: String

    @Inject
    lateinit var storageReference: StorageReference

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

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

        binding.dateBtn.setOnClickListener {
            showDatePickerDialog(requireContext()) { year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.selectedDate.text = selectedDate
            }
        }

        binding.timeBtn.setOnClickListener {
            showTimePickerDialog(requireContext()) { hourOfDay, minute ->
                selectedTimeFrom = String.format("%02d:%02d", hourOfDay, minute)
                binding.selectedTime.text = selectedTimeFrom
            }
        }
        binding.timeUntilBtn.setOnClickListener {
            showTimePickerDialog(requireContext()) { hourOfDay, minute ->
                selectedTimeUntil = String.format("%02d:%02d", hourOfDay, minute)
                binding.selectedTime2.text = selectedTimeUntil
            }
        }

        binding.uploadImg.setOnClickListener {
            selectOfferImage()
        }

        binding.publishBtn.setOnClickListener {

            if (binding.etName.text.toString().isNullOrEmpty() ||
                binding.etPrice.text.isNullOrEmpty() ||
                binding.etAmount.text.isNullOrEmpty() ||
                selectedDate.isEmpty() || selectedTimeFrom.isEmpty() ||
                imageUri == null) {
                Toast.makeText(context, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = binding.etPrice.text.toString().toDoubleOrNull()
            val amount = binding.etAmount.text.toString().toIntOrNull()

            if (price == null || amount == null) {
                Toast.makeText(context, "Please enter valid price and amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImage { imageUrl ->
                saveOfferToFirestore(
                    name = binding.etName.text.toString(),
                    img = imageUrl,
                    price = price,
                    pickUpDate = selectedDate,
                    pickUpTimeFrom = selectedTimeFrom,
                    pickUpTimeUntil = selectedTimeUntil,
                    amount = amount,
                    description = binding.etDescription.text.toString()
                )
            }
        }
    }

    private fun selectOfferImage() {
        imageResultLauncher.launch("image/*")
    }

    private fun saveOfferToFirestore(
        name: String, img: String, price: Double,
        pickUpDate: String, pickUpTimeFrom: String, pickUpTimeUntil: String,
        amount: Int, description: String
    ) {
        val placeId =  getPlaceIdFromPreferences()!!


        val offerId = firestore.collection("places").document(placeId).collection("offers").document().id

        val offer = Order(
            id = offerId.toIntOrNull(),
            title = name,
            imageUrl = img,
            price = price,
            pickUpDate = pickUpDate,
            pickUpTimeFrom = pickUpTimeFrom,
            pickUpTimeUntil = pickUpTimeUntil,
            amount = amount,
            description = description
        )

        firestore.collection("restaurants").document(placeId)
            .collection("orders").document(offerId)
            .set(offer)
            .addOnSuccessListener {
                Toast.makeText(context, "Offer added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add offer: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImage(callback: (String) -> Unit) {
        imageUri?.let { uri ->
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

    fun showDatePickerDialog(context: Context, onDateSet: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                onDateSet(selectedYear, selectedMonth, selectedDayOfMonth)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    fun showTimePickerDialog(context: Context, onTimeSet: (hourOfDay: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHourOfDay, selectedMinute ->
                onTimeSet(selectedHourOfDay, selectedMinute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun getPlaceIdFromPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("placeId", null)
    }
}
