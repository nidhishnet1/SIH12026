package com.example.demosih11.ui.lots

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentCreateLotBinding
import com.example.demosih11.db.AppDatabase
import com.example.demosih11.db.Lot
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CreateLotFragment : Fragment() {
    private var _binding: FragmentCreateLotBinding? = null
    private val binding get() = _binding!!

    private var isPhotoCaptured = false
    private var lotPhotoUrl: String? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isPermissionGranted()) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_LONG).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            binding.imgPreview.visibility = View.VISIBLE
            binding.imgPreview.setImageBitmap(bitmap)
            
            val file = File(requireContext().cacheDir, "lot_photo_${System.currentTimeMillis()}.jpg")

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }

            MediaManager.get()
                .upload(file.absolutePath)
                .unsigned("kabadiwala_upload")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Toast.makeText(requireContext(), "Uploading photo...", Toast.LENGTH_SHORT).show()
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        lotPhotoUrl = resultData["secure_url"] as? String

                        if (!lotPhotoUrl.isNullOrEmpty()) {
                            isPhotoCaptured = true
                            Toast.makeText(requireContext(), "Photo uploaded successfully ✓", Toast.LENGTH_LONG).show()
                        } else {
                            isPhotoCaptured = false
                            Toast.makeText(requireContext(), "Upload completed but URL was not received", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        Toast.makeText(requireContext(), "Photo upload failed: ${error.description}", Toast.LENGTH_LONG).show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        } else {
            Toast.makeText(requireContext(), "Camera cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateLotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCapture.setOnClickListener {
            if (isPermissionGranted()) {
                takePictureLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.editWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculateEstimate() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.chipGroupCategory.setOnCheckedChangeListener { _, _ -> calculateEstimate() }

        binding.btnSave.setOnClickListener {
            saveLot()
        }
    }

    private fun calculateEstimate() {
        val weight = binding.editWeight.text.toString().toDoubleOrNull() ?: 0.0
        val selectedChipId = binding.chipGroupCategory.checkedChipId
        if (selectedChipId == View.NO_ID) {
            binding.txtEstimate.text = "₹ 0.00"
            return
        }
        
        val selectedChip = binding.root.findViewById<Chip>(selectedChipId)
        val rate = when (selectedChip.text.toString()) {
            getString(R.string.cat_pcb) -> 350.0
            getString(R.string.cat_battery) -> 85.0
            getString(R.string.cat_cable) -> 450.0
            else -> 0.0
        }
        
        val estimate = weight * rate
        binding.txtEstimate.text = "₹ %.2f".format(estimate)
    }

    private fun saveLot() {
        val weight = binding.editWeight.text.toString().toDoubleOrNull()
        val selectedChipId = binding.chipGroupCategory.checkedChipId
        
        if (weight == null || selectedChipId == View.NO_ID) {
            Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPhotoCaptured || lotPhotoUrl.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please capture and wait for the photo upload to complete", Toast.LENGTH_SHORT).show()
            return
        }

        val category = binding.root.findViewById<Chip>(selectedChipId).text.toString()
        val rate = when (category) {
            getString(R.string.cat_pcb) -> 350.0
            getString(R.string.cat_battery) -> 85.0
            getString(R.string.cat_cable) -> 450.0
            else -> 0.0
        }
        val estimatedValue = weight * rate

        val lot = Lot(
            category = category,
            weight = weight,
            estimatedValue = estimatedValue,
            photoPath = lotPhotoUrl
        )

        // Save locally for My Lots
        lifecycleScope.launch {
            AppDatabase.getDatabase(requireContext()).lotDao().insertLot(lot)
            
            // Now save to Firestore for Recyclers to see
            val lotId = UUID.randomUUID().toString()
            val firestoreLot = hashMapOf(
                "lotId" to lotId,
                "kabadiwalaUid" to (auth.currentUser?.uid ?: ""),
                "category" to category,
                "weight" to weight,
                "estimatedValue" to estimatedValue,
                "photoUrl" to lotPhotoUrl,
                "status" to "PENDING",
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("lots")
                .document(lotId)
                .set(firestoreLot)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Lot Posted Successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to post lot: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
