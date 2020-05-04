package com.example.smsretriver

import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.TintableBackgroundView
import androidx.core.view.get
import com.example.smsretriver.MySMSBroadcastReceiver.OTPReceiveListener
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() , OnOtpCompletionListener {
private val RC_HINT = 2
    private var  otpFinal:String?=null
    private val smsBroadcastReceiver by lazy { MySMSBroadcastReceiver() }
    override fun onStart() {
        super.onStart()
    }

    //lateinit var mySMSBroadcastReceiver:MySMSBroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appSignature = AppSignatureHelper(this)
        otp_view.setOtpCompletionListener(this)
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
            Toast.makeText(this@MainActivity, "Waiting for the OTP", Toast.LENGTH_SHORT).show();

            val otpListener = object :MySMSBroadcastReceiver.OTPReceiveListener{
                override fun onOTPReceived(otp: String) {
                    Toast.makeText(this@MainActivity, "Otp Received $otp", Toast.LENGTH_LONG).show();
                    otp_view.setText(otp)
                    otpFinal = otp
                    Log.d("OTP_Message_main", otp)

                }

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
            Toast.makeText(this,"Cannot start SMS retriver",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onOtpCompleted(otp: String?) {
        Log.d("otp=>", otp)

      otpFinal.let {
          if (otpFinal.equals(otp)){
              Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
          }else{
              Toast.makeText(this,"not Success",Toast.LENGTH_LONG).show()
          }
      }
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




