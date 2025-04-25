package com.example.dogsadoptionapp.ui.dogslist

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.databinding.FragmentDogsListBinding

class DogsListFragment : Fragment() {

    private var _binding: FragmentDogsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DogsListViewModel
    private lateinit var adapter: DogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        binding.dogsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dogsRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]
        viewModel.allDogs.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.btnAddDog.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}