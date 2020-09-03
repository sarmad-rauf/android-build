package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.responses.Approvaldetail

class AirTimeDataAdpater (private val airTimeData: ArrayList<String>,
                          var listner: AirTimeDataClickLisnter) : RecyclerView.Adapter<AirTimeDataAdpater.AirTimeDataItemViewHolder>() {

    override fun getItemCount() = airTimeData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirTimeDataItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_air_time_data_row, parent, false)
        return AirTimeDataItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AirTimeDataItemViewHolder, position: Int) {
        holder.airTimeItemName.text = airTimeData[position]

        holder.mAirTimeItemLayout.setOnClickListener {
            listner.onSelectedAirTimeData(airTimeData[position])
        }
    }

    class AirTimeDataItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var airTimeItemName : TextView = view.findViewById(R.id.tvAirTimeDataItem)
        var mAirTimeItemLayout : ConstraintLayout = view.findViewById(R.id.containerLayout)
    }


    interface AirTimeDataClickLisnter{
        fun onSelectedAirTimeData(position: String)
    }
}