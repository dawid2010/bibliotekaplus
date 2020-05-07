package mobile.bibliotekaplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class logowanie_fb  extends AppCompatActivity {
    private LoginButton loginButton;
    private CircleImageView circleImageView;
    private TextView txtName,txtEmail;
    private GlobalClass globalClass;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        globalClass =(GlobalClass) getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        loadUser();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logowanie_fb);

        loginButton = findViewById(R.id.login_button_fb);
        txtName = findViewById(R.id.profile_name);
        txtEmail = findViewById(R.id.profile_email);
        circleImageView = findViewById(R.id.profile_pic);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        checkLoginStatus();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Toast.makeText(logowanie_fb.this,"Logowanie nastąpiło pomyślne",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(logowanie_fb.this, menu_glowne.class);
                if(validateUser()) {
                    startActivity(intent);
                }
                else{
                    startActivity(intent);
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(logowanie_fb.this,"Anulowanie logowania",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(logowanie_fb.this,"Błąd",Toast.LENGTH_LONG).show();
            }
        });

        klikDalej();
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                            Toast.makeText(logowanie_fb.this,taskExc , Toast.LENGTH_SHORT).show();

                        }
                    }
                });
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
                            Toast.makeText(logowanie_fb.this, taskExc, Toast.LENGTH_SHORT).show();

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

    private Boolean dodano=false;

    private void addUser(String mail, String plec, String dataUrodzenia){
        // Create a nnew user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("mail", mail);
        user.put("rola", "2");
        user.put("autor","2");
        user.put("haslo","Autoryzacja przez FB");
        user.put("plec",plec);
        user.put("dataUrodzenia",Calendar.getInstance().getTime());
        // Add a nnew document with a generated ID
        TextView editTextMail;
        editTextMail =(TextView) findViewById(R.id.profile_email);

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

    private ArrayList<ArrayList<String>> uzytkownicy = new ArrayList<>();
    private void klikDalej() {
        Button btnMap = (Button) findViewById(R.id.logowanie_k_g);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(validateUser()) {
                    Intent intent = new Intent(logowanie_fb.this, menu_glowne.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private boolean validateUser(){
        /*

        Boolean autoryzacja = false;
        String dataurodzenia = globalClass.getUrodzinyFb();

        }*/
        TextView editTextMail;
        editTextMail =(TextView) findViewById(R.id.profile_email);
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
                        if(!dodano) {
                            addUser(mail, "M", "");
                        }

                    }
                }
            }
        return autoryzacja;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.


                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                String first_name = "";
                String last_name ="";
                String email  ="";
                String dataurodzenia  ="";
                String id  ="1";
                String image_url  = "https://graph.facebook.com/"+id+ "/picture?type=normal";
                try {
                     first_name = object.getString("first_name");
                     last_name = object.getString("last_name");
                     email = object.getString("email");
                     id = object.getString("id");
                     image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";


                } catch (JSONException e) {
                    //Toast.makeText(logowanie_fb.this,"Bład: "+e.toString(),Toast.LENGTH_LONG).show();
                }
                txtEmail.setText(email);
                txtName.setText(first_name +" "+last_name);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                Glide.with(logowanie_fb.this).load(image_url).into(circleImageView);



            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void checkLoginStatus()
    {
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            loadUserProfile(AccessToken.getCurrentAccessToken());
            Toast.makeText(logowanie_fb.this,"Jesteś już zalogowany",Toast.LENGTH_LONG).show();

        }
    }

}
