package mobile.bibliotekaplus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Filter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class menu_glowne extends AppCompatActivity {

    private static final String TAG = "menu_glowne";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ArrayList<Spacecraft> al;
    ListViewAdapter arrayAdapter;
    private ArrayAdapter<URL> arrayAdapterImage;
    private int i;
    private ProgressBar myProgressBar;
    ListView myListView;
    private GlobalClass globalClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_glowne);
        globalClass =(GlobalClass) getApplicationContext();
        myProgressBar= findViewById(R.id.myProgressBarGlowna);
        spacecrafts=new JSONDownloader(menu_glowne.this).retrieve(myListView,myProgressBar);

        if (isServicesOK()) {
            init();
            initSzukaj();
            initPropozycje();
            initKoszyk();
            initNowosci();
            initUstawienia();
            initKody();
            al = new ArrayList<>();
            Spacecraft sp = new Spacecraft();
            sp.setName("Przesuwaj w prawo, aby dodawać do koszyka");
            al.add(sp);
            arrayAdapter=new ListViewAdapter(this,al);

            SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
            flingContainer.setAdapter(arrayAdapter);

            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    Log.d("LIST", "removed object!");
                    al.remove(0);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    Spacecraft newSp = (Spacecraft) dataObject;
                    if(newSp.getName().equals("Przesuwaj w prawo, aby dodawać do koszyka")){
                        makeToast(menu_glowne.this, "Tym sposobem odrzuczasz książki!");
                    }
                    else {
                        makeToast(menu_glowne.this, "Odrzuciłeś książkę!");
                    }

                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    Spacecraft newSp = (Spacecraft) dataObject;
                    if(newSp.getId().equals("Przesuwaj w prawo, aby dodawać do koszyka")){
                        makeToast(menu_glowne.this, "Tym sposobem dodajesz książki do koszyka!");
                    }
                    else {
                        makeToast(menu_glowne.this, "Dodałeś ksiązkę do koszyka!");
                        Map<String, Object> user = new HashMap<>();
                        DocumentReference ksiazka = db.document("ksiazka/" + newSp.getId());
                        DocumentReference uzytkownik = db.document("uzytkownicy/" + globalClass.getUserId());
                        Boolean realizacja = false;
                        user.put("ksiazka", ksiazka);
                        user.put("uzytkownik", uzytkownik);
                        user.put("zrealizowany",realizacja);
                        user.put("wiekU",globalClass.getUserWiek());
                        // Add a nnew document with a generated ID
                        TextView editTextMail;
                        editTextMail = (TextView) findViewById(R.id.profile_email);
                        db.collection("zamowienie")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }

                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // Ask for more data here
                    if(spacecrafts.size()>0){
                        Random rnd = new Random();
                        final int min = 0;
                        final int max = spacecrafts.size()-1;
                        int liczba= rnd.nextInt((max-min)+1)+min;
                        al.add(spacecrafts.get(liczba));

                    }
                    arrayAdapter.notifyDataSetChanged();
                    i++;
                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                }
            });

            // Optionally add an OnItemClickListener
            flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    makeToast(menu_glowne.this, "Clicked!");
                }
            });
        }
    }

    static void makeToast(Context ctx, String s){
            Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
        }

    private void initSzukaj() {
        Button btnMap = (Button) findViewById(R.id.btnszukaj);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(menu_glowne.this, szukaj.class);
                startActivity(intent);
            }
        });
    }

    private void initPropozycje() {
        Button btnMap = (Button) findViewById(R.id.btnpropozycje);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(menu_glowne.this, propozycje.class);
                startActivity(intent);
            }
        });
    }

    private void initNowosci() {
        Button btnMap = (Button) findViewById(R.id.btnnowosci);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(menu_glowne.this, nowosci.class);
                startActivity(intent);
            }
        });
    }

    private void initUstawienia() {
        Button btnMap = (Button) findViewById(R.id.btnustawienia);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(menu_glowne.this, ustawienia.class);
                startActivity(intent);
            }
        });
    }

    private void initKody() {
        Button btnMap = (Button) findViewById(R.id.btnkody);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(menu_glowne.this, kody.class);
                startActivity(intent);
            }
        });
    }

    private void initKoszyk() {
        Button btnMap = (Button) findViewById(R.id.btnkoszyk);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(menu_glowne.this, koszyk.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(menu_glowne.this, MapActivity.class);
                startActivity(intent);

            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(menu_glowne.this);
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google Play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK: An error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(menu_glowne.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"We cant make map request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    ArrayList<Spacecraft> spacecrafts = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public class ListViewAdapter extends BaseAdapter implements Filterable {

        Context c;
        ArrayList<Spacecraft> spacecrafts;
        public ArrayList<Spacecraft> currentList;
        FilterHelper filterHelper;

        public ListViewAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
            this.c = c;
            this.spacecrafts = spacecrafts;
            this.currentList=spacecrafts;
        }
        @Override
        public int getCount() {
            return spacecrafts.size();
        }
        @Override
        public Object getItem(int i) {
            return spacecrafts.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null)
            {
                view= LayoutInflater.from(c).inflate(R.layout.item,viewGroup,false);
            }

            TextView txtName = view.findViewById(R.id.nameTextView);
            TextView txtPropellant = view.findViewById(R.id.propellantTextView);
            TextView txtPropellant2 = view.findViewById(R.id.propellantTextView2);
            TextView txtPropellant3 = view.findViewById(R.id.propellantTextView3);
            CheckBox chkTechExists = view.findViewById(R.id.myCheckBox);
            ImageView spacecraftImageView = view.findViewById(R.id.spacecraftImageView);

            final Spacecraft s= (Spacecraft) this.getItem(i);

            txtName.setText(s.getName());
            txtPropellant.setText(s.getPropellant());
            if(s.getGatunek()==null){
                txtPropellant2.setText("");
            }
            else{
                txtPropellant2.setText("S: "+s.getRodzaj()+" "+s.getGatunek());
            }
            if(s.getEpoka()==null) {
                txtPropellant3.setText("");
            }
            else{
                txtPropellant3.setText("Epoka: "+s.getEpoka());
            }


            //chkTechExists.setEnabled(true);

            if(s.getEpoka()==null){
                chkTechExists.setWidth(0);
                chkTechExists.setHeight(0);
            }
            else {
                if (s.getKaucja() != null) {
                    if (s.getKaucja() > 0.00) {
                        chkTechExists.setChecked(s.getKaucja() > 0.00d);
                        chkTechExists.setText("Kaucja:" + s.getKaucja() + " zł");
                    }
                }
            }
                        chkTechExists.setEnabled(false);

            if(s.getImageURL() != null && s.getImageURL().length()>0)
            {
                Picasso.get().load(s.getImageURL()).placeholder(R.drawable.placeholder).into(spacecraftImageView);
            }else {

                Picasso.get().load(R.drawable.logo).into(spacecraftImageView);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, s.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
        public void setSpacecrafts(ArrayList<Spacecraft> filteredSpacecrafts)
        {
            this.spacecrafts=filteredSpacecrafts;

        }
        @Override
        public Filter getFilter() {
            if(filterHelper==null)
            {
                filterHelper=new FilterHelper(currentList,this,c);
            }

            return filterHelper;
        }
        public void refresh(){
            notifyDataSetChanged();
        }
    }

    class FilterHelper extends Filter {
        ArrayList<Spacecraft> currentList;
        ListViewAdapter adapter;
        Context c;

        public FilterHelper(ArrayList<Spacecraft> currentList, ListViewAdapter adapter, Context c) {
            this.currentList = currentList;
            this.adapter = adapter;
            this.c=c;
        }
        /*
        - Perform actual filtering.
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CHANGE TO UPPER
                constraint=constraint.toString().toUpperCase();

                //HOLD FILTERS WE FIND
                ArrayList<Spacecraft> foundFilters=new ArrayList<>();

                Spacecraft spacecraft=null;

                //ITERATE CURRENT LIST
                for (int i=0;i<currentList.size();i++)
                {
                    spacecraft= currentList.get(i);

                    //SEARCH
                    if(spacecraft.getName().toUpperCase().contains(constraint) )
                    {
                        //ADD IF FOUND
                        foundFilters.add(spacecraft);
                    }
                }

                //SET RESULTS TO FILTER LIST
                filterResults.count=foundFilters.size();
                filterResults.values=foundFilters;
            }else
            {
                //NO ITEM FOUND.LIST REMAINS INTACT
                filterResults.count=currentList.size();
                filterResults.values=currentList;
            }

            //RETURN RESULTS
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.setSpacecrafts((ArrayList<Spacecraft>) filterResults.values);
            adapter.refresh();
        }
    }
}
