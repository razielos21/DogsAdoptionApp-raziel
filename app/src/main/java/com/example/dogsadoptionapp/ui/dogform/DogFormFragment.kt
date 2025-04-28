package com.example.dogsadoptionapp.ui.dogform

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            binding.imagePreview.setImageURI(it)
            requireActivity().contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
                dog?.let { existingDog ->
                    nameInput.setText(existingDog.name)
                    ageInput.setText(existingDog.age.toString())
                    breedInput.setText(existingDog.breed)
                    if (existingDog.imageUri.isNotBlank()) {
                        imageUri = existingDog.imageUri.toUri()
                        imagePreview.setImageURI(imageUri)
                    }

                    btnSubmit.setOnClickListener {
                        if (validateInputs()) {
                            val updatedDog = existingDog.copy(
                                name = nameInput.text.toString().trim(),
                                age = ageInput.text.toString().trim().toInt(),
                                breed = breedInput.text.toString().trim(),
                                imageUri = imageUri?.toString() ?: ""
                            )
                            viewModel.updateDog(updatedDog)
                            findNavController().navigateUp()
                        } else {
                            showValidationError()
                        }
                    }
                }
            }
        } else {
            btnSubmit.setOnClickListener {
                if (validateInputs()) {
                    val newDog = Dog(
                        id = 0, // Room will auto-generate ID
                        name = nameInput.text.toString().trim(),
                        age = ageInput.text.toString().trim().toInt(),
                        breed = breedInput.text.toString().trim(),
                        imageUri = imageUri?.toString() ?: ""
                    )
                    viewModel.insertDog(newDog)
                    findNavController().navigateUp()
                } else {
                    showValidationError()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.inputName.text.toString().trim()
        val breed = binding.inputBreed.text.toString().trim()
        val age = binding.inputAge.text.toString().trim()
        val image = imageUri?.toString() ?: ""

        return when {
            name.isBlank() -> false
            breed.isBlank() -> false
            age.isBlank() || age.toIntOrNull() == null || age.toInt() <= 0 -> false
            image.isBlank() -> false
            else -> true
        }
    }

    private fun showValidationError() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.validation_error_title))
            .setMessage(getString(R.string.validation_error_title))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
