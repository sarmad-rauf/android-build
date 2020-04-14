package com.ens.maroc.usecase.login.signup


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ens.maroc.R
import com.ens.maroc.databinding.FragmentSignUpDetailBinding
import com.ens.maroc.usecase.BaseFragment
import com.ens.maroc.usecase.login.LoginActivity
import com.ens.maroc.usecase.login.LoginActivityViewModel
import kotlinx.android.synthetic.main.layout_login_header.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SignUpDetailFragment : BaseFragment<FragmentSignUpDetailBinding>(), SignUpClickListner {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_sign_up_detail
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@SignUpDetailFragment
        }

        mDataBinding.root.groupBack.visibility = View.VISIBLE
        mDataBinding.root.txtHeaderTitle.text = getString(R.string.create_your_account)

        mDataBinding.root.txtBack.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.root.imgBackButton.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }


    }

    private fun showDatePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activity as LoginActivity,
            OnDateSetListener { datePicker, year, month, day ->
                var monthVal = (month+1).toString()
                var selectedDate = "$day/$monthVal/$year"
                mDataBinding.inputDateOfBirth.setText(selectedDate)
            }, year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun showGenderDialog(){
        val singleChoiceItems =
            resources.getStringArray(R.array.dialog_gender_choice_array)
        val itemSelected = 0
        AlertDialog.Builder(activity)
            .setTitle("Select your gender")
            .setSingleChoiceItems(
                singleChoiceItems,
                itemSelected,
                DialogInterface.OnClickListener { dialogInterface, selectedIndex ->
                    when(selectedIndex){
                        0-> mDataBinding.inputGender.setText("Male")
                        1-> mDataBinding.inputGender.setText("Female")
                        2-> mDataBinding.inputGender.setText("Other")
                    }
                })
            .setPositiveButton("Ok", null)
            .show()
    }

    override fun onNextButtonClick(view: View) {
        (activity as LoginActivity).navController.navigate(R.id.action_signUpDetailFragment_to_verifyNumberFragment)
    }

    override fun onBackButtonClick(view: View) {

    }

    override fun onCalenderCalenderClick(view: View) {
        showDatePickerDialog()
    }

    override fun onGenderSelectionClick(view: View) {
        mDataBinding.inputGender.setText("Male")
        showGenderDialog()
    }


}
