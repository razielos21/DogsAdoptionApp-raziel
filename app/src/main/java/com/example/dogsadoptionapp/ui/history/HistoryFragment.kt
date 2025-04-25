package com.example.dogsadoptionapp.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsadoptionapp.databinding.FragmentHistoryBinding
import com.example.dogsadoptionapp.ui.adoption.AdoptionViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdoptionViewModel
    private lateinit var adapter: AdoptionHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AdoptionViewModel::class.java]
        adapter = AdoptionHistoryAdapter()

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}