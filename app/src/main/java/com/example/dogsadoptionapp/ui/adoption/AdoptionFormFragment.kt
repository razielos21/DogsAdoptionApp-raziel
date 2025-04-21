package com.example.dogsadoptionapp.ui.adoption

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
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

class AdoptionFormFragment : Fragment() {

    private lateinit var viewModel: DogsListViewModel
    private lateinit var adoptionViewModel: AdoptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_adoption_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]
        adoptionViewModel = ViewModelProvider(this)[AdoptionViewModel::class.java]

        val dogId = arguments?.getInt("dogId") ?: -1
        if (dogId == -1) {
            Toast.makeText(requireContext(), "Missing dog ID", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        val image = view.findViewById<ImageView>(R.id.adoptionDogImage)
        val dogName = view.findViewById<TextView>(R.id.adoptionDogName)
        val inputFirst = view.findViewById<EditText>(R.id.inputAdopterFirstName)
        val inputLast = view.findViewById<EditText>(R.id.inputAdopterLastName)
        val inputCity = view.findViewById<EditText>(R.id.inputAdopterCity)
        val checkBox = view.findViewById<CheckBox>(R.id.checkboxAgreement)
        val btnAdopt = view.findViewById<Button>(R.id.btnAdoptConfirm)

        viewModel.getDogById(dogId).observe(viewLifecycleOwner) { dog ->
            dog?.let {
                dogName.text = it.name
                Glide.with(this).load(it.imageUri.toUri()).into(image)

                btnAdopt.setOnClickListener {
                    val first = inputFirst.text.toString()
                    val last = inputLast.text.toString()
                    val city = inputCity.text.toString()
                    val agreed = checkBox.isChecked

                    if (first.isBlank() || last.isBlank() || city.isBlank() || !agreed) {
                        Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                    val record = AdoptionRecord(
                        dogId = dog.id,
                        dogName = dog.name,
                        dogImageUri = dog.imageUri,
                        firstName = first,
                        lastName = last,
                        city = city,
                        date = date
                    )

                    adoptionViewModel.insert(record)

                    // mark dog as adopted
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
