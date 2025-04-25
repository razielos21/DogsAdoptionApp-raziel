package com.example.dogsadoptionapp.ui.favorites

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentFavoritesBinding
import com.example.dogsadoptionapp.ui.dogslist.DogsAdapter
import androidx.navigation.fragment.NavHostFragment
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
                viewModel.deleteDog(dog)
                Toast.makeText(requireContext(), getString(R.string.dog_deleted), Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}