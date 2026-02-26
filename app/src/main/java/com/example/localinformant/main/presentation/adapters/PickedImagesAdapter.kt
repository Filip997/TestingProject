package com.example.localinformant.main.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.databinding.PickedImagesAdapterDesignBinding

class PickedImagesAdapter(
    private val uris: MutableList<Uri>
) : RecyclerView.Adapter<PickedImagesAdapter.PickedImagesViewHolder>() {

    class PickedImagesViewHolder(binding: PickedImagesAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val pickedImage = binding.ivPickedImage
        val removeBtn = binding.ivRemoveBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedImagesViewHolder {
        return PickedImagesViewHolder(PickedImagesAdapterDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = uris.size

    override fun onBindViewHolder(holder: PickedImagesViewHolder, position: Int) {
        holder.pickedImage.setImageURI(uris[position])

        holder.removeBtn.setOnClickListener {
            uris.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun updateList(newUris: List<Uri>) {
        uris.clear()
        uris.addAll(newUris)
        notifyDataSetChanged()
    }

    fun getUris() = uris
}