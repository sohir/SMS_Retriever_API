package com.example.smsretriver

import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.smsretriver.MySMSBroadcastReceiver.OTPReceiveListener
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() , OTPReceiveListener {
private val RC_HINT = 2
    private val smsBroadcastReceiver by lazy { MySMSBroadcastReceiver() }
    override fun onStart() {
        super.onStart()
    }

    //lateinit var mySMSBroadcastReceiver:MySMSBroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val appSignature = AppSignatureHelper(this)
        appSignature.appSignatures
        btn.setOnClickListener(View.OnClickListener {

            startSmsListener()
        })
    }
    private fun startSmsListener(){
        val client = SmsRetriever.getClient(this /* context */)
        val task = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            // ...
            otp_txt.text = "Waiting for the OTP"
            val otpListener = object :MySMSBroadcastReceiver.OTPReceiveListener{
                override fun onOTPReceived(otp: String) {
                    Toast.makeText(this@MainActivity, "Otp Received $otp", Toast.LENGTH_LONG).show();
                    otp_txt.text="Otp Received $otp"
                    Log.d("OTP_Message_main", otp)                }

                override fun onOTPTimeOut() {
                    Toast.makeText(this@MainActivity, "Time out", Toast.LENGTH_LONG).show();

                }
            }
            smsBroadcastReceiver.initOTPListener(otpListener)
            registerReceiver(smsBroadcastReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
            // ...
            otp_txt.text = "Cannot Start SMS Retriever"
        }
    }

    override fun onOTPReceived(otp: String) {
        Toast.makeText(this, "Otp Received $otp", Toast.LENGTH_LONG).show();
        otp_txt.text="Otp Received $otp"
        Log.d("OTP_Message_main", otp)
    }

    override fun onOTPTimeOut() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)
    }
    //Obtain number through hint picker (Optional)

//    @SuppressLint("LongLogTag")
//    private fun requestHint(){
//
//
//        val hintRequest = HintRequest.Builder()
//            .setPhoneNumberIdentifierSupported(true)
//            .build()
//
//        val intent = Auth.CredentialsApi.getHintPickerIntent(
//            mCredentialsApiClient, hintRequest)
//
//        try {
//            startIntentSenderForResult(intent.intentSender,
//                RC_HINT, null, 0, 0, 0)
//        } catch (e: Exception) {
//            Log.e("Error In getting Message", e.message)
//        }
//    }
}
