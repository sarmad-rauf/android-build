package com.es.marocapp.usecase.upgradeprofile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.BuildConfig
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityUpgradeProfileBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UpgradeProfileActivity : BaseActivity<ActivityUpgradeProfileBinding>(),
    DialogUtils.OnPickerDialogListener {

    private val STORAGE_PERMISSION_REQUEST_CODE = 114
    private val CAMERA_PERMISSION_REQUEST_CODE = 112
    private val ATTACH_FILE_REQUEST_CODE = 1211
    private val CAMERA_IMAGE_REQUEST_CODE = 1212

    private lateinit var mActivityViewModel: UpgradeProfileViewModel

    private var selectedFileFrontPath: String = ""
    private var selectedFileBackPath: String = ""

    private var currentPhotoFile: File? = null
    private var currentPhotoFilePath: String? = null

    private var isFrontImage: Boolean = false

    override fun setLayout(): Int {
        return R.layout.activity_upgrade_profile
    }

    override fun init(savedInstanceState: Bundle?) {


        mActivityViewModel = ViewModelProvider(this).get(UpgradeProfileViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        mDataBinding.upgradeProfileCardViewFrontSide.setOnClickListener {
            isFrontImage = true
            DialogUtils.showPickerDialog(this, this)
        }

        mDataBinding.upgradeProfileCardViewBackSide.setOnClickListener {
            isFrontImage = false
            DialogUtils.showPickerDialog(this, this)
        }

        mDataBinding.upgradeProfileIvRemoveFileFront.setOnClickListener {
            mDataBinding.upgradeProfileSelectedFileFront.visibility = View.GONE
            mDataBinding.upgradeProfileTvFileTitleFront.text = ""
            selectedFileFrontPath = ""
        }

        mDataBinding.upgradeProfileIvRemoveFileBack.setOnClickListener {
            mDataBinding.upgradeProfileSelectedFileBack.visibility = View.GONE
            mDataBinding.upgradeProfileTvFileTitleBack.text = ""
            selectedFileBackPath = ""
        }

        mDataBinding.imgBackButton.setOnClickListener {
            onBackPressed()
        }

        mDataBinding.upgradeProfileBtnSubmit.setOnClickListener {
            if (selectedFileFrontPath.isEmpty() or selectedFileBackPath.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_document_type), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val frontImageFile = File(selectedFileFrontPath)
            val frontImageBase64 = Tools.fileToBase64String(frontImageFile)

            val backImageFile = File(selectedFileBackPath)
            val backImageBase64 = Tools.fileToBase64String(backImageFile)

            mActivityViewModel.requestForUpgradeUserProfileFileUploadImage(
                this,
                ApiConstant.CONTEXT_AFTER_LOGIN,
                Constants.getNumberMsisdn(
                    Constants.CURRENT_USER_MSISDN
                ),
                getUserProfile(),
                Constants.reasonUpgradeToLevelTwo,
                frontImageBase64!!,
                backImageBase64!!
            )
        }
        setStrings()
        subscribeObserver()
    }

    private fun getUserProfile(): String {
        var currentProfile =
            Constants.loginWithCertResponse.getAccountHolderInformationResponse.profileName
        if (currentProfile.equals("") || currentProfile.equals(null)) {
            currentProfile = Constants.UserProfileName
        }

        if (currentProfile.contains("1")) {
            return currentProfile.replace("Profile", "").trim() + " to Level 2 Profile KYC"
        }

        return ""
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg, image/jpg"
        startActivityForResult(intent, ATTACH_FILE_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentPhotoFile = createImageFile()
        if (cameraIntent.resolveActivity(packageManager) != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (currentPhotoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        currentPhotoFile!!
                    )
                    cameraIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        photoURI
                    )
                    startActivityForResult(
                        cameraIntent,
                        CAMERA_IMAGE_REQUEST_CODE
                    )
                }
            } else {
                if (currentPhotoFile != null) {
                    val uri = Uri.fromFile(currentPhotoFile)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST_CODE)
                }
            }
        }
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this, Observer {
            DialogUtils.showErrorDialoge(this, it)
        })
        mActivityViewModel.upgradeProfileFileUploadResponseListener.observe(this,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    DialogUtils.successFailureDialogue(
                        this,
                        it.description,
                        0,
                        object : DialogUtils.OnYesClickListner {
                            override fun onDialogYesClickListner() {
                                finish()
                            }
                        })
                } else {
                    DialogUtils.showErrorDialoge(this, it.description)
                }
            }
        )
    }

    private fun setStrings() {
        mDataBinding.tvUpgradeProfileTitle.text=LanguageData.getStringValue("UpgradeProfile")
        mDataBinding.upgradeProfileDescription.text=LanguageData.getStringValue("UpgradeProfileDescription")
        val attachFrontImageTitle=LanguageData.getStringValue("ClickToAttach")+"\n"+LanguageData.getStringValue("FrontSide")
        val attachBackImageTitle=LanguageData.getStringValue("ClickToAttach")+"\n"+LanguageData.getStringValue("BackSide")
        mDataBinding.frontImagetitle.text=attachFrontImageTitle
        mDataBinding.backImageTitle.text=attachBackImageTitle
        mDataBinding.upgradeProfileBtnSubmit.text=LanguageData.getStringValue("BtnTitle_Submit")
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.CAMERA
            ),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoFilePath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Logger.debugLog("Gallery Permission", "Permission to access storage denied.")
                } else {
                    openGallery()
                }
            }
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Logger.debugLog("Camera Permission", "Permission to access camera denied.")
                } else {
                    openCamera()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ATTACH_FILE_REQUEST_CODE) {
            if (data != null) {
                val uri = data.data as Uri
                if (isFrontImage) {
                    selectedFileFrontPath = FileUtils.getPath(this, uri)
                    showFrontFile(uri)
                } else {
                    selectedFileBackPath = FileUtils.getPath(this, uri)
                    showBackFile(uri)
                }
            }
        } else if (requestCode == CAMERA_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val uri = Uri.fromFile(currentPhotoFile)
                if (isFrontImage) {
                    selectedFileFrontPath = FileUtils.getPath(this, uri)
                    showFrontFile(uri)
                } else {
                    selectedFileBackPath = FileUtils.getPath(this, uri)
                    showBackFile(uri)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showFrontFile(uri: Uri) {
        mDataBinding.upgradeProfileSelectedFileFront.visibility = View.VISIBLE
        mDataBinding.upgradeProfileTvFileTitleFront.text = FileUtils.getFileName(this, uri)
    }

    private fun showBackFile(uri: Uri) {
        mDataBinding.upgradeProfileSelectedFileBack.visibility = View.VISIBLE
        mDataBinding.upgradeProfileTvFileTitleBack.text = FileUtils.getFileName(this, uri)
    }

    override fun onCameraClickListener() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            openCamera()
        }
    }

    override fun onGalleryClickListener() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            openGallery()
        }
    }
}