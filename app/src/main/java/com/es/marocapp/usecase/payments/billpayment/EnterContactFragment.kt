package com.es.marocapp.usecase.payments.billpayment


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentEnterContactBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel

class EnterContactFragment : BaseFragment<FragmentEnterContactBinding>(),
    BillPaymentClickListener {

    lateinit var mActivityViewModel: PaymentsViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_enter_contact
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)
        mDataBinding.apply {
            listener = this@EnterContactFragment
        }

        (activity as PaymentsActivity).setCompanyIconToolbarVisibility(true)
        (activity as PaymentsActivity).setToolabarVisibility(true)
    }

    override fun onBackClick(view: View) {
        (activity as PaymentsActivity).navController.navigateUp()
    }

    override fun onValidateClick(view: View) {
        (activity as PaymentsActivity).navController.navigate(R.id.action_enterContactFragment_to_paymentConfirmationFragment)
    }


}
