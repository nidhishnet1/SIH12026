package com.example.demosih11.ui.lots

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.demosih11.databinding.FragmentSubmitEwasteBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File
import java.io.FileOutputStream
import com.cloudinary.android.MediaManager

class SubmitEwasteFragment : Fragment() {

    private var _binding: FragmentSubmitEwasteBinding? = null
    private val binding get() = _binding!!

    private var isImageAttached = false
    private lateinit var categories: List<EwasteCategory>

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isPermissionGranted()) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_LONG).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val file = File(requireContext().cacheDir, "scrap_img_captured.jpg")

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            MediaManager.get()
                .upload(file.absolutePath)
                .unsigned("kabadiwala_upload")
                .callback(object : com.cloudinary.android.callback.UploadCallback {

                    override fun onStart(requestId: String) {
                        Toast.makeText(requireContext(), "Uploading photo...", Toast.LENGTH_SHORT).show()
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        Toast.makeText(requireContext(), "Photo uploaded to Cloudinary ✓", Toast.LENGTH_LONG).show()
                    }

                    override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                        Toast.makeText(requireContext(), "Upload failed: ${error.description}", Toast.LENGTH_LONG).show()
                    }

                    override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                    }
                })
                .dispatch()
            isImageAttached = true
            binding.textScrapPhotoStatus.text = "✓ scrap_img_captured.jpg attached"
            binding.textScrapPhotoStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            Toast.makeText(requireContext(), "E-waste material photo logged", Toast.LENGTH_SHORT).show()
            
            // Automatically identify product using ML Kit
            identifyProduct(bitmap)
        } else {
            Toast.makeText(requireContext(), "Camera cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun identifyProduct(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                var detectedDevice = "Unknown Electronic Device"
                var highestConfidence = 0f
                var autoIndex = -1

                // Refined AI mapping for specific device detection
                for (label in labels) {
                    val text = label.text.lowercase()
                    val confidence = label.confidence

                    if (confidence > highestConfidence) {
                        highestConfidence = confidence
                        detectedDevice = label.text
                    }

                    if (confidence > 0.4) {
                        when {
                            text.contains("phone") || text.contains("mobile") || text.contains("smartphone") || text.contains("cell phone") -> {
                                autoIndex = 0 // PCB/Circuit Boards category
                                detectedDevice = "Smartphone / Mobile Device"
                            }
                            text.contains("laptop") || text.contains("computer") || text.contains("keyboard") || text.contains("mouse") -> {
                                autoIndex = 0 // General Electronics / PCB
                                detectedDevice = "Computing Hardware"
                            }
                            text.contains("battery") || text.contains("accumulator") || text.contains("power bank") -> {
                                autoIndex = 1 // Batteries
                                detectedDevice = "Energy Storage / Battery"
                            }
                            text.contains("monitor") || text.contains("television") || text.contains("screen") || text.contains("display") || text.contains("lcd") -> {
                                autoIndex = 2 // Monitors / CRT
                                detectedDevice = "Visual Display Unit"
                            }
                            text.contains("cable") || text.contains("wire") || text.contains("charger") || text.contains("adapter") -> {
                                autoIndex = 3 // Cables
                                detectedDevice = "Connectivity / Power Cables"
                            }
                            text.contains("printer") || text.contains("scanner") || text.contains("gadget") || text.contains("tool") -> {
                                autoIndex = 4 // Mixed Scrap
                                detectedDevice = "Office / Multi-purpose Gadget"
                            }
                        }
                    }
                    if (autoIndex != -1) break
                }

                if (autoIndex != -1) {
                    binding.spinnerEwasteCategory.setSelection(autoIndex)
                    binding.editScrapCondition.setText("AI Detected: $detectedDevice")
                    Toast.makeText(requireContext(), "AI Analysis: $detectedDevice detected with ${(highestConfidence * 100).toInt()}% confidence", Toast.LENGTH_LONG).show()
                } else {
                    binding.editScrapCondition.setText("AI Detected: General Electronic Component")
                    Toast.makeText(requireContext(), "Product detected as general electronics", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "AI Recognition Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubmitEwasteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate spinner categories list arrays with icons
        categories = listOf(
            EwasteCategory("Circuit Boards (PCB)", R.drawable.ic_pcb),
            EwasteCategory("Li-Ion Batteries", R.drawable.ic_battery),
            EwasteCategory("CRT Monitors / TVs", R.drawable.ic_monitor),
            EwasteCategory("Copper Cables / Chords", R.drawable.ic_cable),
            EwasteCategory("Mixed Electric Scrap", R.drawable.ic_scrap_mix)
        )
        val adapter = CategoryAdapter(requireContext(), categories)
        binding.spinnerEwasteCategory.adapter = adapter

        binding.btnCaptureScrap.setOnClickListener {
            if (isPermissionGranted()) {
                takePictureLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnSubmitDisposal.setOnClickListener {
            val weight = binding.editScrapWeight.text.toString().trim()
            val condition = binding.editScrapCondition.text.toString().trim()
            val location = binding.editPickupLocation.text.toString().trim()
            val selectedCategory = (binding.spinnerEwasteCategory.selectedItem as? EwasteCategory)?.name ?: ""

            if (weight.isEmpty() || condition.isEmpty() || location.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all layout field specifications", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isImageAttached) {
                Toast.makeText(requireContext(), "Please capture an image of the e-waste item for validation", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(
                requireContext(),
                "Disposal batch offer published successfully! Category: $selectedCategory ($weight Kg)",
                Toast.LENGTH_LONG
            ).show()

            // Go back to main collector overview dashboard
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}