package com.es.marocapp.adapter

import android.content.Context
import android.text.BidiFormatter
import android.text.TextDirectionHeuristics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.es.marocapp.R
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.FaqsAnswers
import com.es.marocapp.model.FaqsQuestionModel
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import java.util.*


class FaqsQuestionAnswerAdapter(val context : Context,groups : List<ExpandableGroup<*>>?): ExpandableRecyclerViewAdapter<FaqsQuestionAnswerAdapter.FaqQuestionViewHolder, FaqsQuestionAnswerAdapter.FaqAnswerViewHolder>(groups){

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): FaqQuestionViewHolder {
        val itemView =
            LayoutInflater.from(parent?.context).inflate(R.layout.layout_faq_question, parent, false)
        return FaqQuestionViewHolder(itemView)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): FaqAnswerViewHolder {
        val itemView =
            LayoutInflater.from(parent?.context).inflate(R.layout.layout_faq_answer, parent, false)
        return FaqAnswerViewHolder(itemView)
    }

    override fun onBindChildViewHolder(
        holder: FaqAnswerViewHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val answer: FaqsAnswers = group?.items?.get(childIndex) as FaqsAnswers
        holder?.bind(answer)
    }

    override fun onBindGroupViewHolder(
        holder: FaqQuestionViewHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val faqQuestion: FaqsQuestionModel = group as FaqsQuestionModel
        holder?.bind(faqQuestion,isGroupExpanded(flatPosition),context)
    }

    class FaqAnswerViewHolder(itemView: View) : ChildViewHolder(itemView) {
        val faqAnswer = itemView.findViewById<TextView>(R.id.faqAnswerTV)

        fun bind(faqQuestionAnswers: FaqsAnswers) {
            faqAnswer.text = faqQuestionAnswers.answer
            if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_AR)){

                faqAnswer.text = "\u200E" + faqQuestionAnswers.answer?.replace("MT Cash","\u200EMT Cash")
                faqAnswer.gravity = Gravity.END
            }else{
                faqAnswer.text = faqQuestionAnswers.answer
            }
        }
    }

    class FaqQuestionViewHolder(itemView: View) : GroupViewHolder(itemView) {
        val faqQuestion = itemView.findViewById<TextView>(R.id.faqQuestionTV)

        fun bind(
            continent: FaqsQuestionModel,
            groupExpanded: Boolean,
            context: Context
        ) {

            if(groupExpanded){
                faqQuestion.setTextColor(context.resources.getColor(R.color.colorTextOrange))
            }else{
                faqQuestion.setTextColor(context.resources.getColor(R.color.colorBlack))
            }

            if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_AR)){
                //  val text ="\u200E"+ continent.question?.replace("MT Cash","\u200EMT Cash")
                // faqQuestion.text =  text
                     faqQuestion.text =  continent.question?.trim()

                  faqQuestion.gravity = Gravity.END
            }else{
                 faqQuestion.text = continent.question?.trim()
            }



                  //  faqQuestion.text =    BidiFormatter.getInstance(Locale.getDefault()).unicodeWrap(continent.question, TextDirectionHeuristics.ANYRTL_LTR);



        }

    }

}