package mobile.bibliotekaplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class kody_szcz extends AppCompatActivity {
    private GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kody_szcz);
        globalClass =(GlobalClass) getApplicationContext();
        myListView= findViewById(R.id.myListView);
        final ProgressBar myProgressBar= findViewById(R.id.myProgressBar);
        SearchView mySearchView=findViewById(R.id.mySearchView);

        mySearchView.setIconified(true);
        mySearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
        spacecrafts=new FirebaseKodySzczDonloader(this).retrieve(myListView,myProgressBar);
        adapter=new ListViewAdapterModelKosz(this,spacecrafts);
        myListView.setAdapter(adapter);
    }


    /*
    Our custom adapter class
     */
    public void openDialog(String ksiazkaId) {
        globalClass.setKsiazkaId(ksiazkaId);
        DialogDelete exampleDialog = new DialogDelete();
        exampleDialog.show(getSupportFragmentManager(),"example");
    }

    private String name;
    private String propellant;
    private String techExists;
    private String imageURL;
    private Spacecraft s;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Spacecraft> spacecrafts = new ArrayList<>();
    ListView myListView;
    ListViewAdapterModelKosz adapter;


    public void OnClickRealizuj(View view) {
        Intent intent = new Intent(this, koszykPlacowka.class);
        this.startActivity ( intent );
    }

}