package com.example.dogsadoptionapp.ui.dogform

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import androidx.core.net.toUri
import com.example.dogsadoptionapp.databinding.FragmentDogFormBinding

class DogFormFragment : Fragment() {

    private var _binding: FragmentDogFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DogsListViewModel
    private var imageUri: Uri? = null

    private val imagePickerLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.imagePreview.setImageURI(it)
            if (it != null) {
                requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            imageUri = it
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogFormBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]

        val nameInput = binding.inputName
        val ageInput = binding.inputAge
        val breedInput = binding.inputBreed
        val imagePreview = binding.imagePreview

        val btnPickImage = binding.btnPickImage
        val btnSubmit = binding.btnSubmitDog

        btnPickImage.setOnClickListener {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }

        val dogId = arguments?.getInt("dogId", -1) ?: -1

        if (dogId != -1) {
            // Edit mode
            viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
                dog?.let { existingDog ->
                    nameInput.setText(existingDog.name)
                    ageInput.setText(existingDog.age.toString())
                    breedInput.setText(existingDog.breed)

                    if (existingDog.imageUri.isNotBlank()) {
                        imageUri = existingDog.imageUri.toUri()
                        imagePreview.setImageURI(imageUri)
                    }
                    imageUri?.let { imagePreview.setImageURI(it) }

                    btnSubmit.setOnClickListener {
                        val updatedDog = existingDog.copy(
                            name = nameInput.text.toString(),
                            age = ageInput.text.toString().toIntOrNull() ?: 0,
                            breed = breedInput.text.toString(),
                            imageUri = imageUri?.toString() ?: ""
                        )
                        viewModel.updateDog(updatedDog)
                        findNavController().navigateUp()
                    }
                }
            }
        } else {
            // Add mode
            btnSubmit.setOnClickListener {
                val name = nameInput.text.toString()
                val age = ageInput.text.toString().toIntOrNull() ?: 0
                val breed = breedInput.text.toString()
                val image = imageUri?.toString() ?: ""

                if (name.isBlank() || breed.isBlank() || image.isBlank()) {
                    Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newDog = Dog(name = name, age = age, breed = breed, imageUri = image)
                viewModel.insertDog(newDog)
                findNavController().navigateUp()
            }
        }
    }




}
