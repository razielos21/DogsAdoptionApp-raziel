package com.example.dogsadoptionapp.ui.adoption

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri
import com.example.dogsadoptionapp.databinding.FragmentAdoptionFormBinding

class AdoptionFormFragment : Fragment() {

    private var _binding: FragmentAdoptionFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DogsListViewModel
    private lateinit var adoptionViewModel: AdoptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdoptionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]
        adoptionViewModel = ViewModelProvider(this)[AdoptionViewModel::class.java]

        val dogId = arguments?.getInt("dogId") ?: -1
        if (dogId == -1) {
            Toast.makeText(requireContext(), "Missing dog ID", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        val dogName = binding.adoptionDogName
        val inputFirst = binding.inputAdopterFirstName
        val inputLast = binding.inputAdopterLastName
        val inputAddress = binding.inputAddress
        val inputPhone = binding.inputAdopterPhone
        val inputEmail = binding.inputAdopterEmail
        val checkBox = binding.checkboxAgreement
        val btnAdopt = binding.btnAdoptConfirm

        checkBox.isChecked = false
        checkBox.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Adoption Terms")
                .setMessage(getString(R.string.adoption_terms))
                .setPositiveButton("Agree") { _, _ ->
                    checkBox.isChecked = true
                }
                .setNegativeButton("Cancel") { _, _ ->
                    checkBox.isChecked = false
                }
                .setCancelable(false)
                .show()
        }

        viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
            dog?.let {
                dogName.text = it.name

                btnAdopt.setOnClickListener {
                    val first = inputFirst.text.toString()
                    val last = inputLast.text.toString()
                    val address = inputAddress.text.toString()
                    val phone = inputPhone.text.toString()
                    val email = inputEmail.text.toString()
                    val agreed = checkBox.isChecked

                    if (first.isBlank() || last.isBlank() || address.isBlank() || phone.isBlank() || email.isBlank()) {
                        Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (!agreed) {
                        Toast.makeText(requireContext(), getString(R.string.agree_to_terms), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                    val record = AdoptionRecord(
                        dogId = dog.id,
                        dogName = dog.name,
                        dogImageUri = dog.imageUri,
                        firstName = first,
                        lastName = last,
                        date = date
                    )

                    adoptionViewModel.insert(record)

                    val adoptedDog = dog.copy(isAdopted = true)
                    viewModel.updateDog(adoptedDog)

                    Toast.makeText(requireContext(), getString(R.string.adoption_success), Toast.LENGTH_SHORT).show()
                    NavHostFragment.findNavController(requireParentFragment())
                        .popBackStack(R.id.dogsTabsFragment, false)
                }
            }
        }
    }
}
