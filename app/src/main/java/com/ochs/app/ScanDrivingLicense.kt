package com.ochs.app;

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
//import android.drm.ProcessedData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.acuant.acuantcamera.camera.AcuantCameraActivity
import com.acuant.acuantcamera.camera.AcuantCameraOptions
import com.acuant.acuantcamera.constant.ACUANT_EXTRA_CAMERA_OPTIONS
import com.acuant.acuantcamera.constant.ACUANT_EXTRA_IMAGE_URL
import com.acuant.acuantcamera.constant.ACUANT_EXTRA_PDF417_BARCODE
import com.acuant.acuantcamera.initializer.MrzCameraInitializer
import com.acuant.acuantcommon.exception.AcuantException
import com.acuant.acuantcommon.helper.CredentialHelper
import com.acuant.acuantcommon.initializer.AcuantInitializer

import com.acuant.acuantcommon.initializer.IAcuantPackageCallback
import com.acuant.acuantcommon.model.AuthenticationResult
import com.acuant.acuantcommon.model.Credential
import com.acuant.acuantcommon.model.ErrorCodes
import com.acuant.acuantcommon.model.ErrorDescriptions
import com.acuant.acuantcommon.type.CardSide
import com.acuant.acuantdocumentprocessing.AcuantDocumentProcessor
import com.acuant.acuantdocumentprocessing.model.*
import com.acuant.acuantdocumentprocessing.service.listener.CreateInstanceListener
import com.acuant.acuantdocumentprocessing.service.listener.GetDataListener
import com.acuant.acuantdocumentprocessing.service.listener.UploadImageListener
import com.acuant.acuantimagepreparation.AcuantImagePreparation
import com.acuant.acuantimagepreparation.background.EvaluateImageListener
import com.acuant.acuantimagepreparation.initializer.ImageProcessorInitializer
import com.acuant.acuantimagepreparation.model.AcuantImage
import com.acuant.acuantimagepreparation.model.CroppingData
import com.ochs.app.backgroundtasks.AcuantTokenService
import com.ochs.app.backgroundtasks.AcuantTokenServiceListener
import com.acuant.acuantcommon.model.Error
import com.ochs.app.flexiicar_app.booking.Booking_Activity
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.HashMap
import kotlin.concurrent.thread
import com.ochs.app.flexiicar_app.login.Driver_Profile
import com.ochs.app.flexiicar_app.user.User_Profile

class ScanDrivingLicense : AppCompatActivity() {

    private var progressDialog: LinearLayout? = null
    private var progressText: TextView? = null
    private var capturedFrontImage: AcuantImage? = null
    private var capturedBackImage: AcuantImage? = null
    private var capturedSelfieImage: Bitmap? = null
    private var capturedFaceImage: Bitmap? = null
    private var capturedBarcodeString: String? = null
    private var frontCaptured: Boolean = false
    private var isHealthCard: Boolean = false
    private var isRetrying: Boolean = false
    private var insuranceButton: Button? = null
    private var idButton: Button? = null
    private var capturingImageData: Boolean = true
    private var capturingSelfieImage: Boolean = false
    private var capturingFacialMatch: Boolean = false
    private var facialResultString: String? = null
    private var facialLivelinessResultString: String? = null
    private var captureWaitTime: Int = 0
    private var documentInstanceID: String? = null
    private var autoCaptureEnabled: Boolean = true
    private var numberOfClassificationAttempts: Int = 0
    private var isInitialized = false
    private var livenessSelected = 0
    private var isKeyless = false
    private var processingFacialLiveness = false
    private val useTokenInit = true
    private var recentImage: AcuantImage? = null
    private lateinit var livenessSpinner : Spinner
    private lateinit var livenessArrayAdapter: ArrayAdapter<String>

    fun cleanUpTransaction() {
        capturedFrontImage?.destroy()
        capturedBackImage?.destroy()
        facialResultString = null
        capturedFrontImage = null
        capturedBackImage = null
        capturedSelfieImage = null
        capturedFaceImage = null
        facialLivelinessResultString = null
        capturedBarcodeString = null
        isHealthCard = false
        processingFacialLiveness = false
        isRetrying = false
        capturingImageData = true
        documentInstanceID = null
        numberOfClassificationAttempts = 0
    }

    public var afterScanBackTo: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_driving_license)
        System.out.println("Initializing")

        afterScanBackTo = intent.getIntExtra("afterScanBackTo",0)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 99)
        }

        //setProgress(true, "Initializing...")

        initializeAcuantSdk(object: IAcuantPackageCallback{
            override fun onInitializeFailed(error: List<Error>) {
                TODO("Not yet implemented")
                System.out.println("Initialize Failed")
            }

            override fun onInitializeSuccess() {
                this@ScanDrivingLicense.runOnUiThread {
                    //setProgress(false)
                    System.out.println("Initialize Success")
                    frontCaptured = false
                    cleanUpTransaction()
                    captureWaitTime = 0
                    showDocumentCaptureCamera()
                }
            }

            //override fun onInitializeFailed(error: List<Error>) {
             //   this@ScanDrivingLicense.runOnUiThread {
            //        showAcuDialog("Could not initialize.\n"+error[0].errorDescription)
             //   }
            //}
        })


    }

    private fun initializeAcuantSdk(callback:IAcuantPackageCallback)
    {
        try{

            val initCallback = object: IAcuantPackageCallback{
                override fun onInitializeFailed(error: List<Error>)
                {
                    TODO("Not yet implemented")
                    System.out.println("Initializing 1")

                    callback.onInitializeFailed(error)
                }

                override fun onInitializeSuccess() {
                    System.out.println("Initializing 2")

                    isInitialized = true
                    callback.onInitializeSuccess()

                    if(Credential.get().subscription == null || Credential.get().subscription.isEmpty() ){

                        System.out.println("Initializing 3")

                        isKeyless = true
                        //livenessSpinner.visibility = View.INVISIBLE
                        callback.onInitializeSuccess()
                    }
                    else{
                        if(Credential.get().secureAuthorizations.ozoneAuth) {
                            //findViewById<Button>(R.id.main_mrz_camera).visibility = View.VISIBLE
                        }
                        //getFacialLivenessCredentials(callback)
                    }
                }

                //override fun onInitializeFailed(error: List<Error>) {
                 //   callback.onInitializeFailed(error)
                //}
            }
            /*
            * The initFromXml and AcuantTokenService is just for the sample app, you should be
            * generating these tokens on one of your secure servers, passing it to the app,
            * and then calling initializeWithToken passing the config and token.
            */
            @Suppress("ConstantConditionIf")
            if (useTokenInit) {
                Toast.makeText(this@ScanDrivingLicense, "Using Token Init", Toast.LENGTH_SHORT).show()

                System.out.println("Initializing 4")

                Credential.initFromXml("acuant.config.xml", this)
                AcuantTokenService(Credential.get(), object : AcuantTokenServiceListener {
                    override fun onSuccess(token: String) {

                        System.out.println("Initializing 5")

                        if (!isInitialized) {

                            System.out.println("Initializing 6")

                            AcuantInitializer.initializeWithToken("acuant.config.xml",
                                    token,
                                    this@ScanDrivingLicense,
                                    listOf(ImageProcessorInitializer(), MrzCameraInitializer()),
                                    initCallback)

                            System.out.println("Initializing 7")

                        } else {

                            System.out.println("Initializing 8")

                            if(Credential.setToken(token)) {

                                System.out.println("Initializing 9")

                                initCallback.onInitializeSuccess()

                            } else {

                                System.out.println("Initializing 10")

                                //initCallback.onInitializeFailed(listOf(Error(-2, "Error in setToken\nBad/expired token")))
                            }
                        }

                    }

                    override fun onFail(responseCode: Int) {
                        //initCallback.onInitializeFailed(listOf(Error(responseCode, "Error in getToken service.\nCode: $responseCode")))
                    }

                }).execute()
            } else {
                Toast.makeText(this@ScanDrivingLicense, "Using Credential Init", Toast.LENGTH_SHORT).show()
                AcuantInitializer.initialize("acuant.config.xml",
                        this@ScanDrivingLicense,
                        listOf(ImageProcessorInitializer(), MrzCameraInitializer()),
                        initCallback)
            }

        } catch(e: AcuantException) {
            Log.e("Acuant Error", e.toString())
            showAcuDialog(e.toString())
        }
    }

    // ID/Passport Clicked
    fun idPassPortClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        if (!hasInternetConnection()) {
            showAcuDialog("No internet connection available.")
        } else {
            if (isInitialized && (!useTokenInit || Credential.get()?.token?.isValid == true)) {
                frontCaptured = false
                cleanUpTransaction()
                captureWaitTime = 0
                showDocumentCaptureCamera()
            } else {
                //setProgress(true, "Initializing...")
                initializeAcuantSdk(object: IAcuantPackageCallback{
                    override fun onInitializeSuccess() {
                        this@ScanDrivingLicense.runOnUiThread {
                            //setProgress(false)
                            frontCaptured = false
                            cleanUpTransaction()
                            captureWaitTime = 0
                            showDocumentCaptureCamera()
                        }
                    }

                    override fun onInitializeFailed(error: List<Error>) {
                        this@ScanDrivingLicense.runOnUiThread {
                            showAcuDialog("Could not initialize")
                        }
                    }
                })
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager= this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }

    val initCallback = object: IAcuantPackageCallback{
        override fun onInitializeFailed(error: List<Error>) {
            TODO("Not yet implemented")
        }

        override fun onInitializeSuccess() {

            isInitialized = true
            if(Credential.get().subscription == null || Credential.get().subscription.isEmpty() ){
                isKeyless = true
                //livenessSpinner.visibility = View.INVISIBLE
                //callback.onInitializeSuccess()
            }
            /*else{
                if(Credential.get().secureAuthorizations.ozoneAuth) {
                    findViewById<Button>(R.id.main_mrz_camera).visibility = View.VISIBLE
                }
                getFacialLivenessCredentials(callback)
            }*/
        }

        //override fun onInitializeFailed(error: List<Error>) {
         //   callback.onInitializeFailed(error)
        //}
    }


    private fun showAcuDialog(message: Int, @Suppress("SameParameterValue") title: String = "Error",
                              yesOnClick: DialogInterface.OnClickListener? = null,
                              noOnClick: DialogInterface.OnClickListener? = null) {
        showAcuDialog(getString(message), title, yesOnClick, noOnClick)
    }

    private fun showAcuDialog(message: String, title: String = "Error",
                              yesOnClick: DialogInterface.OnClickListener? = null,
                              noOnClick: DialogInterface.OnClickListener? = null) {

        //setProgress(false)
        val alert = AlertDialog.Builder(this@ScanDrivingLicense)
        alert.setTitle(title)
        alert.setMessage(message)
        if (yesOnClick != null) {
            alert.setPositiveButton("YES", yesOnClick)
            if (noOnClick != null) {
                alert.setNegativeButton("NO", noOnClick)
            }
        } else {
            alert.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alert.show()
    }

    private fun handleKeyless(acuantImage: AcuantImage?) {
        handleKeyless(acuantImage?.isPassport)
    }

    private fun handleKeyless(isPassport: Boolean?){
        this@ScanDrivingLicense.runOnUiThread {
            showAcuDialog(R.string.scan_back_side_id, "Message",
                    DialogInterface.OnClickListener { dialog, _ ->
                        frontCaptured = true
                        dialog?.dismiss()
                        captureWaitTime = 2
                        showDocumentCaptureCamera()
                    }, DialogInterface.OnClickListener { dialog, _ -> dialog?.dismiss() })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == Constants.REQUEST_CAMERA_PHOTO && resultCode == AcuantCameraActivity.RESULT_SUCCESS_CODE) {

                System.out.println("Image Captured 1")

                val url = data?.getStringExtra(ACUANT_EXTRA_IMAGE_URL)
                capturedBarcodeString = data?.getStringExtra(ACUANT_EXTRA_PDF417_BARCODE)
                if (url != null) {

                    System.out.println("Image Captured 2")

                    //setProgress(true, "Cropping...")
                    val bytes = readFromFile(url)
                    AcuantImagePreparation.evaluateImage(this, CroppingData(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)), object : EvaluateImageListener {

                        override fun onSuccess(image: AcuantImage) {

                            System.out.println("Image Captured 3")

                            //setProgress(false)
                            if (isKeyless) {

                                System.out.println("Image Captured 4")

                                handleKeyless(image)
                            } else {

                                System.out.println("Image Captured 5")

                                recentImage = image

                                showConfirmation(!frontCaptured, false)
                            }
                        }

                        override fun onError(error: com.acuant.acuantcommon.model.Error) {

                            System.out.println("Image Captured 6")

                            showAcuDialog(error.errorDescription)
                        }
                    })
                } else {
                    System.out.println("Image Captured 7")

                    showAcuDialog("Camera failed to return valid image path")
                }
            }
            else if (requestCode == Constants.REQUEST_CONFIRMATION && resultCode == Constants.REQUEST_CONFIRMATION) {

                System.out.println("Confirmation 2")

                val isFront = data!!.getBooleanExtra("isFrontImage", true)
                val isConfirmed = data.getBooleanExtra("Confirmed", true)
                if (isConfirmed) {

                    System.out.println("Confirmation 3")
                    if (isFront) {
                        System.out.println("Confirmation 4")
                        capturedFrontImage = recentImage
                        processFrontOfDocument()
                    }
                    else {
                        capturedBackImage = recentImage
                        if (!isHealthCard) {
                            val alert = AlertDialog.Builder(this@ScanDrivingLicense)
                            alert.setTitle("Message")
                            if (capturedBarcodeString != null && capturedBarcodeString!!.trim().isNotEmpty()) {
                                if (livenessSelected != 0) {
                                    alert.setMessage("Following barcode is captured.\n\n"
                                            + "Barcode String :\n\n"
                                            + capturedBarcodeString!!.subSequence(0, (capturedBarcodeString!!.length * 0.25).toInt())
                                            + "...\n\n"
                                            + "Capture Selfie Image now.")
                                } else {
                                    alert.setMessage("Following barcode is captured.\n\n"
                                            + "Barcode String :\n\n"
                                            + capturedBarcodeString!!.subSequence(0, (capturedBarcodeString!!.length * 0.25).toInt()))
                                }
                            } else {
                                if (livenessSelected != 0) {
                                    alert.setMessage("Capture Selfie Image now.")
                                } else {
                                    alert.setMessage("Continue.")

                                }
                            }
                            alert.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                //setProgress(true, "Getting Data...")
                                uploadBackImageOfDocument()
                                //showFrontCamera()
                            }
                            /*if (livenessSelected != 0) {
                                alert.setNegativeButton("CANCEL") { dialog, _ ->
                                    setProgress(true, "Getting Data...")
                                    facialLivelinessResultString = "Facial Liveliness Failed"
                                    capturingSelfieImage = false
                                    uploadBackImageOfDocument()
                                    dialog.dismiss()
                                }
                            }*/
                            alert.show()
                        } else {
                            //uploadHealthCard()
                        }

                    }
                } else {
                    System.out.println("Confirmation 5")
                    showDocumentCaptureCamera()
                }
            } else if (requestCode == Constants.REQUEST_RETRY && resultCode == Constants.REQUEST_RETRY) {
                isRetrying = true
                showDocumentCaptureCamera()
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }


    private fun readFromFile(fileUri: String?): ByteArray{
        val file = File(fileUri)
        val bytes = ByteArray(file.length().toInt())
        try {
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
        file.delete()
        return bytes
    }

    //Show Confirmation UI
    fun showConfirmation(isFrontImage: Boolean, isBarcode: Boolean) {
        val confirmationIntent = Intent(
                this@ScanDrivingLicense,
                ConfirmationActivity::class.java
        )
        confirmationIntent.putExtra("isFrontImage", isFrontImage)
        confirmationIntent.putExtra("isBarcode", isBarcode)
        if (recentImage != null) {
            image = recentImage!!.image
            confirmationIntent.putExtra("sharpness", recentImage!!.sharpness)
            confirmationIntent.putExtra("glare", recentImage!!.glare)
            confirmationIntent.putExtra("dpi", recentImage!!.dpi)
            confirmationIntent.putExtra("barcode", capturedBarcodeString)
        }
        System.out.println("Confirmation 1")

        startActivityForResult(confirmationIntent, Constants.REQUEST_CONFIRMATION)
    }

    //Show Classification Error
    fun showClassificationError() {
        val classificationErrorIntent = Intent(
                this@ScanDrivingLicense,
                ClassificationFailureActivity::class.java
        )
        if (recentImage != null) {
            image = recentImage!!.image
        }
        startActivityForResult(classificationErrorIntent, Constants.REQUEST_RETRY)
    }

    // Process Front image
    private fun processFrontOfDocument() {
        this@ScanDrivingLicense.runOnUiThread {
            //setProgress(true, "Uploading  & Classifying...")
        }

        System.out.println("Process Doc 1")

        val idOptions = IdOptions()
        idOptions.cardSide = CardSide.Front
        idOptions.isHealthCard = false
        idOptions.isRetrying = isRetrying

        val frontData = if (capturedFrontImage?.rawBytes != null) {
            System.out.println("Process Doc 2")
            EvaluatedImageData(capturedFrontImage!!.rawBytes)
        } else {
            System.out.println("Process Doc 3")
            showAcuDialog("Image bytes were null.")
            return
        }

        if (isRetrying) {
            System.out.println("Process Doc 4"+documentInstanceID)
            uploadFrontImageOfDocument(documentInstanceID!!, frontData, idOptions)

        } else {
            System.out.println("Process Doc 5")
            AcuantDocumentProcessor.createInstance(idOptions, object: CreateInstanceListener {
                override fun instanceCreated(instanceId: String?, error: com.acuant.acuantcommon.model.Error?) {

                    System.out.println("Process Doc 6")
                    if (error == null) {

                        System.out.println("Process Doc 7"+instanceId)
                        // Success : Instance Created
                        documentInstanceID = instanceId
                        uploadFrontImageOfDocument(instanceId!!, frontData, idOptions)

                        System.out.println("Process Doc 8")

                    } else {

                        System.out.println("Process Doc 9")
                        // Failure
                        this@ScanDrivingLicense.runOnUiThread {
                            showAcuDialog(error.errorDescription)
                        }
                    }
                }
            })
        }
    }

    // Upload front Image of Driving License
    fun uploadFrontImageOfDocument(instanceId: String, frontData: EvaluatedImageData, idOptions: IdOptions) {

        System.out.println("Upload Doc 1")

        numberOfClassificationAttempts += 1
        // Upload front Image of DL
        Log.d("InstanceId:", instanceId)
        System.out.println("Upload Doc 2"+instanceId)

        AcuantDocumentProcessor.uploadImage(instanceId, frontData, idOptions, object : UploadImageListener {
            override fun imageUploaded(error: com.acuant.acuantcommon.model.Error?, classification: Classification?) {

                System.out.println("Upload Doc 3")
                if (error == null) {

                    System.out.println("Upload Doc 4")
                    // Successfully uploaded
                    //setProgress(false)
                    frontCaptured = true
                    if (isBackSideRequired(classification)) {
                        this@ScanDrivingLicense.runOnUiThread {
                            showAcuDialog(R.string.scan_back_side_id, "Message", DialogInterface.OnClickListener { dialog, _ ->
                                dialog.dismiss()
                                captureWaitTime = 2
                                showDocumentCaptureCamera()
                            }, DialogInterface.OnClickListener { dialog, _ ->
                                dialog.dismiss()
                            })
                        }
                    } else {
                        val alert = AlertDialog.Builder(this@ScanDrivingLicense)
                        alert.setTitle("Message")
                        if (livenessSelected != 0) {
                            alert.setMessage("Capture Selfie Image")
                        } else {
                            alert.setMessage("Continue")
                        }
                        if (livenessSelected != 0) {
                            alert.setNegativeButton("CANCEL") { dialog, _ ->
                                facialLivelinessResultString = "Facial Liveliness Failed"
                                //setProgress(true, "Getting Data...")
                                getData()
                                dialog.dismiss()
                            }
                        }
                        alert.show()
                    }

                } else {
                    // Failure
                    this@ScanDrivingLicense.runOnUiThread {
                        //setProgress(false)
                        if (error.errorCode == ErrorCodes.ERROR_CouldNotClassifyDocument) {
                            showClassificationError()
                        } else {
                            showAcuDialog(error.errorDescription)
                        }
                    }
                }
            }
        })
    }


    //Upload Back side of Driving License
    private fun uploadBackImageOfDocument() {

        val idOptions = IdOptions()
        idOptions.cardSide = CardSide.Back
        idOptions.isHealthCard = false
        idOptions.isRetrying = false

        val backData = if (capturedBackImage?.rawBytes != null) {
            EvaluatedImageData(capturedBackImage!!.rawBytes, capturedBarcodeString)
        } else {
            showAcuDialog("Image bytes were null.")
            return
        }

        AcuantDocumentProcessor.uploadImage(documentInstanceID, backData, idOptions, object : UploadImageListener {
            override fun imageUploaded(error: com.acuant.acuantcommon.model.Error?, classification: Classification?) {
                if (error == null) {
                    getData()
                }
            }
        })
    }

    // Get data
    fun getData() {
        AcuantDocumentProcessor.getData(documentInstanceID,false, object : GetDataListener {
            override fun processingResultReceived(result: ProcessingResult?) {

                var scanData: ArrayList<String> = ArrayList()

                if (result == null || result.error != null) {
                    this@ScanDrivingLicense.runOnUiThread {
                        showAcuDialog(result?.error?.errorDescription ?: ErrorDescriptions.ERROR_DESC_CouldNotGetConnectData)
                    }
                    return
                } else if ((result as IDResult).fields == null || result.fields.dataFieldReferences == null) {
                    this@ScanDrivingLicense.runOnUiThread {
                        showAcuDialog("Unknown error happened.\nCould not extract data")
                    }
                    return
                }
                var docNumber = ""
                var cardType = "ID1"
                var frontImageUri: String? = null
                var backImageUri: String? = null
                var signImageUri: String? = null
                var faceImageUri: String? = null
                var resultString: String? = ""
                val fieldReferences = result.fields.dataFieldReferences
                for (reference in fieldReferences) {
                    if (reference.key == "Document Class Name" && reference.type == "string") {
                        if (reference.value == "Driver License") {
                            cardType = "ID1"
                        }
                    } else if (reference.key == "Document Number" && reference.type == "string") {
                        docNumber = reference.value
                    } else if (reference.key == "Photo" && reference.type == "uri") {
                        faceImageUri = reference.value
                    } else if (reference.key == "Signature" && reference.type == "uri") {
                        signImageUri = reference.value
                    }
                }

                for (image in result.images.images) {
                    if (image.side == 0) {
                        frontImageUri = image.uri
                    } else if (image.side == 1) {
                        backImageUri = image.uri
                    }
                }

                for (reference in fieldReferences) {
                    if (reference.type == "string") {
                        resultString = resultString + reference.key + ":" + reference.value + System.lineSeparator()

                        scanData.add(reference.key+":"+reference.value)
                    }
                }

                scanData.add("InstanceId:"+documentInstanceID)

                resultString = "Authentication Result : " +
                        AuthenticationResult.getString(Integer.parseInt(result.result)) +
                        System.lineSeparator() +
                        System.lineSeparator() +
                        resultString

                System.out.println("Result : "+resultString)

                if(afterScanBackTo == 1)
                {
                    val driverProfile = Intent(
                            this@ScanDrivingLicense,
                            Driver_Profile::class.java
                    )
                    driverProfile.putStringArrayListExtra("scanData", scanData)
                    startActivity(driverProfile)
                }
                else if(afterScanBackTo == 2)
                {
                    val driverProfile = Intent(
                            this@ScanDrivingLicense,
                            User_Profile::class.java
                    )
                    driverProfile.putStringArrayListExtra("scanData", scanData)
                    startActivity(driverProfile)
                }
                else if(afterScanBackTo == 3)
                {
                    val driverProfile = Intent(
                            this@ScanDrivingLicense,
                            Booking_Activity::class.java
                    )
                    driverProfile.putStringArrayListExtra("scanData", scanData)
                    startActivity(driverProfile)
                }
                thread {
                    val frontImage = loadAssureIDImage(frontImageUri, Credential.get())
                    val backImage = loadAssureIDImage(backImageUri, Credential.get())
                    val faceImage = loadAssureIDImage(faceImageUri, Credential.get())
                    val signImage = loadAssureIDImage(signImageUri, Credential.get())
                    capturedFaceImage = faceImage
                    ScanDrivingLicense@ capturingImageData = false
                    while (capturingSelfieImage) {
                        Thread.sleep(100)
                    }
                    this@ScanDrivingLicense.runOnUiThread {
                        showResults(result.biographic.birthDate, result.biographic.expirationDate, docNumber, frontImage, backImage, faceImage, signImage, resultString, cardType)
                    }
                }
            }
        })
    }


    // Show ID Result
    fun showResults(dateOfBirth: String?, dateOfExpiry: String?, documentNumber: String?, frontImage: Bitmap?, backImage: Bitmap?, faceImage: Bitmap?, signImage: Bitmap?, resultString: String?, cardType: String) {

        ProcessedData.cleanup()
        ProcessedData.frontImage = frontImage
        ProcessedData.backImage = backImage
        ProcessedData.faceImage = faceImage
        ProcessedData.capturedFaceImage = capturedSelfieImage
        ProcessedData.signImage = signImage
        ProcessedData.dateOfBirth = dateOfBirth
        ProcessedData.dateOfExpiry = dateOfExpiry
        ProcessedData.documentNumber = documentNumber
        ProcessedData.cardType = cardType
        if (!isHealthCard) {
            thread {
                while (capturingFacialMatch || processingFacialLiveness) {
                    Thread.sleep(100)
                }
                this@ScanDrivingLicense.runOnUiThread {
                    facialResultString = if (facialResultString == null) "Facial Match Failed" else facialResultString
                    ProcessedData.formattedString = (facialLivelinessResultString ?: "No Liveness Test Performed") + System.lineSeparator() + facialResultString+ System.lineSeparator() + resultString

                    /*val resultIntent = Intent(
                            this@ScanDrivingLicense,
                            ResultActivity::class.java
                    )
                    //setProgress(false)
                    startActivity(resultIntent)*/
                }
            }
        } else {
            ProcessedData.formattedString = resultString

            System.out.println(ProcessedData.documentNumber);

            /*val resultIntent = Intent(
                    this@ScanDrivingLicense,
                    ResultActivity::class.java
            )
            //setProgress(false)
            startActivity(resultIntent)*/

        }
    }

    fun loadAssureIDImage(url: String?, credential: Credential?): Bitmap? {
        if (url != null && credential != null) {
            val c = URL(url).openConnection() as HttpURLConnection
            val auth = CredentialHelper.getAcuantAuthHeader(credential)
            c.setRequestProperty("Authorization", auth)
            c.useCaches = false
            c.connect()
            val img = BitmapFactory.decodeStream(c.inputStream)
            c.disconnect()
            return img
        }
        return null
    }

    fun isBackSideRequired(classification : Classification?):Boolean{
        var isBackSideScanRequired = false
        if (classification?.type != null && classification.type.supportedImages != null) {
            val list = classification.type.supportedImages as ArrayList<HashMap<*, *>>
            for (i in list.indices) {
                val map = list[i]
                if (map["Light"] == 0) {
                    if (map["Side"] == 1) {
                        isBackSideScanRequired = true
                    }
                }
            }
        }
        return isBackSideScanRequired
    }

    //override fun onProgress(status: String, progress: Int) {
    //    //setProgress(true, "$progress%\n$status")
    //}

    //override fun onCancel() {
    //    //setProgress(true, "Getting Data...")
    //    capturingSelfieImage = false
    //    facialLivelinessResultString = "Facial Liveliness Failed"
    //}

    companion object {
        var image: Bitmap? = null
    }

    fun showDocumentCaptureCamera() {

        capturedBarcodeString = null
        val cameraIntent = Intent(
                this@ScanDrivingLicense,
                AcuantCameraActivity::class.java
        )
        cameraIntent.putExtra(ACUANT_EXTRA_CAMERA_OPTIONS,
                AcuantCameraOptions
                        .DocumentCameraOptionsBuilder()
                        .setAutoCapture(autoCaptureEnabled)
                        .build()
        )
        startActivityForResult(cameraIntent, Constants.REQUEST_CAMERA_PHOTO)
    }

}