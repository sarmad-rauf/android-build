package com.es.marocapp.usecase.login.signup


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentSignUpDetailBinding
import com.es.marocapp.model.responses.GetAccountHolderInformationResponse
import com.es.marocapp.model.responses.GetInitialAuthDetailsReponse
import com.es.marocapp.model.responses.GetOtpForRegistrationResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import kotlinx.android.synthetic.main.layout_login_header.view.*
import java.util.*
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass.
 */
class SignUpDetailFragment : BaseFragment<FragmentSignUpDetailBinding>(), SignUpClickListner,
    TextWatcher {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_sign_up_detail
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

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
        mDataBinding.inputNationalID.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_CN_LENGTH.toInt()
            )
        )
        mDataBinding.inputNationalID.addTextChangedListener(this)

        subscribeObserver()

    }

    private fun subscribeObserver() {
        val mInitialAuthDetailsResonseObserver = Observer<GetInitialAuthDetailsReponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                //TODO first, last name and CNIC Hardcoded Issue need to reolve
                mActivityViewModel.requestForGetOTPForRegistrationApi(activity,mDataBinding.inputFirstName.text.toString().trim(),mDataBinding.inputLastName.text.toString().trim()
                    ,mDataBinding.inputNationalID.text.toString().trim())
            }
        }

        val mOTPForRegistrationResonseObserver = Observer<GetOtpForRegistrationResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as LoginActivity).navController.navigate(R.id.action_signUpDetailFragment_to_verifyNumberFragment)
            }
        }

        mActivityViewModel.getInitialAuthDetailsResponseListner.observe(this,mInitialAuthDetailsResonseObserver)
        mActivityViewModel.getOtpForRegistrationResponseListner.observe(this,mOTPForRegistrationResonseObserver)
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
                var selectedDate = "$year-$monthVal-$day"
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
                        0-> mDataBinding.inputGender.setText("MALE")
                        1-> mDataBinding.inputGender.setText("FEMALE")
                        2-> mDataBinding.inputGender.setText("OTHER")
                    }
                })
            .setPositiveButton("Ok", null)
            .show()
    }

    override fun onNextButtonClick(view: View) {

        //todo Validation needed to add
        mActivityViewModel.DOB = mDataBinding.inputDateOfBirth.text.toString().trim()
        mActivityViewModel.identificationNumber = mDataBinding.inputNationalID.text.toString().trim()
        mActivityViewModel.firstName = mDataBinding.inputFirstName.text.toString().trim()
        mActivityViewModel.gender = mDataBinding.inputGender.text.toString().trim()
        mActivityViewModel.postalAddress = mDataBinding.inputAddress.text.toString().trim()
        mActivityViewModel.lastName = mDataBinding.inputLastName.text.toString().trim()
        mActivityViewModel.email = mDataBinding.inputEmail.text.toString().trim()

        mActivityViewModel.requestForeGetInitialAuthDetailsApi(activity)
        //For Without API Calling Uncomment Below Line
//        (activity as LoginActivity).navController.navigate(R.id.action_signUpDetailFragment_to_verifyNumberFragment)
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

    override fun afterTextChanged(p0: Editable?) {
        var cnic = mDataBinding.inputNationalID.text.toString().trim()
        var cnicLength = cnic.length
        if(cnicLength > 0 && !Pattern.matches(Constants.APP_CN_REGEX, cnic)) {
            Log.d("CNIC","Cnic Matches")
        }else{
            Log.d("CNIC","Cnic NOt Match")
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


}
