package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class PhotosAdapter internal constructor(context: Context?, private val listUri: ArrayList<Uri>) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView: View = inflater.inflate(R.layout.photo_item, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val uri: Uri = listUri[position]
        holder.bindPhoto(uri)
    }

    override fun getItemCount(): Int {
        return listUri.size
    }

    class ViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imageView : ImageView = view.findViewById(R.id.photoView) as ImageView

        fun bindPhoto(uri: Uri) {
            Glide.with(view.context).load(uri).into(imageView)
        }
    }
}
