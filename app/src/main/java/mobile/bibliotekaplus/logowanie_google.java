package mobile.bibliotekaplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class logowanie_google extends AppCompatActivity {
    private GlobalClass globalClass;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private  String TAG = "logowanie_google";
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private Button logowanieKlasyczne;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie_google);
        globalClass =(GlobalClass) getApplicationContext();
        loadUser();
        signInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        btnSignOut = findViewById(R.id.sign_out_button);
        logowanieKlasyczne = findViewById(R.id.logowanie_k_g);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                Toast.makeText(logowanie_google.this,"Wylogowałeś się",Toast.LENGTH_SHORT).show();
                btnSignOut.setVisibility(View.INVISIBLE);
                logowanieKlasyczne.setVisibility(View.INVISIBLE);
            }
        });

        klikDalej();
    }

    private ArrayList<ArrayList<String>> uzytkownicy = new ArrayList<>();
    private void klikDalej() {
        Button btnMap = (Button) findViewById(R.id.logowanie_k_g);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(validateUser()) {
                    Intent intent = new Intent(logowanie_google.this, menu_glowne.class);
                    startActivity(intent);
                }
            }
        });
    }
    private Boolean dodano=false;

    private void loadUser(){
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
                                long difference = Math.abs(dzis.getTime()-dataurodzenia.getTime());
                                difference = difference/ (24 * 60 * 60 * 1000)/365;
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(dataurodzenia);
                                int year = cal2.get(Calendar.YEAR);
                                int month = cal2.get(Calendar.MONTH);
                                int day = cal2.get(Calendar.DAY_OF_MONTH);
                                String dataurodzeniaT = day+"-"+(month+1)+"-"+year;
                                uzytkownik.add(difference+"");
                                uzytkownik.add(plec);
                                uzytkownik.add(dataurodzeniaT);
                                uzytkownicy.add(uzytkownik);
                            }
                        } else {
                            String taskExc = task.getException()+"";
                            Toast.makeText(logowanie_google.this,taskExc , Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    private String emailu="";
    private String nazwau="";
    private boolean validateUser(){
        TextView editTextMail;
        editTextMail =(TextView) findViewById(R.id.profile_email);
        String mail=emailu;
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
                    if(!dodano) {
                        addUser(mail, "M", "");
                    }
                }
            }
        }
        return autoryzacja;
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void addUser(String mail, String plec, String dataUrodzenia){
        // Create a nnew user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("mail", mail);
        user.put("rola", "2");
        user.put("autor","2");
        user.put("haslo","Autoryzacja przez Google");
        user.put("plec",plec);
        user.put("dataUrodzenia", Calendar.getInstance().getTime());
        // Add a nnew document with a generated ID

        if(!dodano) {
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
        if(dodano){
            Toast.makeText(this,"Dodano użytkownika",Toast.LENGTH_SHORT).show();

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
                                long difference = Math.abs(dzis.getTime()-dataurodzenia.getTime());
                                difference = difference/ (24 * 60 * 60 * 1000)/365;
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(dataurodzenia);
                                int year = cal2.get(Calendar.YEAR);
                                int month = cal2.get(Calendar.MONTH);
                                int day = cal2.get(Calendar.DAY_OF_MONTH);
                                String dataurodzeniaT = day+"-"+(month+1)+"-"+year;
                                uzytkownik.add(difference+"");
                                uzytkownik.add(plec);
                                uzytkownik.add(dataurodzeniaT);
                                uzytkownicy.add(uzytkownik);
                            }
                        } else {
                            String taskExc = task.getException() + "";
                            Toast.makeText(logowanie_google.this, taskExc, Toast.LENGTH_SHORT).show();

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

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(logowanie_google.this,"Logowanie pomyślne",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);


        }
        catch (ApiException e){
            Toast.makeText(logowanie_google.this,"Błędne pogowanie",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(logowanie_google.this, "Ok!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        String first_name = "";
                        String last_name ="";
                        String email  ="";
                        String dataurodzenia  ="";
                        String id  ="1";

                            first_name = user.getDisplayName();
                            last_name = user.getDisplayName();
                            email = user.getEmail();

                        emailu =email;
                        nazwau=(first_name);

                    } else {
                        Toast.makeText(logowanie_google.this, "Błąd!", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(logowanie_google.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(FirebaseUser fUser){
        btnSignOut.setVisibility(View.VISIBLE);
        logowanieKlasyczne.setVisibility(View.VISIBLE);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(logowanie_google.this,personName + personEmail ,Toast.LENGTH_SHORT).show();
        }

    }

}
