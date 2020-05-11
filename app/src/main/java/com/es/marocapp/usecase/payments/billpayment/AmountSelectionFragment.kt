package com.es.marocapp.usecase.payments.billpayment

import android.os.Bundle
import android.view.View
import com.es.marocapp.R
import com.es.marocapp.databinding.LayoutAmountSelectionBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsActivity


class AmountSelectionFragment : BaseFragment<LayoutAmountSelectionBinding>(),
    BillPaymentClickListener {


    override fun setLayout(): Int {
        return R.layout.layout_amount_selection
    }

    override fun init(savedInstanceState: Bundle?) {
        mDataBinding.apply {
            listener = this@AmountSelectionFragment
        }

        (activity as PaymentsActivity).setCompanyIconToolbarVisibility(false)
        (activity as PaymentsActivity).setToolabarVisibility(true)
    }

    override fun onBackClick(view: View) {
    }

    override fun onValidateClick(view: View) {
        (activity as PaymentsActivity).navController.navigate(R.id.action_amountSelectionFragment_to_paymentConfirmationFragment)
    }


}
