package mobile.bibliotekaplus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class addbook extends AppCompatActivity {
    private GlobalClass globalClass;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        globalClass = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

    }
}