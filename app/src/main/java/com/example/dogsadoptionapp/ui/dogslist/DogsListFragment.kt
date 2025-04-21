package com.example.dogsadoptionapp.ui.dogslist

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import androidx.navigation.fragment.NavHostFragment

class DogsListFragment : Fragment() {

    private lateinit var viewModel: DogsListViewModel
    private lateinit var adapter: DogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dogs_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.dogsRecyclerView)
        val addButton = view.findViewById<Button>(R.id.btnAddDog)

        adapter = DogsAdapter(
            onItemClick = { dog ->
                val bundle = Bundle().apply {
                    putInt("dogId", dog.id)
                }
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.dogDetailsFragment, bundle)
            },
            onDeleteClick = { dog ->
                showDeleteDialog(dog)
            },
            onEditClick = { dog ->
                val bundle = Bundle().apply {
                    putInt("dogId", dog.id)
                }
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.dogFormFragment, bundle)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]
        viewModel.allDogs.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        addButton.setOnClickListener {
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.dogFormFragment)
        }
    }

    private fun showDeleteDialog(dog: Dog) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_dog))
            .setMessage("Are you sure you want to delete ${dog.name}?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteDog(dog)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
