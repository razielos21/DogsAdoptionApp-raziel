package com.example.dogsadoptionapp.ui.tabhost

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dogsadoptionapp.ui.dogslist.DogsListFragment
import com.example.dogsadoptionapp.ui.favorites.FavoritesFragment
import com.example.dogsadoptionapp.ui.history.HistoryFragment

class DogsTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DogsListFragment()
            1 -> FavoritesFragment()
            2 -> HistoryFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
