package com.es.marocapp.usecase.login.signup

import android.app.Activity.RESULT_OK
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
import com.es.marocapp.databinding.FragmentUpgradeProfileBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.*
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UpgradeProfileFragment : BaseFragment<FragmentUpgradeProfileBinding>(),
    DialogUtils.OnPickerDialogListener {

    private val STORAGE_PERMISSION_REQUEST_CODE = 114
    private val CAMERA_PERMISSION_REQUEST_CODE = 112
    private val ATTACH_FILE_REQUEST_CODE = 1211
    private val CAMERA_IMAGE_REQUEST_CODE = 1212

    private lateinit var mActivityViewModel: LoginActivityViewModel

    private var selectedFileFrontPath: String = ""
    private var selectedFileBackPath: String = ""
    private var currentPhotoFile: File? = null
    private var currentPhotoFilePath: String? = null

    private var isFrontImage: Boolean = false

    override fun setLayout(): Int {
        return R.layout.fragment_upgrade_profile
    }

    override fun init(savedInstanceState: Bundle?) {


        mActivityViewModel =
            ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        mDataBinding.upgradeProfileCardViewFrontSide.setOnClickListener {
            isFrontImage = true
            DialogUtils.showPickerDialog(requireActivity(), this)
        }

        mDataBinding.upgradeProfileCardViewBackSide.setOnClickListener {
            isFrontImage = false
            DialogUtils.showPickerDialog(requireActivity(), this)
        }

        mDataBinding.upgradeProfileIvRemoveFileFront.setOnClickListener {
            mDataBinding.upgradeProfileSelectedFileFront.visibility = View.GONE
            mDataBinding.upgradeProfileTvFileTitleFront.text = ""
            mActivityViewModel.selectedFileFrontPath = ""
            selectedFileFrontPath = ""
        }

        mDataBinding.upgradeProfileIvRemoveFileBack.setOnClickListener {
            mDataBinding.upgradeProfileSelectedFileBack.visibility = View.GONE
            mDataBinding.upgradeProfileTvFileTitleBack.text = ""
            mActivityViewModel.selectedFileBackPath = ""
            selectedFileBackPath = ""
        }

        mDataBinding.imgBackButton.setOnClickListener {
            //  (activity as LoginActivity) .navController.navigateUp()
            (activity as LoginActivity).navController.popBackStack(
                R.id.signUpDetailFragment,
                false
            )
        }

        mDataBinding.upgradeProfileBtnSubmit.setOnClickListener {
            if (selectedFileFrontPath.isEmpty() or selectedFileBackPath.isEmpty()) {
//               Toast.makeText(this, getString(R.string.select_document_type), Toast.LENGTH_SHORT)
//                    .show()
                return@setOnClickListener
            } else {
                mActivityViewModel.selectedFileFrontPath = selectedFileFrontPath
                mActivityViewModel.selectedFileBackPath = selectedFileBackPath
                (activity as LoginActivity).navController.popBackStack(
                    R.id.signUpDetailFragment,
                    false
                )
            }

        }
        setStrings()
        subscribeObserver()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/jpeg, image/jpg"
        startActivityForResult(intent, ATTACH_FILE_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentPhotoFile = createImageFile()
        if (activity?.packageManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (currentPhotoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        requireActivity(),
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
            DialogUtils.showErrorDialoge(requireActivity(), it)
        })
        mActivityViewModel.getRegisterUserResponseListner.observe(
            this@UpgradeProfileFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    (activity as LoginActivity).navController.navigate(R.id.action_upgradeProfileFragment_to_setYourPinFragment)

                } else {
                    DialogUtils.showErrorDialoge(activity as LoginActivity, it.description)
                }
            })
    }

    private fun setStrings() {
        mDataBinding.tvUpgradeProfileTitle.text = LanguageData.getStringValue("CreateYourAccount")
        mDataBinding.upgradeProfileDescription.text =
            LanguageData.getStringValue("UpgradeProfileDescription")
        val attachFrontImageTitle =
            LanguageData.getStringValue("ClickToAttach") + "\n" + LanguageData.getStringValue("FrontSide")
        val attachBackImageTitle =
            LanguageData.getStringValue("ClickToAttach") + "\n" + LanguageData.getStringValue("BackSide")
        mDataBinding.frontImagetitle.text = attachFrontImageTitle
        mDataBinding.backImageTitle.text = attachBackImageTitle
        mDataBinding.upgradeProfileBtnSubmit.text = LanguageData.getStringValue("Upload")
        if (!mActivityViewModel.selectedFileFrontPath.isEmpty()) {
            showFrontFile(Uri.fromFile(File(mActivityViewModel.selectedFileFrontPath)))
        }

        if (!mActivityViewModel.selectedFileBackPath.isEmpty()) {
//               Toast.makeText(this, getString(R.string.select_document_type), Toast.LENGTH_SHORT)
//                    .show()

            showBackFile(Uri.fromFile(File(mActivityViewModel.selectedFileBackPath)))
        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.CAMERA
            ),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
                if( Constants.isFileSizeVerified(uri,requireContext(),false)){
                if (isFrontImage) {
                    selectedFileFrontPath = FileUtils.getPath(requireActivity(), uri)
                    showFrontFile(uri)
                } else {
                    selectedFileBackPath = FileUtils.getPath(requireActivity(), uri)
                    showBackFile(uri)
                }
                }else{
                    var description = LanguageData.getStringValue("FileSizeLimitErrorMessage")
                    description= description?.replace("<file-size>",Constants.maxFileSizeUploadLimitInMBs.toString())
                    DialogUtils.showErrorDialoge(activity,description)
                }
            }
        } else if (requestCode == CAMERA_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val uri = Uri.fromFile(currentPhotoFile)
                if(Constants.isFileSizeVerified(uri, requireContext(),true)){
                if (isFrontImage) {
                    selectedFileFrontPath = FileUtils.getPath(requireActivity(), uri)
                    showFrontFile(uri)
                } else {
                    selectedFileBackPath = FileUtils.getPath(requireActivity(), uri)
                    showBackFile(uri)
                }
                }else{
                    var description = LanguageData.getStringValue("FileSizeLimitErrorMessage")
                    description= description?.replace("<file-size>",Constants.maxFileSizeUploadLimitInMBs.toString())
                    DialogUtils.showErrorDialoge(activity,description)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showFrontFile(uri: Uri) {
        mDataBinding.upgradeProfileSelectedFileFront.visibility = View.VISIBLE
        mDataBinding.upgradeProfileTvFileTitleFront.text =
            activity?.let { FileUtils.getFileName(it, uri) }
        Picasso.get().load(uri).into(mDataBinding.frontThumbnail)


    }

    private fun showBackFile(uri: Uri) {
        mDataBinding.upgradeProfileSelectedFileBack.visibility = View.VISIBLE
        mDataBinding.upgradeProfileTvFileTitleBack.text =
            activity?.let { FileUtils.getFileName(it, uri) }
        Picasso.get().load(uri).into(mDataBinding.backThumbnail)
    }

    override fun onCameraClickListener() {
        val permission = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.CAMERA
            )
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            openCamera()
        }
    }

    override fun onGalleryClickListener() {
        val permission = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            openGallery()
        }
    }
}