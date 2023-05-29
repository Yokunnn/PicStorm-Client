package com.example.picstorm.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.picstorm.databinding.SearchItemBinding
import com.example.picstorm.domain.model.UserSearched

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var items: List<UserSearched> = emptyList()

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

        with(holder){
            imageView.setImageBitmap(data.avatar)

            nameButton.text = data.nickname
            nameButton.setOnClickListener {
                //nav action
            }

            if (data.subscribed){
                unsubButton.visibility = View.VISIBLE
                unsubButton.setOnClickListener {
                    //unsub action
                }
            } else {
                subButton.visibility = View.VISIBLE
                subButton.setOnClickListener {
                    //sub action
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(data: List<UserSearched>) {
        items = data
        notifyDataSetChanged()
    }
}