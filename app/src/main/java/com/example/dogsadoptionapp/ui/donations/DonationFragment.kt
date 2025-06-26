package com.example.dogsadoptionapp.ui.donations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.DonationCategory
import com.example.dogsadoptionapp.databinding.FragmentDonationBinding
import com.example.dogsadoptionapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DonationFragment : Fragment() {

    private var binding: FragmentDonationBinding by autoCleared()

    private val viewModel: DonationViewModel by viewModels()
    private lateinit var adapter: DonationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupSpinner()
        setupMenu()

        binding.fabAddDonation.setOnClickListener {
            findNavController().navigate(R.id.action_donationFragment_to_donationFormFragment)
        }

        viewModel.donations.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        adapter = DonationAdapter { donation ->
        }
        binding.recyclerDonations.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDonations.adapter = adapter
    }

    private fun setupSpinner() {
        val categories = listOf(
            getString(R.string.all_categories),
            "FOOD", "TOYS", "EQUIPMENT"
        )

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = categories[position]
                val category = when (selected) {
                    "FOOD" -> DonationCategory.FOOD
                    "TOYS" -> DonationCategory.TOYS
                    "EQUIPMENT" -> DonationCategory.EQUIPMENT
                    else -> null
                }
                viewModel.setCategory(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_donations, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete_all -> {
                        val donationCount = viewModel.donations.value?.size ?: 0
                        if (donationCount == 0) {
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle(R.string.alert)
                                .setMessage(R.string.no_donations)
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                        } else {
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle(R.string.confirm_delete)
                                .setMessage(R.string.sure_delete_all)
                                .setPositiveButton(R.string.yes) { _, _ ->
                                    viewModel.deleteAll()
                                }
                                .setNegativeButton(android.R.string.cancel, null)
                                .show()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


}