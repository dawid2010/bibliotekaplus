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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class propozycje extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propozycje);

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
        spacecrafts=new propozycje.JSONDownloader(propozycje.this).retrieve(myListView,myProgressBar);
        adapter=new propozycje.ListViewAdapter(this,spacecrafts);
        myListView.setAdapter(adapter);

    }

    public class Spacecraft {
        /*
        INSTANCE FIELDS
         */
        private int id;
        private String name;
        private String propellant;
        private String imageURL;
        private int technologyExists;
        /*
        GETTERS AND SETTERS
         */
        public int getId() {
            return id;
        }
        public void setId(int id) {
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

    class FilterHelper extends Filter {
        ArrayList<propozycje.Spacecraft> currentList;
        propozycje.ListViewAdapter adapter;
        Context c;

        public FilterHelper(ArrayList<propozycje.Spacecraft> currentList, propozycje.ListViewAdapter adapter, Context c) {
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
                ArrayList<propozycje.Spacecraft> foundFilters=new ArrayList<>();

                propozycje.Spacecraft spacecraft=null;

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
            adapter.setSpacecrafts((ArrayList<propozycje.Spacecraft>) filterResults.values);
            adapter.refresh();
        }
    }

    /*
    Our custom adapter class
     */
    public class ListViewAdapter extends BaseAdapter implements Filterable {

        Context c;
        ArrayList<propozycje.Spacecraft> spacecrafts;
        public ArrayList<propozycje.Spacecraft> currentList;
        propozycje.FilterHelper filterHelper;

        public ListViewAdapter(Context c, ArrayList<propozycje.Spacecraft> spacecrafts) {
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
                view= LayoutInflater.from(c).inflate(R.layout.model,viewGroup,false);
            }

            TextView txtName = view.findViewById(R.id.nameTextView);
            TextView txtPropellant = view.findViewById(R.id.propellantTextView);
            CheckBox chkTechExists = view.findViewById(R.id.myCheckBox);
            ImageView spacecraftImageView = view.findViewById(R.id.spacecraftImageView);

            final propozycje.Spacecraft s= (propozycje.Spacecraft) this.getItem(i);

            txtName.setText(s.getName());
            txtPropellant.setText(s.getPropellant());
            //chkTechExists.setEnabled(true);
            chkTechExists.setChecked( s.getTechnologyExists()==1);
            chkTechExists.setEnabled(false);

            if(s.getImageURL() != null && s.getImageURL().length()>0)
            {
                Picasso.get().load(s.getImageURL()).placeholder(R.drawable.placeholder).into(spacecraftImageView);
            }else {
                Toast.makeText(c, "Empty Image URL", Toast.LENGTH_LONG).show();
                Picasso.get().load(R.drawable.placeholder).into(spacecraftImageView);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, s.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
        public void setSpacecrafts(ArrayList<propozycje.Spacecraft> filteredSpacecrafts)
        {
            this.spacecrafts=filteredSpacecrafts;

        }
        @Override
        public Filter getFilter() {
            if(filterHelper==null)
            {
                filterHelper=new propozycje.FilterHelper(currentList,this,c);
            }

            return filterHelper;
        }
        public void refresh(){
            notifyDataSetChanged();
        }
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        public ArrayList<propozycje.Spacecraft> retrieve(final ListView mListView, final ProgressBar myProgressBar)
        {
            final ArrayList<propozycje.Spacecraft> downloadedData=new ArrayList<>();
            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);

            db.collection("ksiazka")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            JSONObject jo;
                            propozycje.Spacecraft s;
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    int id= 1;
                                    String name=document.getString("tytul");
                                    String propellant="t";
                                    String techExists="t";
                                    String imageURL=document.getString("okladka");

                                    s=new propozycje.Spacecraft();
                                    s.setId(id);
                                    s.setName(name);
                                    s.setPropellant(propellant);
                                    s.setImageURL(imageURL);
                                    s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);

                                    downloadedData.add(s);
                                }
                                myProgressBar.setVisibility(View.GONE);

                            } else {
                                String taskExc = task.getException()+"";
                                Toast.makeText(propozycje.this,taskExc , Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
            return downloadedData;
        }
    }
    ArrayList<propozycje.Spacecraft> spacecrafts = new ArrayList<>();
    ListView myListView;
    propozycje.ListViewAdapter adapter;


}