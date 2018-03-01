package com.nisaapp.Authentication;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.nisaapp.Firebase;
import com.nisaapp.Profile.EditProfile;
import com.nisaapp.R;

import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class PhoneVerification extends Fragment implements View.OnClickListener {

    EditText phoneNumber;
    EditText OTP;
    Button verify;
    Button googleSignIn;
    boolean codeSent;
    String mVerificationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.phone_verification, container, false);

        OTP = rootView.findViewById(R.id.OTP);
        OTP.setVisibility(View.GONE);

        Context c = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? getContext() : getActivity();
        TelephonyManager tMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String number = tMgr.getLine1Number();

        phoneNumber = rootView.findViewById(R.id.PhoneNumber);
        phoneNumber.setText("+91" + number);

        verify = rootView.findViewById(R.id.verify);
        verify.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.verify:
                verify();
                break;
        }
    }

    private void verify() {
        String number = phoneNumber.getText().toString();
        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.

                Firebase.auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.w("SignIn", "Successful");
                            Firebase.currentUser = Firebase.auth.getCurrentUser();
                            Firebase.currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    Firebase.idToken = task.getResult().getToken();
                                    Firebase.db = FirebaseFirestore.getInstance();
                                    Firebase.UID = Firebase.currentUser.getUid();
                                    Firebase.storageReference = FirebaseStorage.getInstance().getReference();
                                }
                            });

                        } else {
                            Log.w("SignIn", "Failure");
                        }
                    }
                });

                googleSignIn.setVisibility(View.VISIBLE);
                verify.setVisibility(View.GONE);
                EditProfile pc = new EditProfile();
                getFragmentManager().beginTransaction().replace(R.id.fragment, pc).commit();


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Failed", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                //Crashlytics.log("Code Sent");

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                codeSent = TRUE;
                OTP.setVisibility(View.VISIBLE);
                verify.setText(R.string.buttonAfterOTPSent);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        verify.setText(R.string.buttonAfterOTPTimeout);
                        OTP.setVisibility(View.GONE);
                        codeSent = FALSE;
                    }
                }, 120000);

            }
        };

        if (codeSent) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, OTP.getText().toString());
            mCallbacks.onVerificationCompleted(credential);

        } else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    number,        // Phone number to verify
                    120,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }
    }
}
