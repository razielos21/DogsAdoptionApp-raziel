package com.example.dogsadoptionapp.ui.dogform

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.databinding.FragmentDogFormBinding
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import com.example.dogsadoptionapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogFormFragment : Fragment() {

    private var binding: FragmentDogFormBinding by autoCleared()
    private val viewModel: DogsListViewModel by viewModels()
    private var imageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
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
        binding = FragmentDogFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

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
                        id = 0,
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

        return name.isNotBlank()
                && breed.isNotBlank()
                && age.toIntOrNull()?.let { it > 0 } == true
                && image.isNotBlank()
    }

    private fun showValidationError() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.validation_error_title))
            .setMessage(getString(R.string.validation_error_message))
            .setPositiveButton(android.R.string.ok, null)
            .show()
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
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.confirm_exit)
                            .setMessage(R.string.confirm_exit_info)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                findNavController().navigateUp()
                            }
                            .setNegativeButton(R.string.no, null)
                            .setCancelable(false)
                            .show()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}
