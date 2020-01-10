package mobile.bibliotekaplus;

import android.app.DatePickerDialog;
import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ustawienia extends AppCompatActivity {
    private EditText mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private GlobalClass globalClass;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = getApplicationContext();
        loadUser();
        globalClass =(GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustawienia);
            mDisplayDate = (EditText) findViewById(R.id.rejdataU);
            RadioButton plecK = (RadioButton) findViewById(R.id.rejplecU);
            plecK.setChecked(true);
        initDelete();

            mDisplayDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            ustawienia.this,
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
        RadioButton plecM = (RadioButton) findViewById(R.id.rejplecMU);
        EditText haslo = (EditText)findViewById(R.id.rejhasloU);
        EditText haslo2 = (EditText)findViewById(R.id.rejhaslo2U);
        EditText mail = (EditText)findViewById(R.id.rejemailU);
        EditText data = (EditText)findViewById(R.id.rejdataU);
        if(globalClass.getPlec().equals("K")) {
            plecK.setChecked(true);
            plecM.setChecked(false);
        }
        else{
            plecM.setChecked(true);
            plecK.setChecked(false);
        }
        mail.setText(globalClass.getMail());
        data.setText(globalClass.getDataUrodzenia());
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
                        }
                    }
                });
    }

    private void initDelete() {
        Button btnMap = (Button) findViewById(R.id.btnusun);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDialog(context);
            }
        });
    }

    public void openDialog(Context context) {
        DialogDeleteUser exampleDialog = new DialogDeleteUser(context);
        exampleDialog.show(getSupportFragmentManager(),"example");
    }

    private void initRejestracja() {
            Button btnMap = (Button) findViewById(R.id.btnZapisUst);
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Boolean rejestracjaPomyslna = false;

                    String plec;
                    RadioButton plecK = (RadioButton) findViewById(R.id.rejplecU);
                    RadioButton plecM = (RadioButton) findViewById(R.id.rejplecMU);
                    EditText haslo = (EditText)findViewById(R.id.rejhasloU);
                    EditText haslo2 = (EditText)findViewById(R.id.rejhaslo2U);
                    EditText mail = (EditText)findViewById(R.id.rejemailU);
                    EditText data = (EditText)findViewById(R.id.rejdataU);
                    if(!mail.getText().toString().equals("")){
                        if(!haslo.getText().toString().equals("")){
                            if(haslo.getText().toString().equals(haslo2.getText().toString())){
                                if(!data.getText().equals("")){
                                    rejestracjaPomyslna=true;
                                }
                                else{
                                    Toast.makeText(ustawienia.this, "Uzupełnij datę urodzenia ", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(ustawienia.this, "Hasła muszą być identycznę ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(ustawienia.this, "Uzupełnij hasło ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(ustawienia.this, "Uzupełnij mail ", Toast.LENGTH_SHORT).show();
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
                    user.put("rola", 2);
                    user.put("plec", plec.toString());
                    user.put("dataUrodzenia",date);

                    if(rejestracjaPomyslna) {
                        db.collection("uzytkownicy").document(globalClass.getUserId())
                        .update("mail", mail.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                        db.collection("uzytkownicy").document(globalClass.getUserId())
                        .update("haslo", haslo.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                        db.collection("uzytkownicy").document(globalClass.getUserId())
                        .update("plec", plec.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                        db.collection("uzytkownicy").document(globalClass.getUserId())
                        .update("dataUrodzenia",date)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });

                        Toast.makeText(ustawienia.this, "Uzytkownik został updatowany! ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ustawienia.this, logowanie.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(ustawienia.this, "Uzupełnij wszystkie pola ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
