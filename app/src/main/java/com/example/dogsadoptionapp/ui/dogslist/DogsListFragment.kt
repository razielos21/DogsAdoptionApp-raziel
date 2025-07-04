package com.example.dogsadoptionapp.ui.dogslist

import android.annotation.SuppressLint
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.databinding.FragmentDogsListBinding
import com.example.dogsadoptionapp.utils.Constants
import com.example.dogsadoptionapp.utils.autoCleared
import com.example.dogsadoptionapp.utils.TranslationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale

@AndroidEntryPoint
class DogsListFragment : Fragment() {

    private var binding: FragmentDogsListBinding by autoCleared()
    private val viewModel: DogsListViewModel by viewModels()
    private lateinit var adapter: DogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

        adapter = DogsAdapter(
            onItemClick = { dog ->
                val bundle = Bundle().apply { putInt("dogId", dog.id) }
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.dogDetailsFragment, bundle)
            },
            onDeleteClick = { dog -> showDeleteDialog(dog) },
            onEditClick = { dog ->
                val bundle = Bundle().apply { putInt("dogId", dog.id) }
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.dogFormFragment, bundle)
            }
        )

        binding.dogsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dogsRecyclerView.adapter = adapter

        viewModel.allDogs.observe(viewLifecycleOwner) { dogList ->
            adapter.submitList(dogList)

            if (dogList.isEmpty()) {
                binding.emptyText.visibility = View.VISIBLE
                binding.dogsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.dogsRecyclerView.visibility = View.VISIBLE
            }
        }

        binding.btnAddDog.setOnClickListener {
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.dogFormFragment)
        }

        binding.btnRandomFact.setOnClickListener {
            fetchRandomDogFact { fact ->
                if (fact.isNotBlank()) {
                    showRandomFactDialog(fact)
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.alert))
                        .setMessage(getString(R.string.api_error_message))
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                }
            }
        }
    }

    private fun fetchRandomDogFact(onResult: (String) -> Unit) {
        val localFacts = resources.getStringArray(R.array.local_dog_facts).toList()

        CoroutineScope(Dispatchers.IO).launch {
            val rawFact: String = try {
                val response = URL(Constants.FACT_URL.removeSuffix("/") + "/facts").readText()
                val json = JSONObject(response)
                val success = json.optBoolean("success", false)
                if (success) {
                    val factsArray = json.optJSONArray("facts")
                    factsArray?.optString(0)?.takeIf { it.isNotBlank() } ?: localFacts.random()
                } else {
                    localFacts.random()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                localFacts.random()
            }

            val finalFact = TranslationHelper.translateToHebrew(rawFact)

            withContext(Dispatchers.Main) {
                onResult(finalFact)
            }
        }
    }

    private fun fetchAndAddRandomDogs(count: Int = 5) {
        CoroutineScope(Dispatchers.IO).launch {
            val nameArray = resources.getStringArray(R.array.random_dog_names)

            repeat(count) {
                try {
                    val response = URL(Constants.BASE_URL.removeSuffix("/") + "s/image/random").readText()
                    val json = JSONObject(response)
                    val imageUrl = json.getString("message")
                    val breedUrl = imageUrl.split("/").getOrNull(4) ?: "Unknown"
                    val breed = breedUrl.replaceFirstChar { it.uppercase() }
                    val gender = if ((0..1).random() == 0)
                        if (isDeviceInHebrew()) "זכר" else "Male"
                    else
                        if (isDeviceInHebrew()) "נקבה" else "Female"

                    val translatedBreed = if (isDeviceInHebrew())
                        TranslationHelper.translateToHebrew(breed.replaceFirstChar { it.uppercase() })
                    else
                        breed

                    val dog = Dog(
                        id = 0,
                        name = nameArray.random(),
                        age = (0..20).random(),
                        gender = gender,
                        breed = translatedBreed,
                        imageUri = imageUrl,
                        isFavorite = false,
                        isAdopted = false
                    )

                    withContext(Dispatchers.Main) {
                        viewModel.insertDog(dog)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isDeviceInHebrew(): Boolean {
        val locale = Locale.getDefault()
        return locale.language == "he" || locale.language == "iw"
    }

    private fun showRandomFactDialog(fact: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_random_fact, null)

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val factText = dialogView.findViewById<TextView>(R.id.factText)
        val image = dialogView.findViewById<ImageView>(R.id.dogImage)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        title.text = getString(R.string.random_fact)
        factText.text = fact
        image.setImageResource(R.drawable.dog_and_speech_bubble)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        okButton.setOnClickListener { dialog.dismiss() }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)

                menu.findItem(R.id.action_delete)?.isVisible = true
                menu.findItem(R.id.action_return)?.isVisible = false
                menu.findItem(R.id.action_refresh)?.isVisible = true

                menu.findItem(R.id.action_delete)?.icon?.let {
                    val insetIcon = InsetDrawable(it, 0, 0, 0, 0)
                    menu.findItem(R.id.action_delete)?.icon = insetIcon
                }
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_delete -> {
                        val dogCount = viewModel.allDogs.value?.size ?: 0
                        if (dogCount == 0) {
                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.alert)
                                .setMessage(R.string.no_dogs)
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                        } else {
                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.confirm_delete)
                                .setMessage(R.string.sure_delete_all)
                                .setPositiveButton(R.string.yes) { _, _ ->
                                    viewModel.deleteAllDogs()
                                }
                                .setNegativeButton(android.R.string.cancel, null)
                                .show()
                        }
                        true
                    }
                    R.id.action_refresh -> {
                        fetchAndAddRandomDogs()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    @SuppressLint("StringFormatInvalid")
    private fun showDeleteDialog(dog: Dog) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_dog))
            .setMessage(getString(R.string.sure_delete, dog.name))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteDog(dog)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}