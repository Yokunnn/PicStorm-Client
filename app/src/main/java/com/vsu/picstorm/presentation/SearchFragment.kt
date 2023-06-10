package com.vsu.picstorm.presentation

import android.app.Dialog
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
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentDialogAlertBinding
import com.vsu.picstorm.databinding.FragmentSearchBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.presentation.adapter.UserLineAdapter
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.util.DialogFactory
import com.vsu.picstorm.viewmodel.SearchViewModel
import com.vsu.picstorm.viewmodel.UserLineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var alertBinding: FragmentDialogAlertBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val userLineViewModel: UserLineViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage
    private val pageSize: Int = 20
    private var lastPage = 0
    private var accessToken: String? = null
    private lateinit var searchAdapter: UserLineAdapter
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())
        searchAdapter = UserLineAdapter(userLineViewModel, viewLifecycleOwner, tokenStorage, findNavController(), requireContext())
        alertBinding = FragmentDialogAlertBinding.inflate(inflater, container, false)
        dialog = DialogFactory.createAlertDialog(requireContext(), alertBinding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTextListener()

        observeToken()

        initRecyclerView()
        observeSearchResult()
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

    fun initBottomNav(isAuthorised: Boolean) {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_feedFragment)
        }
        if (isAuthorised) {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
            }
        } else {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_searchFragment_to_loginFragment)
            }
        }
    }

    fun initRecyclerView() {
        binding.searchRv.setItemViewCacheSize(pageSize)
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
                    searchViewModel.search(
                        accessToken,
                        binding.editTextSearch.text.toString(),
                        itemsCount / pageSize,
                        pageSize
                    )
                    lastPage++
                }
            }
        })
    }

    fun observeToken() {
        tokenStorage.token.observe(viewLifecycleOwner) { token ->
            if (token.accessToken != "null") {
                val jwt = JWT(token.accessToken)
                if (jwt.isExpired(0)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        tokenStorage.deleteToken()
                    }
                } else {
                    initBottomNav(true)
                    accessToken = token.accessToken
                }
            } else {
                initBottomNav(false)
                accessToken = null
            }
            searchViewModel.search(accessToken, "", lastPage, pageSize)
        }
    }

    fun observeSearchResult() {
        searchViewModel.searchResult.observe(viewLifecycleOwner) { result ->
            when (result.second.status) {
                ApiStatus.SUCCESS ->  {
                    if (result.first == 0) {
                        searchAdapter.clear()
                        lastPage = 0
                    }
                    val data = result.second.data!!
                    searchAdapter.update(data)
                    searchAdapter.isLoading = false
                }
                ApiStatus.ERROR ->   {
                    searchAdapter.isLoading = false
                    alertBinding.textView.text = result.second.message.toString()
                    dialog.show()
                }
                ApiStatus.LOADING ->  {
                    searchAdapter.isLoading = true
                }
            }
        }
    }

    override fun onDestroy() {
        searchAdapter.recycleAll()
        super.onDestroy()
    }
}