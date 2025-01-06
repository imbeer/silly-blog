package com.example.blog_android_app.view.feed

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.R

class ImagePagerAdapter(private val bitmapList: MutableList<Bitmap?>) :
    RecyclerView.Adapter<ImagePagerAdapter.BitmapViewHolder>() {

    class BitmapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BitmapViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return BitmapViewHolder(view)
    }

    override fun onBindViewHolder(holder: BitmapViewHolder, position: Int) {
        holder.imageView.setImageBitmap(bitmapList[position])
    }

    override fun getItemCount(): Int = bitmapList.size
}
