package mobile.bibliotekaplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        globalClass =(GlobalClass) getApplicationContext();
        loadUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie_fb);

        loginButton = findViewById(R.id.login_button);
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
                Toast.makeText(logowanie_fb.this,"Logowanie nastąpiło pomyślne",Toast.LENGTH_LONG).show();

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
                                uzytkownik.add(mailF);
                                uzytkownik.add(hasloF);
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
                                uzytkownik.add(difference+"");
                                uzytkownik.add(plec);
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
                Toast.makeText(this, "Brak użytkownika, zarejestruj się lub zaloguj się przez social media", Toast.LENGTH_SHORT).show();
            }
        }
        return autoryzacja;

    }

    private Boolean dodano=false;

    private void addUser(String mail, String plec, String dataUrodzenia){
        // Create a nnew user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("mail", mail);
        user.put("rola", 2);
        user.put("autor",2);
        user.put("haslo","Autoryzacja przez FB");
        user.put("plec",plec);
        user.put("dataUrodzenia",Calendar.getInstance().getTime());
        // Add a nnew document with a generated ID
        TextView editTextMail;
        editTextMail =(TextView) findViewById(R.id.profile_email);


        if(!loadUserAuth(mail)) {
            db.collection("uzytkownicy")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            dodano=true;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dodano=false;
                        }
                    });
            if(dodano){
                Toast.makeText(this,"Dodano użytkownika",Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(this,"Użytkownik o podanym loginie już istnieje",Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<ArrayList<String>> uzytkownicy = new ArrayList<>();
    private void klikDalej() {
        Button btnMap = (Button) findViewById(R.id.logowanie_k);
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

    private boolean validateUser(){
        TextView editTextMail;
        editTextMail =(TextView) findViewById(R.id.profile_email);
        String mail = editTextMail.getText().toString().trim();
        Boolean autoryzacja = false;
        String dataurodzenia = globalClass.getUrodzinyFb();
        for(ArrayList<String> u:uzytkownicy){
            if(u.get(0).equals(mail)){
                    autoryzacja=true;
                    globalClass.setUserId(u.get(2));
                    globalClass.setUserWiek(Double.parseDouble(u.get(3)));
                    globalClass.setPlec(u.get(4));
            }
            else{
                if(mail.equals("")){
                    Toast.makeText(this,"Brak użytkownika, zarejestruj się lub zaloguj się przez social media",Toast.LENGTH_SHORT).show();
                }
                else{
                    addUser(mail,"M",dataurodzenia);
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

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {

        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {
            if(currentAccessToken==null)
            {
                txtName.setText("");
                txtEmail.setText("");
                circleImageView.setImageResource(0);
                Toast.makeText(logowanie_fb.this,"User Logged out",Toast.LENGTH_LONG).show();
            }
            else {
                loadUser();
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String dataurodzenia = object.getString("birthday");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";

                    txtEmail.setText(email);
                    txtName.setText(first_name +" "+last_name);
                    globalClass.setUrodzinyFb(dataurodzenia);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    Glide.with(logowanie_fb.this).load(image_url).into(circleImageView);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
