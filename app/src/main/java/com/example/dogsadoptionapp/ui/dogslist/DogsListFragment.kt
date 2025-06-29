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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

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
                if (fact != null) {
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
            val fact: String = try {
                val response = URL(Constants.FACT_URL.removeSuffix("/") + "/facts").readText()
                val json = JSONObject(response)
                val success = json.optBoolean("success", false)
                if (success) {
                    val factsArray = json.optJSONArray("facts")
                    factsArray?.optString(0)?.takeIf { it.isNotBlank() }
                        ?: localFacts.random()
                } else {
                    localFacts.random()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                localFacts.random()
            }

            withContext(Dispatchers.Main) {
                onResult(fact)
            }
        }
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

                menu.findItem(R.id.action_return)?.isVisible = false

                val deleteItem = menu.findItem(R.id.action_delete)
                deleteItem?.isVisible = true

                val icon = deleteItem?.icon
                icon?.let {
                    val insetIcon = InsetDrawable(it, 0, 20, 0, 0)
                    deleteItem.icon = insetIcon
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
