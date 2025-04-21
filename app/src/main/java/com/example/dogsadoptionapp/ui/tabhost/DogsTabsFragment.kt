package com.example.dogsadoptionapp.ui.tabhost

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.dogsadoptionapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DogsTabsFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dogs_tabs, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.dogsViewPager)
        tabLayout = view.findViewById(R.id.dogsTabLayout)

        val adapter = DogsTabsAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_all_dogs)
                1 -> getString(R.string.tab_favorites)
                2 -> getString(R.string.tab_history)
                else -> ""
            }
        }.attach()
    }
}
