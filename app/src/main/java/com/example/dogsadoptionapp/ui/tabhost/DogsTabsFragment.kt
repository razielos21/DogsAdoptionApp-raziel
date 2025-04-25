package com.example.dogsadoptionapp.ui.tabhost

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentDogsTabsBinding
import com.google.android.material.tabs.TabLayoutMediator

class DogsTabsFragment : Fragment() {

    private var _binding: FragmentDogsTabsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogsTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}