package com.example.dogsadoptionapp.ui.stray

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentStrayDetailsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class StrayDetailsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentStrayDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StrayReportViewModel by viewModels()
    private val args: StrayDetailsFragmentArgs by navArgs()

    private var mapView: MapView? = null
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStrayDetailsBinding.inflate(inflater, container, false)
        mapView = binding.detailMap
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

        viewModel.getReportById(args.reportId).observe(viewLifecycleOwner) { report ->
            if (report != null) {
                Glide.with(requireContext())
                    .load(report.imageUri)
                    .placeholder(R.drawable.barkbuddy_icon_foreground)
                    .into(binding.detailImage)

                binding.detailAddress.text = report.address
                binding.detailDescription.text = report.description

                val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date(report.timestamp))
                binding.detailDate.text = formattedDate

                map?.let {
                    val location = LatLng(report.latitude, report.longitude)
                    it.clear()
                    it.addMarker(MarkerOptions().position(location).title("Reported here"))
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            } else {
                Toast.makeText(requireContext(), "Report not found", Toast.LENGTH_SHORT).show()
            }
        }
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
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
