package com.vsu.picstorm.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.vsu.picstorm.databinding.BottomNavViewBinding

class BottomNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    val binding = BottomNavViewBinding.inflate(LayoutInflater.from(context), this)
}