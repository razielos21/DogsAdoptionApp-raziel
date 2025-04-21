package com.example.dogsadoptionapp.ui.dogdetails

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import androidx.core.net.toUri

class DogDetailsFragment : Fragment() {

    private lateinit var viewModel: DogsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dog_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]

        val image = view.findViewById<ImageView>(R.id.dogDetailImage)
        val name = view.findViewById<TextView>(R.id.dogDetailName)
        val breed = view.findViewById<TextView>(R.id.dogDetailBreed)
        val age = view.findViewById<TextView>(R.id.dogDetailAge)
        val adoptFormBtn = view.findViewById<Button>(R.id.btnAdoptDog)
        val favoriteToggle = view.findViewById<ToggleButton>(R.id.toggleFavorite)

        val dogId = arguments?.getInt("dogId") ?: -1

        if (dogId != -1) {
            viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
                dog?.let {
                    name.text = it.name
                    breed.text = it.breed
                    age.text = getString(R.string.years_old_format, it.age)
                    Glide.with(this).load(it.imageUri.toUri()).into(image)
                    favoriteToggle.isChecked = it.isFavorite

                    adoptFormBtn.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt("dogId", dog.id)
                        }
                        NavHostFragment.findNavController(requireParentFragment())
                            .navigate(R.id.adoptionFormFragment, bundle)
                    }

                    favoriteToggle.setOnCheckedChangeListener { _, isChecked ->
                        val updatedDog = dog.copy(isFavorite = isChecked)
                        viewModel.updateDog(updatedDog)
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Dog not found", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
}
