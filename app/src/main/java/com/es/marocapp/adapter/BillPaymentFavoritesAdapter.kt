package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.responses.Contact
import com.es.marocapp.model.responses.Creancier

class BillPaymentFavoritesAdapter(private val favContacts: ArrayList<Contact>,
                                  var listner: BillPaymentFavoriteClickListner) : RecyclerView.Adapter<BillPaymentFavoritesAdapter.BillPaymentFirstLetterItemViewHolder>() {

    override fun getItemCount() = favContacts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillPaymentFirstLetterItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_top_pop_up_fatoratie_favortie, parent, false)
        return BillPaymentFirstLetterItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillPaymentFirstLetterItemViewHolder, position: Int) {
        /*holder.paymentItem.text = paymentItems[position].nomCreancier
        var name = paymentItems[position].nomCreancier
        holder.paymentItemIcon.text = name[0].toString()*/
        if(favContacts[position].contactName.contains("Telec_Internet@")){
            holder.fav_main_image.visibility = View.GONE
            holder.fav_main_image.setImageResource(R.drawable.internet_blue)

            holder.fav_telecom_bill_img.visibility = View.VISIBLE
            var name = favContacts[position].contactName
            name = name.substringBefore("@")
            name = name.substringAfter("_")
            holder.fav_telecom_bill_img.text = name[0].toString()

            /*var number = favContacts[position].fri
            number = number.substringBefore("@")
            number = number.substringBefore("/")*/
            var nickName = favContacts[position].contactName
            nickName = nickName.substringAfter("@")
            nickName = nickName.substringBefore(",")
            holder.fav_name.text = nickName
        }else if(favContacts[position].contactName.contains("Telec_PostpaidMobile@")){
            holder.fav_main_image.visibility = View.GONE
            holder.fav_main_image.setImageResource(R.drawable.postpaid_blue)

            holder.fav_telecom_bill_img.visibility = View.VISIBLE
            var name = favContacts[position].contactName
            name = name.substringBefore("@")
            name = name.substringAfter("_")
            holder.fav_telecom_bill_img.text = name[0].toString()

            /*var number = favContacts[position].fri
            number = number.substringBefore("@")
            number = number.substringBefore("/")*/
            var nickName = favContacts[position].contactName
            nickName = nickName.substringAfter("@")
            nickName = nickName.substringBefore(",")
            holder.fav_name.text = nickName
        }else if(favContacts[position].contactName.contains("Telec_PostpaidFix@")){
            holder.fav_main_image.visibility = View.GONE
            holder.fav_main_image.setImageResource(R.drawable.postpaid_fix_blue)

            holder.fav_telecom_bill_img.visibility = View.VISIBLE
            var name = favContacts[position].contactName
            name = name.substringBefore("@")
            name = name.substringAfter("_")
            holder.fav_telecom_bill_img.text = name[0].toString()

            /*var number = favContacts[position].fri
            number = number.substringBefore("@")
            number = number.substringBefore("/")*/
            var nickName = favContacts[position].contactName
            nickName = nickName.substringAfter("@")
            nickName = nickName.substringBefore(",")
            holder.fav_name.text = nickName
        }else if(favContacts[position].contactName.contains("Util_")){
            holder.fav_main_image.visibility = View.GONE
            holder.fav_telecom_bill_img.visibility = View.VISIBLE

            var name = favContacts[position].contactName
            name = name.substringBefore("@")
            name = name.substringAfter("_")
            holder.fav_telecom_bill_img.text = name[0].toString()

            /*var number = favContacts[position].fri
            number = number.substringBefore("@")*/
            var nickName = favContacts[position].contactName
            nickName = nickName.substringAfter("@")
            nickName = nickName.substringBefore(",")
            holder.fav_name.text = nickName
        }

        holder.img_delete_favorite.setOnClickListener {
            listner.onDeleteFavoriteItemTypeClick(favContacts[position])
        }

        holder.billPaymentFavContainer.setOnClickListener{
            listner.onFavoriteItemTypeClick(favContacts[position])
        }
    }

    class BillPaymentFirstLetterItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var img_delete_favorite : ImageView = view.findViewById(R.id.img_delete_favorite)
        var fav_main_image : ImageView = view.findViewById(R.id.fav_main_image)
        var fav_telecom_bill_img : TextView = view.findViewById(R.id.fav_telecom_bill_img)
        var fav_name : TextView = view.findViewById(R.id.fav_name)
        var billPaymentFavContainer : ConstraintLayout = view.findViewById(R.id.billPaymentFavContainer)
    }


    interface BillPaymentFavoriteClickListner{
        fun onFavoriteItemTypeClick(selectedContact : Contact)
        fun onDeleteFavoriteItemTypeClick(selectedContact : Contact)
    }
}