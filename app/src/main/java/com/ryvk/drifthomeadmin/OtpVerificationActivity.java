package com.ryvk.drifthomeadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class OtpVerificationActivity extends AppCompatActivity{
    private static final String TAG = "OtpVerificationActivity";
    private String adminEmail;
    private ProgressBar progressBar;
    private OnBackPressedCallback callback;
    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private Button resendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertUtils.showConfirmDialog(OtpVerificationActivity.this, "Cancel Verification?", "Are you sure you want to cancel.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent backIntent = new Intent(OtpVerificationActivity.this,MainActivity.class);
                        startActivity(backIntent);
                        finish();
                    }
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        EditText otpField = findViewById(R.id.editTextNumberPassword);
        Button verifyButton = findViewById(R.id.button2);
        progressBar = findViewById(R.id.progressBar);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(otpField);
                runOnUiThread(()->progressBar.setVisibility(View.VISIBLE));
                String enteredOtp = otpField.getText().toString().trim();
                authenticate(enteredOtp);
            }
        });
        resendButton = findViewById(R.id.button7);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newOtp = Utils.generateOTP();
                resendOTP(newOtp);
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            adminEmail = intent.getStringExtra("email");
            startCountdown();
        }else{
            AlertUtils.showAlert(OtpVerificationActivity.this,"Error","An error occured. Please try again later.");
        }

        timerTextView = findViewById(R.id.timerTextView);
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 60000);
                int seconds = (int) ((millisUntilFinished % 60000) / 1000);
                runOnUiThread(()->timerTextView.setText(String.format("%02d:%02d", minutes, seconds)));
            }

            @Override
            public void onFinish() {
                runOnUiThread(()->timerTextView.setText("00:00"));
                storeOTP("000000",adminEmail);
                runOnUiThread(()->{
                    resendButton.setTextColor(ContextCompat.getColor(OtpVerificationActivity.this, R.color.d_blue));
                    resendButton.setEnabled(true);
                });
            }
        }.start();
    }
    private void authenticate(String enteredOtp){
        new Thread(()->{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("admin")
                    .document(adminEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Admin admin = documentSnapshot.toObject(Admin.class);
                                if(admin != null && admin.getOtp() != null){
                                    if(admin.getOtp().equals("000000")){
                                        runOnUiThread(()->progressBar.setVisibility(View.INVISIBLE));
                                        AlertUtils.showAlert(OtpVerificationActivity.this,"Oops","OTP is already expired. Please resend.");
                                    }else if(admin.getOtp().equals(enteredOtp)){
                                        admin.updateSPAdmin(OtpVerificationActivity.this,admin);
                                        Intent i = new Intent(OtpVerificationActivity.this, BaseActivity.class);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        runOnUiThread(()->progressBar.setVisibility(View.INVISIBLE));
                                        AlertUtils.showAlert(OtpVerificationActivity.this,"Error","OTP entered is wrong!");
                                    }
                                }else{
                                    runOnUiThread(()->progressBar.setVisibility(View.INVISIBLE));
                                    Log.d(TAG, "onSuccess: admin data is empty. Authentication failed");
                                    AlertUtils.showAlert(OtpVerificationActivity.this,"Error","An error has occurred. Please try again later.");
                                }
                            }else{
                                runOnUiThread(()->progressBar.setVisibility(View.INVISIBLE));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            runOnUiThread(()->progressBar.setVisibility(View.INVISIBLE));
                            AlertUtils.showAlert(OtpVerificationActivity.this,"Error","An error occured when authenticating. Please try again later.");
                            Log.e(TAG, "authenticate otp: failure",e);
                        }
                    });
        }).start();
    }
    private void resendOTP(String otp){
        runOnUiThread(()->{
            resendButton.setTextColor(ContextCompat.getColor(OtpVerificationActivity.this, R.color.d_blue_disabled));
            resendButton.setEnabled(false);
        });
        new Thread(()->{

            String htmlBody = "<!DOCTYPE html>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "    <meta charset=\"UTF-8\">" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "    <title>DriftHome Admin OTP Verification</title>" +
                    "</head>" +
                    "<body style=\"font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; color: #333;\">" +
                    "    <table style=\"max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 8px;\">" +
                    "        <tr>" +
                    "            <td>" +
                    "                <h2 style=\"color: #5664F5; font-size: 24px; margin-bottom: 20px;\">DriftHome Admin Login - OTP Verification</h2>" +
                    "                <p style=\"font-size: 16px;\">Dear Admin,</p>" +
                    "                <p style=\"font-size: 16px;\">To verify your login request for the DriftHome admin panel, please use the following One-Time Password (OTP):</p>" +
                    "                <h3 style=\"font-size: 22px; color: #F5A356; font-weight: bold;\">Your OTP is:</h3>" +
                    "                <p style=\"font-size: 20px; color: #F55656; font-weight: bold;\">" + otp + "</p>" +
                    "                <p style=\"font-size: 16px;\">Enter this code on the login page to proceed. The OTP is valid for 2 minutes.</p>" +
                    "                <p style=\"font-size: 16px;\">If you did not request this login, please disregard this email.</p>" +
                    "                <br>" +
                    "                <p style=\"font-size: 16px; color: #888;\">Thank you,</p>" +
                    "                <p style=\"font-size: 16px; color: #888;\">The DriftHome Team</p>" +
                    "            </td>" +
                    "        </tr>" +
                    "    </table>" +
                    "</body>" +
                    "</html>";

            MailSender.sendEmail(adminEmail, "OTP Verification for Drift Home Admin", htmlBody, OtpVerificationActivity.this, new MailSender.MailSenderProcess() {
                @Override
                public void onCompletion() {
                    startCountdown();
                    storeOTP(otp, adminEmail);
                    Toast.makeText(OtpVerificationActivity.this,"OTP resent successfully!",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception e) {
                    AlertUtils.showAlert(OtpVerificationActivity.this,"Error","Email sending failed!");
                    Log.e(TAG, "onError: Email sending failed", e);
                }
            });

        }).start();
    }
    private void storeOTP(String otp, String email){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admin")
                .document(email)
                .update("otp",otp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "store otp: success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "store otp: failure",e);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}