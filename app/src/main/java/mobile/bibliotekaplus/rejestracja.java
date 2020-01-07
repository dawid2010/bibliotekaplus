package mobile.bibliotekaplus;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Calendar;

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
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
        initRejestracja();

    }

    private void initRejestracja() {
        Button btnMap = (Button) findViewById(R.id.btnrejestracjaK);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
               Boolean rejestracjaPomyslna=false;

                RadioButton plecK2 = (RadioButton) findViewById(R.id.rejplec);
                RadioButton plecM = (RadioButton) findViewById(R.id.rejplecM);
                EditText haslo = (EditText)findViewById(R.id.rejhaslo);
                EditText haslo2 = (EditText)findViewById(R.id.rejhaslo2);
                EditText mail = (EditText)findViewById(R.id.rejemail);
                EditText data = (EditText)findViewById(R.id.rejdata);
                if(!mail.getText().equals("")){
                    if(!haslo.getText().equals("")){
                        if(haslo.getText().equals(haslo2.getText())){
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

                if(rejestracjaPomyslna) {
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