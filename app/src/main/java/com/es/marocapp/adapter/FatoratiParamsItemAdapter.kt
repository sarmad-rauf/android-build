package com.es.marocapp.adapter

import android.annotation.SuppressLint
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
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.RecievededParam
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.utils.Constants
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
    val TSAVROW=2
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
        else if(viewType==INPUT_FIELD){
           view = LayoutInflater.from(parent.context).inflate(
               R.layout.fatourati_param_adapter_style,
               parent,
               false
           )
           return FavoritesItemViewHolder(view)
        }
       else{
           view = LayoutInflater.from(parent.context).inflate(
               R.layout.bill_payment_tsav_row,
               parent,
               false
           )
           return TsavItemViewHolder(view)
       }
    }

    @SuppressLint("ResourceAsColor")
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
        else if(viewType==SPINNER) {
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
            val spinnervals: Array<String> = paramItems[position].listVals.toTypedArray()
            spinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activityy as BillPaymentActivity,
                    spinnervals,
                    (activityy as BillPaymentActivity).resources.getColor(R.color.colorBlack),true
                )
            spinnerHolder.spinnerField.apply { adapter=spinnerAdapter }

        }
        else{
            val tsavHolder: TsavItemViewHolder = holder as TsavItemViewHolder
            tsavHolder.spinnerFieldTitile.isEnabled =false
            if(paramItems[position].libelle.contains(LanguageData.getStringValue("invalid").toString()))
            {
                tsavHolder.spinnerFieldTitile.setTextColor(R.color.colorRed)
            }
            else{
                tsavHolder.spinnerFieldTitile.setTextColor(R.color.colorBlack)
            }
            tsavHolder.spinnerFieldTitile.text=paramItems[position].libelle
            val firstvalue=paramItems[position].firstValue
            val secondValue=paramItems[position].secondValue
            com.es.marocapp.utils.Logger.debugLog("billpayment","Tsav51 ${paramItems[position].firstValue}    ${paramItems[position].secondValue}  ")
            if(firstvalue.isEmpty())
            {
                tsavHolder.tsav1.hint=LanguageData.getStringValue("MatriculePlaceholder1").toString()
            }
            else{
                tsavHolder.tsav1.setText(firstvalue)
            }
            if(secondValue.isEmpty())
            {
                tsavHolder.tsav2.hint=LanguageData.getStringValue("MatriculePlaceholder2").toString()
            }
            else{
                com.es.marocapp.utils.Logger.debugLog("billpayment","Tsav51   ${paramItems[position].secondValue}  ")
                tsavHolder.tsav2.setText(secondValue)
            }
            if(firstvalue.isEmpty()&&paramItems[position].errorEnabled) {
                tsavHolder.tsav1Layout.error = paramItems[position].errorText
                tsavHolder.tsav1Layout.isErrorEnabled = paramItems[position].errorEnabled
            }
            if(secondValue.isEmpty()&&paramItems[position].errorEnabled) {
                tsavHolder.tsav2Layout.error = paramItems[position].errorText
                tsavHolder.tsav2Layout.isErrorEnabled = paramItems[position].errorEnabled
            }

            tsavHolder.spinnerField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, positionn: Int, id: Long) {
                    listner.onTsavSpinnerTextChangedClick(holder.tsav1.text.toString().trim(),holder.tsav2.text.toString().trim(),Constants.fatouratiTsavMatriculeDdVals[positionn].trim(),position)
                    Constants.selectedTSAVSpinnerPosition=positionn
                }
            }
            val spinnervals: Array<String> = Constants.fatouratiTsavMatriculeDdVals
            spinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activityy as BillPaymentActivity,
                    spinnervals,
                    (activityy as BillPaymentActivity).resources.getColor(R.color.colorBlack),true
                )
            tsavHolder.spinnerField.apply { adapter=spinnerAdapter }
           // com.es.marocapp.utils.Logger.debugLog("billpayment","Tsav51   ${Constants.selectedTSAVSpinnerPosition}  ")
            tsavHolder.spinnerField.setSelection(Constants.selectedTSAVSpinnerPosition,true)


            tsavHolder.tsav1.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    var selectedSpinnerPosition= Constants.selectedTSAVSpinnerPosition
                    if(selectedSpinnerPosition==null)
                    {
                        selectedSpinnerPosition=0
                    }
                    listner.onTsavTextChangedClick(holder.tsav1.text.toString().trim(),holder.tsav2.text.toString().trim(),Constants.fatouratiTsavMatriculeDdVals[selectedSpinnerPosition].trim(),position)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
            tsavHolder.tsav2.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    var selectedSpinnerPosition= Constants.selectedTSAVSpinnerPosition
                    if(selectedSpinnerPosition==null)
                    {
                        selectedSpinnerPosition=0
                    }
                    listner.onTsavTextChangedClick(holder.tsav1.text.toString().trim(),holder.tsav2.text.toString().trim(),Constants.fatouratiTsavMatriculeDdVals[selectedSpinnerPosition].trim(),position)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })

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

    class TsavItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var spinnerField : Spinner = view.findViewById(R.id.spinnerField)
        var spinnerFieldTitile : TextView = view.findViewById(R.id.spinnerFieldTitile)
        var tsav1 : EditText = view.findViewById(R.id.tsav1)
        var tsav2 : EditText = view.findViewById(R.id.tsav2)
        var tsav1Layout : TextInputLayout = view.findViewById(R.id.tsav1_layout)
        var tsav2Layout : TextInputLayout = view.findViewById(R.id.tsav2_layout)
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
        if(paramItems[position].libelle.contains("Immatriculation"))
        {
            return TSAVROW
        }
        else if(paramItems[position].listVals.isNullOrEmpty())
        {
            return INPUT_FIELD
        }
        else{
            return SPINNER
        }

    }


    interface ParamTextChangedListner{
        fun onParamTextChangedClick(itemType: String, position: Int)
        fun onTsavTextChangedClick(firstVal: String,secondVal: String,spinnerVal: String, position: Int)
        fun onSpinnerTextChangedClick(itemType: String, position: Int)
        fun onTsavSpinnerTextChangedClick(firstVal: String,secondVal: String,spinnerVal: String, position: Int)
    }
}