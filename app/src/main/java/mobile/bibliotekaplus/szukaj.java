package mobile.bibliotekaplus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Filter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.androidnetworking.common.Priority;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class szukaj  extends AppCompatActivity {
    private GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szukaj);
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
        spacecrafts=new JSONDownloader(szukaj.this).retrieve(myListView,myProgressBar);
        Collections.shuffle(spacecrafts);
        adapter=new ListViewAdapterModel(this,spacecrafts);
        myListView.setAdapter(adapter);

    }

    public void openDialog(String ksiazkaId) {
        globalClass.setKsiazkaId(ksiazkaId);
        DialogAdd exampleDialog = new DialogAdd();
        exampleDialog.show(getSupportFragmentManager(),"example");
    }

    /*

    Our HTTP Client
     */
    public class JSONDownloader3 {

        //SAVE/RETRIEVE URLS
        private static final String JSON_DATA_URL="https://firebasestorage.googleapis.com/v0/b/bibliotekaplus.appspot.com/o/book.json?alt=media&token=80d0a924-e54b-43ed-8038-7ca876938953";

        //INSTANCE FIELDS
        private final Context c;
        private Boolean dodano=false;
        public JSONDownloader3(Context c) {
            this.c = c;
        }
        /*
        Fetch JSON Data
         */
        public ArrayList<Spacecraft> retrieve(final ListView mListView, final ProgressBar myProgressBar)
        {
            final ArrayList<Spacecraft> downloadedData=new ArrayList<>();
            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);

            AndroidNetworking.get(JSON_DATA_URL)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            Spacecraft s;
                            try
                            {
                                for(int i=0;i<1000;i++)
                                {
                                    jo=response.getJSONObject(i);
                                    int id=1;
                                    String title=jo.getString("title");
                                    String kind=jo.getString("kind");
                                    String author=jo.getString("author");
                                    String cover="https://wolnelektury.pl/media/"+jo.getString("cover");
                                    String epoch=jo.getString("epoch");
                                    String genre=jo.getString("genre");
                                    Map<String, Object> ksiazka = new HashMap<>();
                                    ksiazka.put("autor", author);
                                    ksiazka.put("dodano", Calendar.getInstance().getTime());
                                    ksiazka.put("epoka", epoch);
                                    ksiazka.put("gatunek", genre);
                                    ksiazka.put("html", "");
                                    Random rnd = new Random();
                                    final int min = 0;
                                    final int max = 30;
                                    int liczba= rnd.nextInt((max-min)+1)+min;
                                    ksiazka.put("kaucja",liczba);
                                    ksiazka.put("okladka", cover);
                                    ksiazka.put("rodzaj",kind);
                                    ksiazka.put("tytul",title);

                                        db.collection("ksiazka")
                                                .add(ksiazka)
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

                                    }




                                myProgressBar.setVisibility(View.GONE);

                            }catch (JSONException e)
                            {
                                myProgressBar.setVisibility(View.GONE);
                                Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            myProgressBar.setVisibility(View.GONE);
                            Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : "+anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            return downloadedData;
        }
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Spacecraft> spacecrafts = new ArrayList<>();
    ListView myListView;
    ListViewAdapterModel adapter;


}
