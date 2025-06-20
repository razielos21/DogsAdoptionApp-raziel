package com.example.dogsadoptionapp.ui.stray

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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

}
