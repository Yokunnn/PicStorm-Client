package com.vsu.picstorm.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vsu.picstorm.databinding.PublicationItemBinding
import com.vsu.picstorm.domain.model.Publication

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>(){

    private var publications: List<Publication> = emptyList()

    inner class FeedViewHolder(
        binding: PublicationItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        val avatarIv = binding.avatarImageView
        val nameButton = binding.nameButton
        val deleteButton = binding.deleteButton
        val publicationIv = binding.publicationImageView
        val rateUpButton = binding.rateUpButton
        val rateDownButton = binding.rateDownButton
        val rateTv = binding.rateTextView
        val dateTv = binding.dateTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = PublicationItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val data = publications[position]

        with(holder){
            avatarIv.setImageBitmap(data.avatar)
            dateTv.text = data.date.toString()
            rateTv.text = data.rating.toString()
            publicationIv.setImageBitmap(data.pic)

            nameButton.text = data.nickname
            nameButton.setOnClickListener {
                //nav action
            }

            deleteButton.setOnClickListener {
                //delete action
            }

            rateUpButton.setOnClickListener {
                //rate up
            }

            rateDownButton.setOnClickListener {
                //rate down
            }
        }
    }

    override fun getItemCount(): Int = publications.size

    fun update(data: List<Publication>) {
        publications = data
        notifyDataSetChanged()
    }
}