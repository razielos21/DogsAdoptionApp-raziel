package com.example.dogsadoptionapp.ui.donations

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Donation
import com.example.dogsadoptionapp.data.model.DonationCategory
import com.example.dogsadoptionapp.databinding.FragmentDonationFormBinding
import com.example.dogsadoptionapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DonationFormFragment : Fragment() {

    private var binding: FragmentDonationFormBinding by autoCleared()
    private val viewModel: DonationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonationFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

        binding.buttonSaveDonation.setOnClickListener {
            saveDonation()
        }
    }

    private fun saveDonation() {
        val name = binding.editDonorName.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()

        val selectedId = binding.radioGroupCategory.checkedRadioButtonId
        val category = when (selectedId) {
            R.id.radioFood -> DonationCategory.FOOD
            R.id.radioToys -> DonationCategory.TOYS
            R.id.radioEquipment -> DonationCategory.EQUIPMENT
            else -> null
        }

        if (name.isEmpty() || category == null || description.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val donation = Donation(
            donorName = name,
            category = category,
            description = description,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        viewModel.addDonation(donation)
        findNavController().popBackStack()
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

}