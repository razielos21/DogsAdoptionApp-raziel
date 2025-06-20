package com.example.dogsadoptionapp.ui.tabhost

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentDogsTabsBinding
import com.example.dogsadoptionapp.utils.autoCleared
import com.google.android.material.tabs.TabLayoutMediator

class DogsTabsFragment : Fragment() {

    private var binding: FragmentDogsTabsBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogsTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTabs()
        setupMenu()
    }

    private fun setupTabs() {
        val adapter = DogsTabsAdapter(this)
        binding.dogsViewPager.adapter = adapter

        TabLayoutMediator(binding.dogsTabLayout, binding.dogsViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_all_dogs)
                1 -> getString(R.string.tab_favorites)
                2 -> getString(R.string.tab_history)
                else -> ""
            }
        }.attach()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}
