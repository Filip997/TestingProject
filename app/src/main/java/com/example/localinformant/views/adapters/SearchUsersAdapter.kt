package com.example.localinformant.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.databinding.SearchUsersAdapterDesignBinding
import com.example.localinformant.models.User

class SearchUsersAdapter(context: Context,
                         private var users: MutableList<User>,
                         private val onUserClicked: (String, String) -> Unit
) : RecyclerView.Adapter<SearchUsersAdapter.SearchUserViewHolder>(), Filterable {

    var usersListFiltered = users
    var usersListFull = users

    class SearchUserViewHolder(binding: SearchUsersAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        var profilePicture = binding.civSearchUserProfilePicture
        var userName = binding.tvSearchUserName
        var cardView = binding.searchCardView
    }

    fun updateList(newList: List<User>) {
        users.clear()
        users.addAll(newList)
        usersListFiltered = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        return SearchUserViewHolder(SearchUsersAdapterDesignBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        holder.userName.text = users[position].name

        holder.cardView.setOnClickListener {
            onUserClicked.invoke(users[position].id, users[position].type)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.lowercase()?.trim() ?: ""

                usersListFiltered = if (charString.isEmpty())
                    usersListFull
                else {
                    val filteredList: MutableList<User> = mutableListOf()
                    for (item in users) {
                        if (item.name.lowercase().startsWith(charString))
                            filteredList.add(item)
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = usersListFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                users = results?.values as MutableList<User>
                notifyDataSetChanged()
            }
        }
    }
}