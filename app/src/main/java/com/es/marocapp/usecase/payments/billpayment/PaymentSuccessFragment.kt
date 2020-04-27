package com.es.marocapp.usecase.payments.billpayment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.LayoutSuccessBillPaymentBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel

class PaymentSuccessFragment : BaseFragment<LayoutSuccessBillPaymentBinding>(),
    BillPaymentClickListener {

    lateinit var mActivityViewModel: PaymentsViewModel

    override fun setLayout(): Int {
        return R.layout.layout_success_bill_payment
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)
        mDataBinding.apply {
            listener = this@PaymentSuccessFragment
        }
        (activity as PaymentsActivity).setCompanyIconToolbarVisibility(false)
        (activity as PaymentsActivity).setToolabarVisibility(false)
    }

    override fun onBackClick(view: View) {
        (activity as PaymentsActivity).navController.navigateUp()
    }

    override fun onValidateClick(view: View) {
        (activity as PaymentsActivity).navController.popBackStack(R.id.billPaymentTypeFragment,false)
    }


}
