package com.example.dogsadoptionapp.ui.favorites

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.ui.dogslist.DogsAdapter
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import androidx.navigation.fragment.NavHostFragment

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: DogsListViewModel
    private lateinit var adapter: DogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.favoritesRecyclerView)
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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.allDogs.observe(viewLifecycleOwner) { dogs ->
            val favorites = dogs.filter { it.isFavorite && !it.isAdopted }
            adapter.submitList(favorites)
        }
    }
}
