package com.example.localinformant.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.databinding.CompanyPostsAdapterDesignBinding
import com.example.localinformant.models.Post
import com.example.localinformant.models.User

class CompanyPostsAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapter<CompanyPostsAdapter.CompanyPostsViewModel>() {

    class CompanyPostsViewModel(binding: CompanyPostsAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val companyName = binding.tvCompanyName
        val postText = binding.tvCompanyPostText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyPostsViewModel {
        return CompanyPostsViewModel(CompanyPostsAdapterDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: CompanyPostsViewModel, position: Int) {
        holder.companyName.text = posts[position].companyName
        holder.postText.text = posts[position].postText
    }

    fun updateList(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}