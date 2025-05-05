package com.example.dogsadoptionapp.ui.adoption

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import com.example.dogsadoptionapp.databinding.FragmentAdoptionFormBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class AdoptionFormFragment : Fragment() {

    private var _binding: FragmentAdoptionFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DogsListViewModel
    private lateinit var adoptionViewModel: AdoptionViewModel

    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdoptionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]
        adoptionViewModel = ViewModelProvider(this)[AdoptionViewModel::class.java]

        val dogId = arguments?.getInt("dogId") ?: -1
        if (dogId == -1) {
            Toast.makeText(requireContext(), R.string.missing_dog_ID, Toast.LENGTH_SHORT).show()
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
                .setTitle(R.string.adoption_terms_title)
                .setMessage(getString(R.string.adoption_terms))
                .setPositiveButton(R.string.agree) { _, _ ->
                    checkBox.isChecked = true
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    checkBox.isChecked = false
                }
                .setCancelable(false)
                .show()
        }

        viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
            dog?.let {
                dogName.text = it.name

                btnAdopt.setOnClickListener {
                    val first = inputFirst.text.toString().trim()
                    val last = inputLast.text.toString().trim()
                    val address = inputAddress.text.toString().trim()
                    val phone = inputPhone.text.toString().trim()
                    val email = inputEmail.text.toString().trim()
                    val agreed = checkBox.isChecked

                    if (first.isBlank() || last.isBlank() || address.isBlank() || phone.isBlank() || email.isBlank()) {
                        Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (!emailPattern.matcher(email).matches()) {
                        Toast.makeText(requireContext(), R.string.enter_valid_email, Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (phone.length != 10) {
                        Toast.makeText(requireContext(), R.string.enter_valid_phone, Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (!agreed) {
                        Toast.makeText(requireContext(), getString(R.string.agree_to_terms), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val date = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

                    val record = AdoptionRecord(
                        dogId = dog.id,
                        dogName = dog.name,
                        dogImageUri = dog.imageUri,
                        firstName = first,
                        lastName = last,
                        date = date,
                        phone = phone,
                        email = email
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

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.action_delete).isVisible = false
        menu.findItem(R.id.action_return).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_return -> {
                android.app.AlertDialog.Builder(requireContext())
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
            else -> super.onOptionsItemSelected(item)
        }
        }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}