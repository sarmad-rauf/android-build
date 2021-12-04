package com.es.marocapp.usecase.transaction

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PDFPrint
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentTransactionDetailsBinding

import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.CustomModelHistoryItem
import com.es.marocapp.model.responses.History
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import com.google.android.material.snackbar.Snackbar
import com.tejpratapsingh.pdfcreator.utils.FileManager
import com.tejpratapsingh.pdfcreator.utils.PDFUtil
import java.io.*
import java.net.URI
import java.util.jar.Manifest
import okhttp3.ResponseBody
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class TransactionDetailsActivity : BaseActivity<FragmentTransactionDetailsBinding>(),TransactionDownloadRecipt{

    lateinit var mActivityViewModel: TransactionViewModel
    private lateinit var mItemDetailsToShow : History
    private var amount = ""
    private var fee = ""

    override fun setLayout(): Int {
        return R.layout.fragment_transaction_details
    }

    override fun onRestart() {
        super.onRestart()
      //  startNewActivityAndClear(Intent(this,MainActivity::class.java))
       // startNewActivityAndClear(this, MainActivity::class.java)
        finish()
    }
    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this@TransactionDetailsActivity)[TransactionViewModel::class.java]
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }



        mItemDetailsToShow = Constants.currentTransactionItem
        mDataBinding.imgBackButton.setOnClickListener {
            this@TransactionDetailsActivity.finish()
        }
        if(!mItemDetailsToShow.showReceipt){
        mDataBinding.btnDownloadPdf.visibility  = View.GONE
        }
        setStrings()
        updateUI()
        subscribeObservers()
        mDataBinding.btnDownloadPdf.setOnClickListener {
            mActivityViewModel.isLoading.set(true)
            mActivityViewModel.requestForGetDownloadRecipTemplateApi(this,Constants.CURRENT_USER_MSISDN,mItemDetailsToShow.transactionid)
        }
    }

    private fun setStrings() {
        mDataBinding.btnDownloadPdf.text = LanguageData.getStringValue("DownloadReceipt")
        mDataBinding.statusTitle.text = LanguageData.getStringValue("TransactionStatus")
        mDataBinding.tvTransactionHistoryTitle.text = LanguageData.getStringValue("TransactionDetails")
        mDataBinding.dateTitle.text = LanguageData.getStringValue("Date")
        mDataBinding.transactionIDTitle.text = LanguageData.getStringValue("TransactionID")
        mDataBinding.ReceiverNameTitle.text = LanguageData.getStringValue("ReceiverIdentity")
        mDataBinding.ReceiverIdentityTitle.text = LanguageData.getStringValue("ReceiverNumber")
        mDataBinding.SenderNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.SenderIdentityTitle.text = LanguageData.getStringValue("SenderNumber")
        mDataBinding.amountTitle.text = LanguageData.getStringValue("Amount")
        mDataBinding.feeTitle.text = LanguageData.getStringValue("Fee")
        mDataBinding.totalAmountTitle.text = LanguageData.getStringValue("Total")
    }

    private fun updateUI(){
        //status
        if(mItemDetailsToShow.transactionstatus.isNullOrEmpty()){
            mDataBinding.statusVal.text = "-"

        }else{
            if(!mItemDetailsToShow.transactionstatus.equals("SUCCESSFUL"))
            {
                mDataBinding.btnDownloadPdf.visibility= View.GONE
            }
            mDataBinding.statusVal.text = mItemDetailsToShow.transactionstatus
        }

        //Date
        if(mItemDetailsToShow.date.isNullOrEmpty()){
            mDataBinding.dateVal.text = "-"
        }else{
            mDataBinding.dateVal.text = Constants.getZoneFormattedDateAndTime(mItemDetailsToShow.date)
        }

        //TransactionID
        if(mItemDetailsToShow.transactionid.isNullOrEmpty()){
            mDataBinding.transactionIDVal.text = "-"
        }else{

            mDataBinding.transactionIDVal.text = mItemDetailsToShow.transactionid
        }

        //ReceiverName
        if(mItemDetailsToShow.toname.isNullOrEmpty()){
            mDataBinding.ReceiverNameVal.text = "-"
        }else{
            mDataBinding.ReceiverNameVal.text = mItemDetailsToShow.toname
        }

        //ReceiverNumber
        if(mItemDetailsToShow.tofri.isNullOrEmpty()){
            mDataBinding.ReceiverIdentityVal.text = "-"
        }else{

            mDataBinding.ReceiverIdentityVal.text = getUpdateFri(mItemDetailsToShow.tofri)
        }

        //SenderName
        if(mItemDetailsToShow.fromname.isNullOrEmpty()){
            mDataBinding.SenderNameVal.text = "-"
        }else{

            mDataBinding.SenderNameVal.text = mItemDetailsToShow.fromname
        }

        //SenderNumber
        if(mItemDetailsToShow.fromfri.isNullOrEmpty()){
            mDataBinding.SenderIdentityVal.text = "-"
        }else{

            mDataBinding.SenderIdentityVal.text = getUpdateFri(mItemDetailsToShow.fromfri)
        }

        //Amount
        if(Constants.IS_MERCHANT_USER&&mItemDetailsToShow.fromTax.isNullOrEmpty())
        {
            if(mItemDetailsToShow.toavailablebalance.isNullOrEmpty()){
                amount = "0.00"
                mDataBinding.amountVal.text = "0.00 DH"
            }else{
                amount = mItemDetailsToShow.toavailablebalance
                mDataBinding.amountVal.text = mItemDetailsToShow.toavailablebalance+" "+Constants.CURRENT_CURRENCY_TYPE_TO_SHOW
            }
        }else{
        if(mItemDetailsToShow.toamount.isNullOrEmpty()){
            amount = "0.00"
            mDataBinding.amountVal.text = "0.00 DH"
        }else{
            amount = mItemDetailsToShow.toamount
            mDataBinding.amountVal.text = mItemDetailsToShow.toamount+" "+Constants.CURRENT_CURRENCY_TYPE_TO_SHOW
        }
        }

        //Fee

        var fromTax="0"
        if(Constants.IS_MERCHANT_USER&&mItemDetailsToShow.fromTax.isNullOrEmpty())
        {
            fromTax=mItemDetailsToShow.toTax
        }
        else{
         fromTax=mItemDetailsToShow.fromTax
        }

        if(fromTax.isNullOrEmpty())
        {
            fromTax="0"
        }



        if(Constants.IS_MERCHANT_USER&&mItemDetailsToShow.fromTax.isNullOrEmpty()){
            if(mItemDetailsToShow.toTax.isNullOrEmpty()){
                fee = "0.00"
                mDataBinding.feeVal.text = "0.00 DH"
            }else{
                val feeWithTax = Constants.converValueToTwoDecimalPlace(mItemDetailsToShow.tofee.toDouble()+fromTax.toDouble())
                fee = mItemDetailsToShow.tofee
                mDataBinding.feeVal.text = feeWithTax+" "+Constants.CURRENT_CURRENCY_TYPE_TO_SHOW
            }
        }else{
        if(mItemDetailsToShow.fromfee.isNullOrEmpty()){
            fee = "0.00"
            mDataBinding.feeVal.text = "0.00 DH"
        }else{
            val feeWithTax = Constants.converValueToTwoDecimalPlace(mItemDetailsToShow.fromfee.toDouble()+fromTax.toDouble())
            fee = mItemDetailsToShow.fromfee
            mDataBinding.feeVal.text = feeWithTax+" "+Constants.CURRENT_CURRENCY_TYPE_TO_SHOW
        }
        }

        //TotalAmount
        var totalAmount = Constants.addAmountAndFee(amount.toDouble(),fee.toDouble())
        totalAmount = Constants.converValueToTwoDecimalPlace(totalAmount.toDouble()+fromTax.toDouble())
        mDataBinding.totalAmountVal.text = totalAmount+" "+Constants.CURRENT_CURRENCY_TYPE_TO_SHOW


        if(mItemDetailsToShow.toaccount.contains(Constants.AIR_TIME_RECEIVER_ALIAS)
            || mItemDetailsToShow.toaccount.contains(Constants.POST_PAID_MOBILE_ALIAS)
            || mItemDetailsToShow.toaccount.contains(Constants.POST_PAID_FIXED_ALIAS)
            || mItemDetailsToShow.toaccount.contains(Constants.POST_PAID_INTERNET_ALIAS)){
            mDataBinding.receiverNameGroup.visibility = View.GONE
        }else{
            mDataBinding.receiverNameGroup.visibility = View.VISIBLE
        }
    }

    private  fun subscribeObservers(){
        mActivityViewModel.getReciptTemplateListner.observe(this,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.fileDataHtml.isNullOrEmpty()){
//                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//                        val currentDate = sdf.format(Date())

                        val fileName = "TransactionDetail${mItemDetailsToShow.transactionid}.pdf"
                     val htmlTextPdf =  EncryptionUtils.decryptString(it.fileDataHtml)
                          val savedPDFFile = FileManager.getInstance().createTempFileWithName(applicationContext, fileName, false)
                     //   val destinationFilename = File(
                       //     this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName)

                        // Generate Pdf From Html
                        PDFUtil.generatePDFFromHTML(
                            applicationContext, savedPDFFile,  htmlTextPdf, object :
                                PDFPrint.OnPDFPrintListener {
                                override fun onSuccess(file: File) {
                                    // Open Pdf Viewer
                                    val pdfUri: Uri = Uri.fromFile(savedPDFFile)
                                   mActivityViewModel.isLoading.set(false)

                                    DialogUtils.showFileDialogue(this@TransactionDetailsActivity,
                                        LanguageData.getStringValue("FileSaveSuccessMessage"),
                                        0,
                                        object :DialogUtils.OnYesClickListner{
                                        override fun onDialogYesClickListner() {
                                           //savefile(pdfUri)
                                            viewPdf(pdfUri)
                                        }})
                                }

                                override fun onError(exception: Exception) {
                                    exception.printStackTrace()
                                    mActivityViewModel.isLoading.set(false)
                                    DialogUtils.showErrorDialoge(this@TransactionDetailsActivity,Constants.SHOW_DEFAULT_ERROR)
                                }
                            })

                    }else{
                        mActivityViewModel.isLoading.set(false)
                        DialogUtils.showErrorDialoge(this,it.description)
                    }
                }else{
                    mActivityViewModel.isLoading.set(false)
                    DialogUtils.showErrorDialoge(this,it.description)
                }
            })
    }

    fun isStoragePermissionGranted(activity: Activity):Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            {    //Log.v(TAG, "Permission is granted");
                return true}
         else {
            ActivityCompat.requestPermissions(activity,  arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 1);
            return false;
        }
    } else { //permission is automatically granted on sdk<23 upon installation
        return true;
    }
}


    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        try {

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            var fileName = "TransactionHistory$currentDate.pdf"
            //File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            val futureStudioIconFile = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + fileName
            )
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Logger.debugLog("ok", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                return true
            } catch (e: IOException) {
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            return false
        }
    }



    fun savefile(sourceuri: Uri) {
        var fileName = "TransactionDetail${mItemDetailsToShow.transactionid}.pdf"
        val sourceFilename: String? = sourceuri.path
        val destinationFilename =
            this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(sourceFilename))
            bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
            val buf = ByteArray(1024)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) !== -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bis?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun viewPdf(uri : Uri) {

        // Setting the intent for pdf reader
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(uri, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFri(fri: String): String {
        var userFri = fri.substringAfter("212")
        userFri = userFri.subSequence(0,9).toString()
        userFri = "${Constants.APP_MSISDN_PREFIX}$userFri"
        userFri = userFri.removePrefix("+")
        return userFri
    }

    private fun getUpdateFri(fri: String): String {
        var userFri = fri.substringAfter(":")
        userFri = userFri.substringBefore("@")
        userFri = userFri.substringBefore("/")
        return userFri
    }

    override fun onDownloadReciptClickListner(view: View) {
        if(isStoragePermissionGranted(this)) {
            mActivityViewModel.requestForGetDownloadRecipTemplateApi(
                this,
                Constants.CURRENT_USER_MSISDN,
                mItemDetailsToShow.toname
            )
        }

        }

}
