package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.responses.Contact

class ViewFavoritesAdapter(private val contacts: ArrayList<Contact>,var listner : ViewFavoritesClickListner) :
    RecyclerView.Adapter<ViewFavoritesAdapter.ViewFavoritesViewHolder>() {

    override fun getItemCount() = contacts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewFavoritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_view_favorites_row, parent, false)
        return ViewFavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewFavoritesViewHolder, position: Int) {
        var contactName = contacts[position].contactname
//        if (contactName.contains("Util_"))
//        {
            contactName = contactName.substringAfter("@")
            contactName = contactName.substringBefore(",")
//        }
//        else{
//            contactName = contactName.substringAfter("@")
//            contactName = contactName.substringAfter("@")
//        }
        holder.favortiesName.text = contactName
        var contactNimber = contacts[position].customerreference
        contactNimber = contactNimber.substringBefore("/")
        contactNimber = contactNimber.substringBefore("@")
        holder.favoriteNumber.text = contactNimber

        if(position.equals(contacts.size-1)){
            holder.favoritesListDivider.visibility = View.GONE
        }

        holder.deleteFavoriteIcon.setOnClickListener {
            listner.onFavoritesItemClickListner(contacts[position])
        }
    }

    class ViewFavoritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var favortiesName: TextView = view.findViewById(R.id.tvFavortieName)
        var favoriteNumber: TextView = view.findViewById(R.id.tvFavortieNumber)
        var favoritesListDivider: ImageView = view.findViewById(R.id.view_favorite_dotted_line)
        var deleteFavoriteIcon: ImageView = view.findViewById(R.id.imgDeleteFavorite)
    }


    interface ViewFavoritesClickListner {
        fun onFavoritesItemClickListner(contact: Contact)
    }
}