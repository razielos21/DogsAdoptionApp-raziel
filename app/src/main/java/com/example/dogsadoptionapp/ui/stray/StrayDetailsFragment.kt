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
import com.example.dogsadoptionapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class StrayDetailsFragment : Fragment() {

    private var binding: FragmentStrayDetailsBinding by autoCleared()
    private val viewModel: StrayReportViewModel by viewModels()
    private val args: StrayDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStrayDetailsBinding.inflate(inflater, container, false)
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
                menu.findItem(R.id.action_refresh)?.isVisible = false
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
}
