package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R

class FavoritesTypeItemAdapter (private val favoriteItems : ArrayList<String>, var listner : FavoritesItemTypeClickListner) : RecyclerView.Adapter<FavoritesTypeItemAdapter.FavoritesItemViewHolder>() {

    override fun getItemCount() = favoriteItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.payment_items_row_layout, parent, false)
        return FavoritesItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesItemViewHolder, position: Int) {
        holder.paymentItem.text = favoriteItems[position]

        holder.mPaymentItemLayout.setOnClickListener {
            listner.onFavoriteItemTypeClick(favoriteItems[position])
        }
    }

    class FavoritesItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var paymentItem : TextView = view.findViewById(R.id.payment_type_name)
        var mPaymentItemLayout : ConstraintLayout = view.findViewById(R.id.containerLayout)
    }


    interface FavoritesItemTypeClickListner{
        fun onFavoriteItemTypeClick(itemType : String)
    }
}