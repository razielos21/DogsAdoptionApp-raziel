package com.example.dogsadoptionapp.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.ui.adoption.AdoptionViewModel

class HistoryFragment : Fragment() {

    private lateinit var viewModel: AdoptionViewModel
    private lateinit var adapter: AdoptionHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AdoptionViewModel::class.java]
        adapter = AdoptionHistoryAdapter()

        val recyclerView = view.findViewById<RecyclerView>(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
        }
    }
}
