package com.example.picstorm.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentSearchBinding
import com.example.picstorm.domain.TokenStorage
import com.example.picstorm.presentation.adapter.UserLineAdapter
import com.example.picstorm.viewmodel.UserLineViewModel
import com.example.picstorm.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val userLineViewModel: UserLineViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage
    private val pageSize: Int = 20
    private var lastPage = 0
    private var accessToken: String? = null
    private lateinit var searchAdapter: UserLineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())
        searchAdapter = UserLineAdapter(userLineViewModel, viewLifecycleOwner, tokenStorage)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomNav()
        addTextListener()

        observeToken()

        initRecyclerView()
        observeSearchItems()
    }

    fun addTextListener() {
        binding.editTextSearch.addTextChangedListener {
            lastPage = 0
            searchViewModel.search(
                accessToken,
                binding.editTextSearch.text.toString(),
                lastPage,
                pageSize
            )
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
                        accessToken,
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

    fun observeToken() {
        tokenStorage.token.observe(viewLifecycleOwner) { token ->
            if (token.accessToken != "null") {
                var jwt = JWT(token.accessToken)
                if (jwt.isExpired(0)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        tokenStorage.deleteToken()
                    }
                } else {
                    accessToken = token.accessToken
                }
            } else {
                accessToken = null
            }
            searchViewModel.search(accessToken, "", lastPage, pageSize)
        }
    }

    fun observeSearchItems() {
        searchViewModel.items.observe(viewLifecycleOwner) { items ->
            searchAdapter.update(items)
        }
    }
}