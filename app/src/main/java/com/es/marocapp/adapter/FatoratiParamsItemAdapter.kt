package com.es.marocapp.adapter

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.responses.Param
import com.es.marocapp.model.responses.RecievededParam
import com.google.android.material.textfield.TextInputLayout

class FatoratiParamsItemAdapter (private val paramItems : List<RecievededParam>,
                                 var listner :ParamTextChangedListner) : RecyclerView.Adapter<FatoratiParamsItemAdapter.FavoritesItemViewHolder>() {

    override fun getItemCount() = paramItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fatourati_param_adapter_style, parent, false)
        return FavoritesItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesItemViewHolder, position: Int) {
        holder.inputLayout.hint = ""
        holder.inputTextHint.text = paramItems[position].libelle
        holder.inputText.text = paramItems[position].inputValue
        //holder.inputText.hint=paramItems[position].nomChamp
        holder.inputLayout.error = paramItems[position].errorText
        holder.inputLayout.isErrorEnabled = paramItems[position].errorEnabled
        holder.inputTextHint.visibility = paramItems[position].hintVisibility

        if (paramItems[position].typeChamp.contains("text")){
            holder.inputText.inputType=InputType.TYPE_CLASS_TEXT
        }
        else{
            holder.inputText.inputType=InputType.TYPE_CLASS_PHONE
        }

        holder.inputText.addTextChangedListener(object :TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                listner.onParamTextChangedClick(holder.inputText.text.toString().trim(),position)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    class FavoritesItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var inputLayout : TextInputLayout = view.findViewById(R.id.input_layout_phone_number)
        var inputText : TextView = view.findViewById(R.id.input_phone_number)
        var inputTextHint : TextView = view.findViewById(R.id.input_phone_number_hint)
    }


    interface ParamTextChangedListner{
        fun onParamTextChangedClick(itemType: String, position: Int)
    }
}