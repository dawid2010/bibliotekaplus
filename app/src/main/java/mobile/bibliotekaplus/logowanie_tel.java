package mobile.bibliotekaplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class logowanie_tel extends AppCompatActivity {

    EditText editTextPhone, editTextCode;
    FirebaseAuth mAuth;
    String codeSent;
    private GlobalClass globalClass;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        globalClass = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie_tel);
        mAuth = FirebaseAuth.getInstance();
        loadUser();
        callbackManager = CallbackManager.Factory.create();
        editTextCode = findViewById(R.id.editTextCode);
        editTextPhone = findViewById(R.id.editTextPhone);


        findViewById(R.id.buttonGetVerificationCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerifcationCode();
            }
        });

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignInCode();
            }
        });
    }



    private void verifySignInCode() {
        String code = editTextCode.getText().toString();
        verifyCode(codeSent);
    }



    private ArrayList<ArrayList<String>> uzytkownicy = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void loadUser() {
        db.collection("uzytkownicy")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> uzytkownik = new ArrayList<>();
                                String mailF = document.getString("mail");
                                String hasloF = document.getString("haslo");
                                Date dataurodzenia = document.getDate("dataUrodzenia");
                                String plec = document.getString("plec");
                                uzytkownik.add(mailF);
                                uzytkownik.add(hasloF);
                                uzytkownik.add(document.getId());
                                Calendar cal = Calendar.getInstance();
                                Date dzis = cal.getTime();
                                long difference = Math.abs(dzis.getTime() - dataurodzenia.getTime());
                                difference = difference / (24 * 60 * 60 * 1000) / 365;
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(dataurodzenia);
                                int year = cal2.get(Calendar.YEAR);
                                int month = cal2.get(Calendar.MONTH);
                                int day = cal2.get(Calendar.DAY_OF_MONTH);
                                String dataurodzeniaT = day + "-" + (month + 1) + "-" + year;
                                uzytkownik.add(difference + "");
                                uzytkownik.add(plec);
                                uzytkownik.add(dataurodzeniaT);
                                uzytkownicy.add(uzytkownik);
                            }
                        } else {
                            String taskExc = task.getException() + "";
                            Toast.makeText(logowanie_tel.this, taskExc, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private Boolean dodano = false;

    private void addUser(String mail, String plec, String dataUrodzenia) {
        // Create a nnew user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("mail", mail);
        user.put("rola", "2");
        user.put("autor", "2");
        user.put("haslo", "Autoryzacja przez FB");
        user.put("plec", plec);
        user.put("dataUrodzenia", Calendar.getInstance().getTime());
        // Add a nnew document with a generated ID
        TextView editTextMail;
        editTextMail = (TextView) findViewById(R.id.profile_email);

        if (!dodano) {
            if (!loadUserAuth(mail)) {
                dodano = true;
                db.collection("uzytkownicy")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });

            } else {
                //Toast.makeText(this,"Użytkownik o podanym loginie już istnieje",Toast.LENGTH_SHORT).show();
            }
        }
        if (dodano) {
            Toast.makeText(this, "Dodano użytkownika", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean loadUserAuth(String mail) {
        db.collection("uzytkownicy")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> uzytkownik = new ArrayList<>();
                                String mailF = document.getString("mail");
                                String hasloF = document.getString("haslo");
                                Date dataurodzenia = document.getDate("dataUrodzenia");
                                String plec = document.getString("plec");
                                uzytkownik.add(mailF);
                                uzytkownik.add(hasloF);
                                uzytkownik.add(document.getId());
                                Calendar cal = Calendar.getInstance();
                                Date dzis = cal.getTime();
                                long difference = Math.abs(dzis.getTime() - dataurodzenia.getTime());
                                difference = difference / (24 * 60 * 60 * 1000) / 365;
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(dataurodzenia);
                                int year = cal2.get(Calendar.YEAR);
                                int month = cal2.get(Calendar.MONTH);
                                int day = cal2.get(Calendar.DAY_OF_MONTH);
                                String dataurodzeniaT = day + "-" + (month + 1) + "-" + year;
                                uzytkownik.add(difference + "");
                                uzytkownik.add(plec);
                                uzytkownik.add(dataurodzeniaT);
                                uzytkownicy.add(uzytkownik);
                            }
                        } else {
                            String taskExc = task.getException() + "";
                            Toast.makeText(logowanie_tel.this, taskExc, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        Boolean autoryzacja = false;
        for (ArrayList<String> u : uzytkownicy) {
            if (u.get(0).equals(mail)) {
                autoryzacja = true;
            } else {
                // Toast.makeText(this, "Brak użytkownika, zarejestruj się lub zaloguj się przez social media", Toast.LENGTH_SHORT).show();
            }
        }
        return autoryzacja;

    }

    private boolean validateUser() {
        /*

        Boolean autoryzacja = false;
        String dataurodzenia = globalClass.getUrodzinyFb();

        }*/
        TextView editTextMail;
        editTextMail = (TextView) findViewById(R.id.editTextPhone);
        String mail = editTextMail.getText().toString().trim();
        Boolean autoryzacja = false;
        for (ArrayList<String> u : uzytkownicy) {
            if (u.get(0).equals(mail)) {
                autoryzacja = true;
                globalClass.setMail(u.get(0));
                globalClass.setUserId(u.get(2));
                globalClass.setUserWiek(Double.parseDouble(u.get(3)));
                globalClass.setPlec(u.get(4));
                globalClass.setDataUrodzenia(u.get(5));
            } else {
                if (mail.equals("")) {
                    //Toast.makeText(this, "Brak użytkownika, zarejestruj się lub zaloguj się przez social media", Toast.LENGTH_SHORT).show();
                } else {
                    if (!dodano) {
                        addUser(mail, "M", "");
                    }

                }
            }
        }
        return autoryzacja;
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //here you can open new activity
                            Toast.makeText(getApplicationContext(),
                                    "Logowanie poprawne", Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(logowanie_tel.this, menu_glowne.class);
                            if (validateUser()) {
                                startActivity(intent);
                            } else {
                                startActivity(intent);
                            }
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Błędny kod", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendVerifcationCode() {
        String phone = editTextPhone.getText().toString();

        if (phone.isEmpty()) {
            editTextPhone.setError("Podaj poprawnie numer telefonu");
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            editTextPhone.setError("Podaj poprawny numer telefonu, podaj numer kraju +48");
            editTextPhone.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(),
                    "Weryfikacja poprawna", Toast.LENGTH_LONG).show();
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),
                    "Weryfikacja błędna", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

}
