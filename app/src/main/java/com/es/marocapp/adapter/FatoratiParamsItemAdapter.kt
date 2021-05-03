package com.es.marocapp.adapter

import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.responses.RecievededParam
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.google.android.material.textfield.TextInputLayout
import java.util.logging.Logger

class FatoratiParamsItemAdapter(
    activity: FragmentActivity?,
    private val paramItems: List<RecievededParam>,
    var listner: ParamTextChangedListner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val activityy=activity
    val INPUT_FIELD=0
    val SPINNER=1
    lateinit var spinnerAdapter: LanguageCustomSpinnerAdapter
    override fun getItemCount() = paramItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? =null
       if(viewType==SPINNER)
       {
           view = LayoutInflater.from(parent.context).inflate(
               R.layout.bill_payment_lydec_spinner_row,
               parent,
               false
           )
           return SpinnerItemViewHolder(view)
       }
        else{
           view = LayoutInflater.from(parent.context).inflate(
               R.layout.fatourati_param_adapter_style,
               parent,
               false
           )
           return FavoritesItemViewHolder(view)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if(viewType==INPUT_FIELD){
            val fieldHolder: FavoritesItemViewHolder =
                holder as FavoritesItemViewHolder
            fieldHolder.inputLayout.hint = ""
            fieldHolder.inputTextHint.text = paramItems[position].libelle
            fieldHolder.inputText.text = paramItems[position].inputValue
        //holder.inputText.hint=paramItems[position].nomChamp
            fieldHolder.inputLayout.error = paramItems[position].errorText
            fieldHolder.inputLayout.isErrorEnabled = paramItems[position].errorEnabled
            fieldHolder.inputTextHint.visibility = paramItems[position].hintVisibility
//            if(paramItems[position].listVals.isNullOrEmpty())
//            {
//               fieldHolder.inputText.imeOptions=EditorInfo.IME_ACTION_NEXT
//            }
//            else{
//                fieldHolder.inputText.imeOptions=EditorInfo.IME_ACTION_DONE
//            }
        if (paramItems[position].typeChamp.contains("text")){
            fieldHolder.inputText.inputType=InputType.TYPE_CLASS_TEXT
        }
        else{
            fieldHolder.inputText.inputType=InputType.TYPE_CLASS_PHONE
        }

            fieldHolder.inputText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                listner.onParamTextChangedClick(fieldHolder.inputText.text.toString().trim(), position)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }
        else{
            val spinnerHolder: SpinnerItemViewHolder = holder as SpinnerItemViewHolder
            spinnerHolder.spinnerFieldTitile.isEnabled =false
            spinnerHolder.spinnerFieldTitile.text=paramItems[position].libelle
            spinnerHolder.spinnerField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, positionn: Int, id: Long) {
                  listner.onSpinnerTextChangedClick(paramItems[position].listVals[positionn],position)
                }
            }
            val acountTypeArray: Array<String> = paramItems[position].listVals.toTypedArray()
            spinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activityy as BillPaymentActivity,
                    acountTypeArray,
                    (activityy as BillPaymentActivity).resources.getColor(R.color.colorBlack),true
                )
            spinnerHolder.spinnerField.apply { adapter=spinnerAdapter }

        }
    }

    class FavoritesItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var inputLayout : TextInputLayout = view.findViewById(R.id.input_layout_phone_number)
        var inputText : TextView = view.findViewById(R.id.input_phone_number)
        var inputTextHint : TextView = view.findViewById(R.id.input_phone_number_hint)
    }

    class SpinnerItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var spinnerField : Spinner = view.findViewById(R.id.spinnerField)
        var spinnerFieldTitile : TextView = view.findViewById(R.id.spinnerFieldTitile)
        var dummy : EditText = view.findViewById(R.id.dummy)
    }

    //    @Override
    //    public int getItemCount() {
    //        return videoList.size();
    //    }
    //    @Override
    //    public int getItemViewType(int position) {
    //        if(videoList.get(position)==null)
    //            return AD_TYPE;
    //        return CONTENT_TYPE;
    //    }
    override fun getItemViewType(position: Int): Int {
        if(paramItems[position].listVals.isNullOrEmpty())
        {
            return INPUT_FIELD
        }
        else{
            return SPINNER
        }

    }


    interface ParamTextChangedListner{
        fun onParamTextChangedClick(itemType: String, position: Int)
        fun onSpinnerTextChangedClick(itemType: String, position: Int)
    }
}