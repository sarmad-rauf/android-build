package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.HomeUseCasesModel

class HomeUseCasesAdapter(private val usecases : ArrayList<HomeUseCasesModel>) : RecyclerView.Adapter<HomeUseCasesAdapter.HomeUseCasesViewHolder>() {

    override fun getItemCount() = usecases.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeUseCasesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.use_case_item_layout, parent, false)
        return HomeUseCasesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeUseCasesViewHolder, position: Int) {
        holder.useCaseTitle.text = usecases[position].useCaseTitle
        holder.useCaseImage.setImageResource(usecases[position].useCaseImage)

        if(position==4){
           // holder.useCaseParenttLayout.setBackgroundResource(R.color.colorBtnBlue)
            holder.useCaseChildtLayout.setBackgroundResource(R.color.colorBtnBlue)
        }else{
           // holder.useCaseParenttLayout.setBackgroundResource(R.color.colorWhite)
            holder.useCaseChildtLayout.setBackgroundResource(R.color.colorWhite)
        }
    }

    class HomeUseCasesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var useCaseTitle : TextView = view.findViewById(R.id.useCaseTitle)
        var useCaseImage : ImageView = view.findViewById(R.id.useCaseImageView)
        var useCaseParenttLayout : ConstraintLayout = view.findViewById(R.id.useCasesParentLayout)
        var useCaseChildtLayout : ConstraintLayout = view.findViewById(R.id.useCaseChildLayout)
    }

}