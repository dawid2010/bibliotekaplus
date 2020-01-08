package mobile.bibliotekaplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class logowanie extends AppCompatActivity {

    private ImageView imageView;
    private GlobalClass globalClass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
        globalClass =(GlobalClass) getApplicationContext();

        imageView =(ImageView) findViewById(R.id.logo);
        loadUser();
        animateShakeLogo();
        init();
        initFB();
        initRejestracja();
    }

    private void animateShakeLogo() {
        Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
        imageView.setAnimation(shake);
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.logowanie_d);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(validateUser()) {

                    Intent intent = new Intent(logowanie.this, menu_glowne.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initRejestracja(){
        Button btnMap = (Button) findViewById(R.id.btnrejestracja);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                    Intent intent = new Intent(logowanie.this, rejestracja.class);
                    startActivity(intent);

            }
        });
    }

    private void initFB(){
        Button btnMap = (Button) findViewById(R.id.logowanie_fb);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(logowanie.this, logowanie_fb.class);
                startActivity(intent);

            }
        });
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<ArrayList<String>> uzytkownicy = new ArrayList<>();
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
                                uzytkownik.add(difference+"");
                                uzytkownik.add(plec);
                                uzytkownicy.add(uzytkownik);
                            }
                        } else {
                            String taskExc = task.getException()+"";
                            Toast.makeText(logowanie.this,taskExc , Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean validateUser(){
         EditText editTextMail;
         EditText editTextHaslo;
        editTextMail =(EditText) findViewById(R.id.mail);
        editTextHaslo = (EditText)findViewById(R.id.haslo);
        String mail = editTextMail.getText().toString().trim();
        String haslo = editTextHaslo.getText().toString().trim();
        Boolean autoryzacja = false;
        for(ArrayList<String> u:uzytkownicy){
            if(u.get(0).equals(mail)){
                if(u.get(1).equals(haslo)){
                    autoryzacja=true;
                    globalClass.setUserId(u.get(2));
                    globalClass.setUserWiek(Double.parseDouble(u.get(3)));
                    globalClass.setPlec(u.get(4));
                }
                else{
                    Toast.makeText(this,"Błędne hasło",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this,"Brak użytkownika, zarejestruj się lub zaloguj się przez social media",Toast.LENGTH_SHORT).show();
            }
        }


        return autoryzacja;
    }
}
