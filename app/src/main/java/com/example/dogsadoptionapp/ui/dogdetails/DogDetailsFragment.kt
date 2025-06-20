package com.example.dogsadoptionapp.ui.dogdetails

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentDogDetailsBinding
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import com.example.dogsadoptionapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogDetailsFragment : Fragment() {

    private var binding: FragmentDogDetailsBinding by autoCleared()

    private val viewModel: DogsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

        val dogId = arguments?.getInt("dogId") ?: -1
        if (dogId == -1) {
            Toast.makeText(requireContext(), R.string.dog_not_found, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
            dog?.let {
                binding.dogDetailName.text = it.name
                binding.dogDetailBreed.text = it.breed
                binding.dogDetailAge.text = getString(R.string.years_old_format, it.age)
                Glide.with(this).load(it.imageUri.toUri()).into(binding.dogDetailImage)
                binding.toggleFavorite.isChecked = it.isFavorite

                binding.btnAdoptDog.setOnClickListener {
                    val bundle = Bundle().apply {
                        putInt("dogId", dog.id)
                    }
                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(R.id.adoptionFormFragment, bundle)
                }

                binding.toggleFavorite.setOnCheckedChangeListener { _, isChecked ->
                    val updatedDog = dog.copy(isFavorite = isChecked)
                    viewModel.updateDog(updatedDog)
                }
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
                menu.findItem(R.id.action_delete)?.isVisible = false
                menu.findItem(R.id.action_return)?.isVisible = true
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_return -> {
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}
