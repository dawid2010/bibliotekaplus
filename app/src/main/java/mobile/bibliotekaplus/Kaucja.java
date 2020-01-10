package mobile.bibliotekaplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Kaucja extends AppCompatActivity {
    private GlobalClass globalClass;
    String placowka;
    String selectedMarkerUlica;

    private String paymentAmount;

    public static final int PAYPAL_REQUEST_CODE = 123;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    ArrayList<Book> books;

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
        books = new JSONDownloader(Kaucja.this).retrieve(myListView,myProgressBar);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView kaucjeTextView = findViewById(R.id.kaucjeTextView);
                kaucjeTextView.setMovementMethod(new ScrollingMovementMethod());
                String textToDisplay = "";
                for (Book book : books) {
                    textToDisplay = textToDisplay + "\n" + book.name + " - " + "\t Kaucja: " + String.valueOf(book.getKaucja()) + " zł" ;
                }
                kaucjeTextView.setText(textToDisplay);
                //Toast.makeText(Kaucja.this,"gotowe" , Toast.LENGTH_SHORT).show();
                //Toast.makeText(Kaucja.this,textToDisplay , Toast.LENGTH_SHORT).show();
            }
        }, 2000);


        Intent intentFromPreviousStep = getIntent();
        placowka = intentFromPreviousStep.getExtras().getString("placowka");
        selectedMarkerUlica = intentFromPreviousStep.getExtras().getString("adres");


        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
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
        private double kaucja;
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
        public double getKaucja() {
            return kaucja;
        }
        public void setKaucja(double kaucja) {
            this.kaucja = kaucja;
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
    private double kaucja;
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
                                    Boolean decyzja = document.getBoolean("zrealizowany");
                                    if(!decyzja) {
                                        DocumentReference uzytkownik = document.getDocumentReference("uzytkownik");
                                        String idUser = uzytkownik.getId();
                                        if (idUser.equals(globalClass.getUserId())) {
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
                                                        kaucja = daneUzytkownika.getDouble("kaucja");
                                                        s = new Kaucja.Book();
                                                        s.setId(daneUzytkownika.getId());
                                                        s.setName(name);
                                                        s.setPropellant(propellant);
                                                        s.setImageURL(imageURL);
                                                        s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);
                                                        s.setKaucja(kaucja);
                                                        if(!findCustomerByid(s.getId(),downloadedData)) {
                                                            downloadedData.add(s);
                                                        }
                                                        //Toast.makeText(Kaucja.this,name , Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
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

    private Boolean findCustomerByid(String id, ArrayList<Book> customers ){
        for (Book customer : customers) {
            if (customer.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void OnClickRealizuj(View view) {
        globalClass.setPlatnosc(false);
        Intent intent = new Intent(this, Realizacja.class);
        intent.putExtra("placowka", placowka);
        intent.putExtra("adres", selectedMarkerUlica);
        intent.putExtra("oplacono", false);
        this.startActivity ( intent );
    }
    public void OnClickPaypal(View view) {
        globalClass.setPlatnosc(true);
        paymentAmount = "10";
        double tempIntAmount = 0;
        for (Book book : books) {
            tempIntAmount+=book.getKaucja();
            Log.d("platnosci","kwota:"+book.getKaucja());
        }
        paymentAmount = String.valueOf(tempIntAmount);
        Log.d("platnosciFinal","kwota:"+paymentAmount);
        getPayment();
    }
    private void getPayment() {
        //Getting the amount from editText

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "Simplified Coding Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, Realizacja.class)
                                //.putExtra("PaymentDetails", paymentDetails)
                                //.putExtra("PaymentAmount", paymentAmount)
                                .putExtra("oplacono", true)
                                .putExtra("placowka", placowka)
                                .putExtra("adres", selectedMarkerUlica));

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }


}