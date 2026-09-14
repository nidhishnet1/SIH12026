package com.example.demosih11.ui.register

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentRegisterBinding
import com.example.demosih11.model.RegistrationCache
import com.example.demosih11.util.LocationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.callback.ErrorInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var isPhotoCaptured = false
    private var aadharPhotoUrl: String? = null
    private var selectedRegistrationRole = "Kabadi Wala"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            performRegistration()
        } else {
            Toast.makeText(requireContext(), "Location access is mandatory for security verification", Toast.LENGTH_LONG).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isPermissionGranted()) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_LONG).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val file = File(requireContext().cacheDir, "aadhar_scan.jpg")

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }

            MediaManager.get()
                .upload(file.absolutePath)
                .unsigned("kabadiwala_upload")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Toast.makeText(
                            requireContext(),
                            "Uploading Aadhaar photo...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        aadharPhotoUrl = resultData["secure_url"] as? String

                        if (!aadharPhotoUrl.isNullOrEmpty()) {
                            isPhotoCaptured = true

                            binding.textAadharPhotoStatus.text = "✓ Aadhaar photo uploaded"
                            binding.textAadharPhotoStatus.setTextColor(
                                resources.getColor(android.R.color.holo_green_dark, null)
                            )

                            Toast.makeText(
                                requireContext(),
                                "Aadhaar photo uploaded ✓",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            isPhotoCaptured = false
                            Toast.makeText(
                                requireContext(),
                                "Aadhaar upload completed but URL was not received",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        Toast.makeText(
                            requireContext(),
                            "Aadhaar upload failed: ${error.description}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                    }
                })
                .dispatch()
        }else {
            Toast.makeText(requireContext(), "Camera cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE

        binding.btnCaptureAadhar.setOnClickListener {
            if (isPermissionGranted()) {
                takePictureLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.toggleGroupRegRole.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedRegistrationRole = when (checkedId) {
                    R.id.btnRegRoleRecycler -> "Recycler"
                    R.id.btnRegRoleKabadi -> "Kabadi Wala"
                    else -> "Kabadi Wala"
                }
            }
        }

        binding.btnSubmitRegister.setOnClickListener {
            requestLocationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        binding.textBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun performRegistration() {
        LocationHelper.checkLocationInIndia(requireContext()) { isInIndia ->
            if (isInIndia) {
                val first = binding.editFirstName.text.toString().trim()
                val last = binding.editLastName.text.toString().trim()
                val phone = binding.editPhone.text.toString().trim()
                val email = binding.editEmail.text.toString().trim()
                if (email.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Email is required for registration",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@checkLocationInIndia
                }
                val aadhar = binding.editAadhar.text.toString().trim()
                val gst = binding.editGst.text.toString().trim()
                val pass = binding.editRegPassword.text.toString()
                val confirm = binding.editRegConfirmPassword.text.toString()

                if (first.isEmpty() || last.isEmpty() || phone.isEmpty() || aadhar.isEmpty() || gst.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please complete all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@checkLocationInIndia
                }

                if (pass != confirm) {
                    Toast.makeText(
                        requireContext(),
                        "Security Error: Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@checkLocationInIndia
                }

                if (!isPhotoCaptured) {
                    Toast.makeText(
                        requireContext(),
                        "Please take an Aadhar Card photo for KYC validation",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@checkLocationInIndia
                }

                val roleName = selectedRegistrationRole

                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid

                            if (uid == null) {
                                Toast.makeText(
                                    requireContext(),
                                    "Registration failed: User ID not found",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@addOnCompleteListener
                            }
                            val registrationData = hashMapOf(
                                "uid" to uid,
                                "role" to roleName,
                                "firstName" to first,
                                "lastName" to last,
                                "phone" to phone,
                                "email" to email,
                                "aadharNo" to aadhar,
                                "gstNo" to gst,
                                "aadharPhotoUrl" to aadharPhotoUrl,
                                "isVerified" to false
                            )
                            db.collection("registrations")
                                .document(uid)
                                .set(registrationData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Registration submitted successfully for $roleName! Pending admin approval.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    findNavController().navigate(R.id.loginFragment)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Registration failed: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            // Firebase account created successfully
                            RegistrationCache.pendingApplications.add(
                                RegistrationCache.UserRegistration(
                                    role = roleName,
                                    firstName = first,
                                    lastName = last,
                                    phone = phone,
                                    email = email,
                                    aadharNo = aadhar,
                                    gstNo = gst
                                )
                            )

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

            }else {
                Toast.makeText(requireContext(), "Access Denied: Registration only available within India territory", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}