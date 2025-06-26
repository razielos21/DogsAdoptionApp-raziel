package com.example.dogsadoptionapp.ui.stray

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.StrayReport
import com.example.dogsadoptionapp.databinding.FragmentReportStrayBinding
import com.example.dogsadoptionapp.utils.autoCleared
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ReportStrayFragment : Fragment() {

    private var binding: FragmentReportStrayBinding by autoCleared()
    private val viewModel: StrayReportViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photoUri: Uri? = null
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            getCurrentLocation()
        }
        if (permissions[Manifest.permission.CAMERA] == true) {
            launchCamera()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imagePreview.setImageURI(photoUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportStrayBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        binding.imagePreview.setOnClickListener {
            launchCamera()
        }

        binding.btnSubmit.setOnClickListener {
            val description = binding.editDescription.text.toString()
            val address = binding.editLocation.text.toString()

            if (photoUri == null || description.isBlank() || address.isBlank()) {
                Toast.makeText(requireContext(), getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val report = StrayReport(
                imageUri = photoUri.toString(),
                description = description,
                address = address,
                latitude = currentLatitude,
                longitude = currentLongitude
            )

            viewModel.insert(report)
            Toast.makeText(requireContext(), getString(R.string.report_submitted), Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        getCurrentLocation()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
                menu.findItem(R.id.action_delete)?.isVisible = false
                menu.findItem(R.id.action_return)?.isVisible = true
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_return -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.confirm_exit)
                            .setMessage(R.string.confirm_exit_info)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                findNavController().navigateUp()
                            }
                            .setNegativeButton(R.string.no, null)
                            .setCancelable(false)
                            .show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun launchCamera() {
        val photoFile = File.createTempFile(
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}",
            ".jpg",
            requireContext().cacheDir
        )
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraLauncher.launch(intent)
    }

    private fun getCurrentLocation() {
        val context = requireContext()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude

                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(
                            currentLatitude,
                            currentLongitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    if (addresses.isNotEmpty()) {
                                        binding.editLocation.setText(addresses[0].getAddressLine(0))
                                    }
                                }

                                override fun onError(errorMessage: String?) {
                                    Toast.makeText(context, "Failed to get address", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        val addressList = geocoder.getFromLocation(currentLatitude, currentLongitude, 1)
                        if (!addressList.isNullOrEmpty()) {
                            binding.editLocation.setText(addressList[0].getAddressLine(0))
                        }
                    }

                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

}
