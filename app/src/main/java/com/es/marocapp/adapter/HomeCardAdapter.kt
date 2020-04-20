package com.es.marocapp.adapter

import android.graphics.Movie
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.CardModel
import com.github.islamkhsh.CardSliderAdapter

class HomeCardAdapter(private val card : ArrayList<CardModel>) : CardSliderAdapter<HomeCardAdapter.CardViewHolder>() {

    override fun getItemCount() = card.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_card_details, parent, false)
        return CardViewHolder(view)
    }

    override fun bindVH(holder: CardViewHolder, position: Int) {
        holder.cardName.text = card[position].cardName
        holder.cardNumber.text = card[position].cardNumber
        holder.cardBalance.text = card[position].cardBalance
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var cardName : TextView = view.findViewById(R.id.tvCardTitle)
        var cardNumber : TextView = view.findViewById(R.id.tvCardNumber)
        var cardBalance : TextView = view.findViewById(R.id.tvCardBalance)
    }
}