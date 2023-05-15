package com.example.picstorm.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentSearchBinding
import com.example.picstorm.presentation.adapter.SearchAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val searchAdapter = SearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomNav()

        initRecyclerView()
    }

    fun initBottomNav(){
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_feedFragment)
        }
    }

    fun initRecyclerView(){
        binding.searchRv.adapter = searchAdapter
    }
}