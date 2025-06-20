package com.example.dogsadoptionapp.ui.adoption

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import com.example.dogsadoptionapp.databinding.FragmentAdoptionFormBinding
import com.example.dogsadoptionapp.ui.dogslist.DogsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@AndroidEntryPoint
class AdoptionFormFragment : Fragment() {

    private var _binding: FragmentAdoptionFormBinding? = null
    private val binding get() = _binding!!

    private val dogsViewModel: DogsListViewModel by viewModels()
    private val adoptionViewModel: AdoptionViewModel by viewModels()

    private val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdoptionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

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
            showTermsDialog()
        }

        showTermsDialog()

        dogsViewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
            dog?.let {
                dogName.text = it.name

                btnAdopt.setOnClickListener {
                    val first = inputFirst.text.toString().trim()
                    val last = inputLast.text.toString().trim()
                    val address = inputAddress.text.toString().trim()
                    val phone = inputPhone.text.toString().trim()
                    val email = inputEmail.text.toString().trim()
                    val agreed = checkBox.isChecked

                    when {
                        first.isBlank() || last.isBlank() || address.isBlank() || phone.isBlank() || email.isBlank() ->
                            toast(R.string.fill_all_fields)
                        !emailPattern.matcher(email).matches() ->
                            toast(R.string.enter_valid_email)
                        phone.length != 10 ->
                            toast(R.string.enter_valid_phone)
                        !agreed ->
                            toast(R.string.agree_to_terms)
                        else -> {
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
                            dogsViewModel.updateDog(adoptedDog)

                            toast(R.string.adoption_success)
                            NavHostFragment.findNavController(requireParentFragment())
                                .popBackStack(R.id.dogsTabsFragment, false)
                        }
                    }
                }
            }
        }
    }

    private fun showTermsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.adoption_terms_title)
            .setMessage(getString(R.string.adoption_terms))
            .setPositiveButton(R.string.agree) { _, _ ->
                binding.checkboxAgreement.isChecked = true
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                binding.checkboxAgreement.isChecked = false
            }
            .setCancelable(false)
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

    private fun toast(messageResId: Int) {
        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
