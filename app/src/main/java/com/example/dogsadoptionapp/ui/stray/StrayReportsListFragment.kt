package com.example.dogsadoptionapp.ui.stray

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentStrayReportsListBinding
import com.example.dogsadoptionapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StrayReportsListFragment : Fragment() {

    private var binding: FragmentStrayReportsListBinding by autoCleared()
    private val viewModel: StrayReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStrayReportsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

        binding.strayReportsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.allReports.observe(viewLifecycleOwner, Observer { reports ->
            binding.strayReportsRecyclerView.adapter = StrayReportAdapter(reports) { report ->
                val action = StrayReportsListFragmentDirections
                    .actionStrayReportsListFragmentToStrayDetailsFragment(report.id)
                findNavController().navigate(action)
            }
        })

        binding.addReportFab.setOnClickListener {
            val action = StrayReportsListFragmentDirections
                .actionStrayReportsListFragmentToReportStrayFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_strays, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete_all -> {
                        val reportCount = viewModel.allReports.value?.size ?: 0
                        if (reportCount == 0) {
                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.alert)
                                .setMessage(R.string.no_stray_reports)
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                        } else {
                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.confirm_delete)
                                .setMessage(R.string.sure_delete_all)
                                .setPositiveButton(R.string.yes) { _, _ ->
                                    viewModel.deleteAllReports()
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
