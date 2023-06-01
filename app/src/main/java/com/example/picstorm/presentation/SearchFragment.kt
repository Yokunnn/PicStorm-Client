package com.example.picstorm.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentSearchBinding
import com.example.picstorm.presentation.adapter.SearchAdapter
import com.example.picstorm.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val pageSize: Int = 20
    var lastPage = 0

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
        addTextListener()
        initRecyclerView()

        observeSearchItems()
    }

    fun addTextListener() {
        binding.editTextSearch.addTextChangedListener {
            lastPage = 0
            searchViewModel.search(binding.editTextSearch.text.toString(), lastPage, pageSize)
        }
    }

    fun initBottomNav() {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_feedFragment)
        }
        binding.bottomNav.binding.imageUser.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
        }
    }

    fun initRecyclerView() {
        searchViewModel.search("", lastPage, pageSize)
        binding.searchRv.layoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchRv.adapter = searchAdapter

        val layoutManager = binding.searchRv.layoutManager as LinearLayoutManager
        binding.searchRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemsCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!searchAdapter.isLoading && itemsCount == lastVisibleItem + 1 && itemsCount / pageSize == lastPage + 1) {
                    searchAdapter.isLoading = true
                    searchViewModel.search(
                        binding.editTextSearch.text.toString(),
                        itemsCount / pageSize,
                        pageSize
                    )
                    searchAdapter.isLoading = false
                    lastPage++
                }
            }
        })
    }

    fun observeSearchItems() {
        searchViewModel.items.observe(viewLifecycleOwner) { items ->
            searchAdapter.update(items)
        }
    }
}