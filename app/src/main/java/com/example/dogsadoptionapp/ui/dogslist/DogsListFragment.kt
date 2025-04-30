package com.example.dogsadoptionapp.ui.dogslist

import android.annotation.SuppressLint
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.databinding.FragmentDogsListBinding

@Suppress("DEPRECATION")
class DogsListFragment : Fragment() {

    private var _binding: FragmentDogsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DogsListViewModel
    private lateinit var adapter: DogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)
        _binding = FragmentDogsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DogsAdapter(
            onItemClick = { dog ->
                val bundle = Bundle().apply {
                    putInt("dogId", dog.id)
                }
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.dogDetailsFragment, bundle)
            },
            onDeleteClick = { dog ->
                showDeleteDialog(dog)
            },
            onEditClick = { dog ->
                val bundle = Bundle().apply {
                    putInt("dogId", dog.id)
                }
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.dogFormFragment, bundle)
            }
        )

        binding.dogsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dogsRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[DogsListViewModel::class.java]
        viewModel.allDogs.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.btnAddDog.setOnClickListener {
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.dogFormFragment)
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        val deleteItem = menu.findItem(R.id.action_delete)
        val icon = deleteItem.icon
        icon?.let {
            val insetIcon = InsetDrawable(it, 0, 20, 0, 0)
            deleteItem.icon = insetIcon
        }

        menu.findItem(R.id.action_delete).isVisible = true
        menu.findItem(R.id.action_return).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.confirm_delete)
                .setMessage(R.string.sure_delete_all)
                .setPositiveButton(R.string.yes)
                { _, _ -> viewModel.deleteAllDogs()}.show()

        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("StringFormatInvalid")
    private fun showDeleteDialog(dog: Dog) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_dog))
            .setMessage(getString(R.string.sure_delete_all, dog.name))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteDog(dog)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}