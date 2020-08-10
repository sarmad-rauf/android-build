package com.es.marocapp.usecase.termsandcondiitons

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityTermsAndConditionsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import kotlinx.android.synthetic.main.fragment_approval.*
import java.io.File

class TermsAndConditions : BaseActivity<ActivityTermsAndConditionsBinding>() {

    private lateinit var mActivityViewModel : TermsAndConditionsVIewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var navGraph: NavGraph

    lateinit var downloadedFileFromURL : File

    var headerText = ""


    override fun setLayout(): Int {
        return R.layout.activity_terms_and_conditions
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(TermsAndConditionsVIewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        PRDownloader.initialize(applicationContext)

        headerText = intent.getStringExtra("title")
        mDataBinding.tvTermConditionTitle.text = headerText

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_termandcondition_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navGraph = navController.navInflater.inflate(R.navigation.terms_and_condition_nav_graph)

        if(headerText == LanguageData.getStringValue("Faqs")){
            mActivityViewModel.isLoading.set(true)
            val fileName = "faq.pdf"
            downloadPdfFromInternet(
                Constants.URL_FOR_FAQ,
                getRootDirPath(this),
                fileName
            )
        }else if(headerText == LanguageData.getStringValue("TermsAndConditions")){
            mActivityViewModel.isLoading.set(true)
            val fileName = "terms.pdf"
            downloadPdfFromInternet(
                Constants.URL_FOR_TERMSANDCONDITIONS,
                getRootDirPath(this),
                fileName
            )
        }else if(headerText == "Contact Us"){
            navGraph.startDestination = R.id.contactUsFragment
        }else{
            navGraph.startDestination = R.id.termsAndConditionFragment
        }

        mDataBinding.imgBackButton.setOnClickListener{
            onBackPressed()
        }

    }

    fun setFragmentToShow(){
        if(headerText == LanguageData.getStringValue("Faqs")){
            navGraph.startDestination = R.id.FAQsFragment
        }else if(headerText == LanguageData.getStringValue("TermsAndConditions")){
            navGraph.startDestination = R.id.termsAndConditionFragment
        }else if(headerText == "Contact Us"){
            navGraph.startDestination = R.id.contactUsFragment
        }else{
            navGraph.startDestination = R.id.termsAndConditionFragment
        }

        navController.setGraph(navGraph)
    }

    private fun downloadPdfFromInternet(url: String, dirPath: String, fileName: String) {
        PRDownloader.download(
            url,
            dirPath,
            fileName
        ).build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    mActivityViewModel.isLoading.set(false)
                    /*Toast.makeText(this@TermsAndConditions, "downloadComplete", Toast.LENGTH_LONG)
                        .show()*/
                    val downloadedFile = File(dirPath, fileName)
                    downloadedFileFromURL = downloadedFile
                    setFragmentToShow()
//                    showPdfFromFile(downloadedFile)
                }

                override fun onError(error: com.downloader.Error?) {
                    mActivityViewModel.isLoading.set(false)
                    Toast.makeText(
                        this@TermsAndConditions,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })
    }

    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

}
