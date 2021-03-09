package com.es.marocapp.usecase.payments.billpayment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.LayoutConfirmationBillPaymentBinding
import com.es.marocapp.databinding.LayoutSuccessBillPaymentBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel
import com.es.marocapp.utils.Constants

class PaymentConfirmationFragment: BaseFragment<LayoutConfirmationBillPaymentBinding>(),
    BillPaymentClickListener {

    lateinit var mActivityViewModel: PaymentsViewModel

    override fun setLayout(): Int {
        return R.layout.layout_confirmation_bill_payment
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)
        mDataBinding.apply {
            listener = this@PaymentConfirmationFragment
        }
        (activity as PaymentsActivity).setCompanyIconToolbarVisibility(false)
        (activity as PaymentsActivity).setToolabarVisibility(false)
        if(Constants.IS_AGENT_USER)
        {
            mDataBinding.tvDHTitle.visibility=View.GONE
            mDataBinding.tvDHVal.visibility=View.GONE
        }
    }

    override fun onBackClick(view: View) {
        (activity as PaymentsActivity).navController.navigateUp()
    }

    override fun onValidateClick(view: View) {
        (activity as PaymentsActivity).navController.navigate(R.id.action_paymentConfirmationFragment_to_paymentSuccessFragment)
    }


}