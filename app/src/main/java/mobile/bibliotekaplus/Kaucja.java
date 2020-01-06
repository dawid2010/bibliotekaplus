package mobile.bibliotekaplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Kaucja extends AppCompatActivity {
    private GlobalClass globalClass;
    String placowka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaucja);
        globalClass =(GlobalClass) getApplicationContext();
        HorizontalStepView setpview5 = (HorizontalStepView) findViewById(R.id.step_view);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("Koszyk",1);
        StepBean stepBean1 = new StepBean("Placówka",1);
        StepBean stepBean2 = new StepBean("Kaucja",0);
        StepBean stepBean3 = new StepBean("Realizacja",-1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);
        setpview5
                .setStepViewTexts(stepsBeanList)//总步骤
                .setTextSize(12)//set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(Kaucja.this, android.R.color.white))//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(Kaucja.this, R.color.uncompleted_text_color))//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(ContextCompat.getColor(Kaucja.this, android.R.color.white))//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(Kaucja.this, R.color.uncompleted_text_color))//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(Kaucja.this, R.drawable.complted))//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(Kaucja.this, R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(Kaucja.this, R.drawable.attention));//设置StepsViewIndicator AttentionIcon

        final ProgressBar myProgressBar= findViewById(R.id.myProgressBar);
        SearchView mySearchView=findViewById(R.id.mySearchView);

        ListView myListView = findViewById(R.id.myListView);
        final ArrayList<Book> books = new JSONDownloader(Kaucja.this).retrieve(myListView,myProgressBar);



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView kaucjeTextView = findViewById(R.id.kaucjeTextView);
                String textToDisplay = "";
                for (Book book : books) {
                    textToDisplay = textToDisplay + "\n" + book.name + " - " + " Kaucja: 50gr za dzień";
                }
                kaucjeTextView.setText(textToDisplay);
                //Toast.makeText(Kaucja.this,"gotowe" , Toast.LENGTH_SHORT).show();
                //Toast.makeText(Kaucja.this,textToDisplay , Toast.LENGTH_SHORT).show();
            }
        }, 2000);


        Intent intent = getIntent();
        placowka = intent.getExtras().getString("placowka");

    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public class Book {
        /*
        INSTANCE FIELDS
         */
        private String id;
        private String name;
        private String propellant;
        private String imageURL;
        private int technologyExists;
        /*
        GETTERS AND SETTERS
         */
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPropellant() {
            return propellant;
        }
        public void setPropellant(String propellant) {
            this.propellant = propellant;
        }
        public String getImageURL() {
            return imageURL;
        }
        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }
        public int getTechnologyExists() {
            return technologyExists;
        }
        public void setTechnologyExists(int technologyExists) {
            this.technologyExists = technologyExists;
        }
        /*
        TOSTRING
         */
        @Override
        public String toString() {
            return name;
        }
    }


    private String name;
    private String propellant;
    private String techExists;
    private String imageURL;
    private Book s;
    public class JSONDownloader {

        //SAVE/RETRIEVE URLS

        //INSTANCE FIELDS
        private final Context c;

        public JSONDownloader(Context c) {
            this.c = c;
        }
        /*
        Fetch JSON Data
         */
        public ArrayList<Book> retrieve(final ListView mListView, final ProgressBar myProgressBar)
        {
            final ArrayList<Book> downloadedData=new ArrayList<>();
            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);

            db.collection("zamowienie")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference uzytkownik = document.getDocumentReference("uzytkownik");
                                    String idUser = uzytkownik.getId();
                                    if (idUser.equals(globalClass.getUserId())){
                                        DocumentReference ksiazka = document.getDocumentReference("ksiazka");
                                        ksiazka.get().
                                                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task3) {
                                                        DocumentSnapshot daneUzytkownika = task3.getResult();
                                                        name = daneUzytkownika.getString("tytul");
                                                        propellant = "t";
                                                        techExists = "t";
                                                        imageURL = daneUzytkownika.getString("okladka");
                                                        s = new Kaucja.Book();
                                                        s.setId(daneUzytkownika.getId());
                                                        s.setName(name);
                                                        s.setPropellant(propellant);
                                                        s.setImageURL(imageURL);
                                                        s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);
                                                        downloadedData.add(s);
                                                        //Toast.makeText(Kaucja.this,name , Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                }
                                myProgressBar.setVisibility(View.GONE);



                            } else {
                                String taskExc = task.getException()+"";
                                Toast.makeText(Kaucja.this,taskExc , Toast.LENGTH_SHORT).show();

                            }
                        }

                    });

            return downloadedData;
        }
    }

    public void OnClickRealizuj(View view) {
        Intent intent = new Intent(this, Realizacja.class);
        intent.putExtra("placowka", placowka);
        this.startActivity ( intent );
    }

}