package com.example.dogsadoptionapp.ui.dogform

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.databinding.FragmentDogFormBinding
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import com.example.dogsadoptionapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import com.example.dogsadoptionapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import com.example.dogsadoptionapp.utils.TranslationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import androidx.lifecycle.lifecycleScope
import android.util.Log
import java.util.Locale

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
        lifecycleScope.launch {
            Log.d("LanguageCheck", "Device language: ${Locale.getDefault().language}")
            val translated = TranslationHelper.translateToHebrew("hello")
            Log.d("TranslateTest", "Translated: $translated")
        }

        setupMenu()
        fetchAndSetBreeds()

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
                    if (existingDog.imageUri.isNotBlank()) {
                        imageUri = existingDog.imageUri.toUri()
                        if (existingDog.imageUri.startsWith("http")) {
                            Glide.with(requireContext()).load(existingDog.imageUri).into(imagePreview)
                        } else {
                            imagePreview.setImageURI(imageUri)
                        }
                    }

                    when (existingDog.gender) {
                        "Male" -> binding.radioMale.isChecked = true
                        "Female" -> binding.radioFemale.isChecked = true
                    }

                    btnSubmit.setOnClickListener {
                        if (validateInputs()) {
                            val gender = getSelectedGender()
                            val updatedDog = existingDog.copy(
                                name = nameInput.text.toString().trim(),
                                age = ageInput.text.toString().trim().toInt(),
                                breed = (breedInput.selectedItem as? String)?.trim() ?: "",
                                gender = gender,
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
                    val gender = getSelectedGender()
                    val newDog = Dog(
                        id = 0,
                        name = nameInput.text.toString().trim(),
                        age = ageInput.text.toString().trim().toInt(),
                        breed = (breedInput.selectedItem as? String)?.trim() ?: "",
                        gender = gender,
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

    private fun getSelectedGender(): String {
        return when {
            binding.radioMale.isChecked -> "Male"
            binding.radioFemale.isChecked -> "Female"
            else -> ""
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.inputName.text.toString().trim()
        val age = binding.inputAge.text.toString().trim()
        val image = imageUri?.toString() ?: ""
        val gender = getSelectedGender()

        return name.isNotBlank()
                && age.toIntOrNull()?.let { it in 0..20 } == true
                && image.isNotBlank()
                && gender.isNotBlank()
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
                menu.findItem(R.id.action_refresh)?.isVisible = false
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

    private fun fetchAndSetBreeds() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(Constants.BASE_URL.removeSuffix("/") + "s/list/all").readText()
                val breeds = parseBreeds(response)

                val displayBreeds = if (Locale.getDefault().language in listOf("he", "iw")) {
                    breeds.map { breed ->
                        async {
                            val capitalized = breed.replaceFirstChar { it.uppercase() }
                            val translated = TranslationHelper.translateToHebrew(capitalized)
                            if (translated.startsWithHebrew()) translated else null
                        }
                    }.awaitAll().filterNotNull()
                } else {
                    breeds.map { it.replaceFirstChar { c -> c.uppercase() } }
                }
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        displayBreeds
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.inputBreed.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseBreeds(json: String): List<String> {
        val breedList = mutableListOf<String>()
        val message = JSONObject(json).getJSONObject("message")
        message.keys().forEach { breed ->
            breedList.add(breed)
        }
        return breedList.sorted()
    }

    private fun String.startsWithHebrew(): Boolean {
        val c = this.firstOrNull() ?: return false
        return c in '\u0590'..'\u05FF'
    }
}