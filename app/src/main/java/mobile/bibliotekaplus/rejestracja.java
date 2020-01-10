package mobile.bibliotekaplus;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class rejestracja extends AppCompatActivity {

    private EditText mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejrestracja);
        mDisplayDate = (EditText) findViewById(R.id.rejdata);
        RadioButton plecK = (RadioButton) findViewById(R.id.rejplec);
        plecK.setChecked(true);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        rejestracja.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date;
                month = month + 1;

                if(month<10){
                    date= day  + "-0" + month + "-" + year;
                }
                else{
                    date = day  + "-" + month + "-" + year;
                }
                mDisplayDate.setText(date);
            }
        };
        initRejestracja();

    }

    private void initRejestracja() {
        Button btnMap = (Button) findViewById(R.id.btnDodaj);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
               Boolean rejestracjaPomyslna = false;

               String plec;
                RadioButton plecK = (RadioButton) findViewById(R.id.rejplec);
                RadioButton plecM = (RadioButton) findViewById(R.id.rejplecM);
                EditText haslo = (EditText)findViewById(R.id.rejhaslo);
                EditText haslo2 = (EditText)findViewById(R.id.rejhaslo2);
                EditText mail = (EditText)findViewById(R.id.rejemail);
                EditText data = (EditText)findViewById(R.id.rejdata);
                if(!mail.getText().toString().equals("")){
                    if(!haslo.getText().toString().equals("")){
                        if(haslo.getText().toString().equals(haslo2.getText().toString())){
                            if(!data.getText().equals("")){
                                rejestracjaPomyslna=true;
                            }
                            else{
                                Toast.makeText(rejestracja.this, "Uzupełnij datę urodzenia ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(rejestracja.this, "Hasła muszą być identycznę ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(rejestracja.this, "Uzupełnij hasło ", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(rejestracja.this, "Uzupełnij mail ", Toast.LENGTH_SHORT).show();
                }

                 FirebaseFirestore db = FirebaseFirestore.getInstance();

                if(plecK.isChecked())
                {
                    plec = "K";
                }
                else
                {
                    plec = "M";
                }
                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    date = sdf.parse(data.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Map<String, Object> user = new HashMap<>();
                user.put("autor", "User");
                user.put("mail", mail.getText().toString());
                user.put("haslo", haslo.getText().toString());
                user.put("rola", "2".toString());
                user.put("plec", plec.toString());
                user.put("dataUrodzenia",date);

                if(rejestracjaPomyslna) {
                    db.collection("uzytkownicy").document(mail.getText().toString())
                            .set(user)
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

                    Toast.makeText(rejestracja.this, "Uzytkownik został utworzony! ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(rejestracja.this, logowanie.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(rejestracja.this, "Uzupełnij wszystkie pola ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}