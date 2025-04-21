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

class DogFormFragment : Fragment() {

    private lateinit var viewModel: DogsListViewModel
    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var breedInput: EditText
    private lateinit var imagePreview: ImageView
    private var imageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val args: DogFormFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dog_form, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                imagePreview.setImageURI(imageUri)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]

        nameInput = view.findViewById(R.id.inputName)
        ageInput = view.findViewById(R.id.inputAge)
        breedInput = view.findViewById(R.id.inputBreed)
        imagePreview = view.findViewById(R.id.imagePreview)

        val btnPickImage = view.findViewById<Button>(R.id.btnPickImage)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitDog)

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
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
