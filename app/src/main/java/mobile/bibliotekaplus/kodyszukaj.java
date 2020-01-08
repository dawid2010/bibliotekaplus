package mobile.bibliotekaplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class kodyszukaj extends AppCompatActivity {
    private GlobalClass globalClass;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        globalClass =(GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kody_szukaj);
        init();
    }

    private void init() {
        Button btnMap = (Button) findViewById(R.id.btnranking);


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                EditText editText = (EditText) findViewById(R.id.suzkajkodutext);
                globalClass.setZamowienieId(editText.getText().toString());
                Intent intent = new Intent(kodyszukaj.this, kody_szcz.class);
                startActivity(intent);
            }
        });
    }
}
