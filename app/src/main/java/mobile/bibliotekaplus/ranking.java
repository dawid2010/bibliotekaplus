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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ranking extends AppCompatActivity {
    private GlobalClass globalClass;
    private EditText mDisplayDateStart;
    private EditText mDisplayDateEnd;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        globalClass = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        init();
        mDisplayDateStart = (EditText) findViewById(R.id.eTrankStart);
        mDisplayDateEnd = (EditText) findViewById(R.id.eTrankEnd);
        mDisplayDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ranking.this,
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
                mDisplayDateStart.setText(date);
            }
        };


        mDisplayDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ranking.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener2,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
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
                mDisplayDateEnd.setText(date);
            }
        };
    }
    private void init() {
        Button btnMap = (Button) findViewById(R.id.btnranking);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                EditText dataStart = (EditText) findViewById(R.id.eTrankStart);
                Date dateStartD = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    dateStartD = sdf.parse(dataStart.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                EditText dataEnd = (EditText) findViewById(R.id.eTrankEnd);
                Date dateEndD = null;
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    dateEndD = sdf2.parse(dataEnd.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                globalClass.setRankStart(dateStartD);
                globalClass.setRankEnd(dateEndD);
                Intent intent = new Intent(ranking.this, ranking_gen.class);
                startActivity(intent);
            }
        });
    }

}