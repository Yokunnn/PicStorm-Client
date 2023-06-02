package com.example.picstorm.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.picstorm.databinding.SearchItemBinding
import com.example.picstorm.domain.TokenStorage
import com.example.picstorm.domain.model.UserSearched
import com.example.picstorm.util.ApiStatus
import com.example.picstorm.viewmodel.UserLineViewModel

class UserLineAdapter constructor(
    private val userLineViewModel: UserLineViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val tokenStorage: TokenStorage
) : RecyclerView.Adapter<UserLineAdapter.SearchViewHolder>() {

    private var items: MutableList<UserSearched> = emptyList<UserSearched>().toMutableList()
    private var accessToken: String? = null
    var isLoading: Boolean = false
    private var viewHolders: MutableMap<Long, SearchViewHolder> =
        emptyMap<Long, SearchViewHolder>().toMutableMap()

    init {
        observeToken()
        userLineViewModel.subResult.observe(lifecycleOwner) { result ->
            when (result.second.status) {
                ApiStatus.SUCCESS ->  {
                    val holder: SearchViewHolder = viewHolders[result.first]!!
                    with(holder) {
                        if (subButton.visibility == View.VISIBLE) {
                            subButton.visibility = View.GONE
                            unsubButton.visibility = View.VISIBLE
                        } else if (unsubButton.visibility == View.VISIBLE) {
                            unsubButton.visibility = View.GONE
                            subButton.visibility = View.VISIBLE
                        }
                    }
                }
                ApiStatus.ERROR ->   {
                }
                ApiStatus.LOADING ->  {
                }
            }
        }
    }

    inner class SearchViewHolder(
        binding: SearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView = binding.imageView
        val nameButton = binding.nameButton
        val subButton = binding.subButton
        val unsubButton = binding.unsubButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = SearchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val data = items[position]
        viewHolders[data.id] = holder
        with(holder) {
            imageView.setImageBitmap(data.avatar)

            nameButton.text = data.nickname
            nameButton.setOnClickListener {
                //nav action
            }

            unsubButton.setOnClickListener {
                userLineViewModel.changeSubscribe(accessToken, data.id)
            }
            subButton.setOnClickListener {
                userLineViewModel.changeSubscribe(accessToken, data.id)
            }
            if (data.subscribed == true) {
                unsubButton.visibility = View.VISIBLE
            } else if (data.subscribed == false) {
                subButton.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(data: List<UserSearched>) {
        items = data.toMutableList()
        notifyDataSetChanged()
    }

    fun observeToken() {
        tokenStorage.token.observe(lifecycleOwner) { token ->
            accessToken = if (token.accessToken != "null") {
                token.accessToken
            } else {
                null
            }
        }
    }
}