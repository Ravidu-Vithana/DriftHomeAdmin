package com.ryvk.drifthomeadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailField = findViewById(R.id.editTextText2);

        Button signInButton = findViewById(R.id.button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(emailField);
                String email = emailField.getText().toString().trim();
                String otp = Utils.generateOTP();
                authenticateEmail(otp,email);
            }
        });

        checkUserInSP();

    }

    private void checkUserInSP(){
        Admin admin = Admin.getSPAdmin(MainActivity.this);
        if(admin != null){
            Intent i = new Intent(MainActivity.this, BaseActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void authenticateEmail(String otp , String email){
        ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admin")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot2 -> {
                    if (documentSnapshot2.exists()) {
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

                            MailSender.sendEmail(email, "OTP Verification for Drift Home Admin", htmlBody, MainActivity.this, new MailSender.MailSenderProcess() {
                                @Override
                                public void onCompletion() {
                                    storeOTP(otp, email);
                                    Intent i = new Intent(MainActivity.this, OtpVerificationActivity.class);
                                    i.putExtra("email", email);
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void onError(Exception e) {
                                    AlertUtils.showAlert(MainActivity.this,"Error","Email sending failed!");
                                    Log.e(TAG, "onError: Email sending failed", e);
                                }
                            });

                        }).start();

                    } else {
                        Log.d(TAG, "onFailure: Login Error : The account doesn't seem to exist!.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: Login Error : Data retrieval failed! Please restart the application.",e);
                });
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
}