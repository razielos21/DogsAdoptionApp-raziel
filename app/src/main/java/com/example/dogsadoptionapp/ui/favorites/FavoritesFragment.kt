package com.example.dogsadoptionapp.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentFavoritesBinding
import com.example.dogsadoptionapp.ui.dogslist.DogsAdapter
import androidx.navigation.fragment.NavHostFragment
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DogsListViewModel
    private lateinit var adapter: DogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]

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

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter

        viewModel.allDogs.observe(viewLifecycleOwner) { dogs ->
            val favorites = dogs.filter { it.isFavorite && !it.isAdopted }
            adapter.submitList(favorites)
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun showDeleteDialog(dog: Dog) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_dog))
            .setMessage(getString(R.string.sure_delete, dog.name))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteDog(dog)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}