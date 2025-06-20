package com.example.dogsadoptionapp.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.databinding.FragmentAboutBinding
import com.example.dogsadoptionapp.utils.autoCleared

class AboutFragment : Fragment() {

    private var binding: FragmentAboutBinding by autoCleared()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textAbout.text = getString(R.string.about_text)
    }

}
