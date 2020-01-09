package mobile.bibliotekaplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addbook extends AppCompatActivity {
    private GlobalClass globalClass;

    TextView autor;
    TextView tytul;
    TextView gatunek;
    TextView rodzaj;
    TextView epoka;
    TextView urlOkladka;
    TextView kaucja;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        globalClass = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);


         autor = findViewById(R.id.autor);
         tytul = findViewById(R.id.tytul);
         gatunek = findViewById(R.id.gatunek);
         rodzaj = findViewById(R.id.rodzaj);
         epoka = findViewById(R.id.epoka);
         urlOkladka = findViewById(R.id.urlOkladka);
         kaucja = findViewById(R.id.kaucja);


    }

    public void dodajKsiazke(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss z");
        String date = df.format(Calendar.getInstance().getTime());

        Log.d("Format",date.toString());

        Map<String, Object> book = new HashMap<>();
        book.put("autor", autor.getText().toString());
        book.put("dodano", date.toString());
        book.put("epoka", epoka.getText().toString());
        book.put("gatunek", gatunek.getText().toString());
        book.put("kaucja", Float.parseFloat(String.valueOf(kaucja.getText())));
        book.put("okladka", urlOkladka.getText().toString());
        book.put("rodzaj", rodzaj.getText().toString());
        book.put("tytul", tytul.getText().toString());

        db.collection("ksiazka").document(tytul.getText().toString())
                .set(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error writing document", e);
                    }
                });

        Toast.makeText(addbook.this, "Ksiazka została dodana! ", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(rejestracja.this, logowanie.class);
        //startActivity(intent);
    }
}