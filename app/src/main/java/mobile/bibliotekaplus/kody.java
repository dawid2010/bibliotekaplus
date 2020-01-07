package mobile.bibliotekaplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class kody extends AppCompatActivity {
    private GlobalClass globalClass;
    ArrayList<Zamowienia> spacecrafts = new ArrayList<>();
    ListView myListView;
    ListViewAdapterZamowienia adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kody);
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
        spacecrafts=new FirebaseKodyDonwload(this).retrieve(myListView,myProgressBar);
        Collections.shuffle(spacecrafts);
        adapter=new ListViewAdapterZamowienia(this,spacecrafts);
        myListView.setAdapter(adapter);
    }

    private class ListViewAdapterZamowienia extends BaseAdapter implements Filterable {

        Context c;
        ArrayList<Zamowienia> zamowienias;
        public ArrayList<Zamowienia> currentList;
        FilterHelperZamowienia filterHelper;

        public ListViewAdapterZamowienia(Context c, ArrayList<Zamowienia> zamowienias) {
            this.c = c;
            this.zamowienias = spacecrafts;
            this.currentList=zamowienias;
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
                view= LayoutInflater.from(c).inflate(R.layout.model_zamowienie,viewGroup,false);
            }

            TextView kod = view.findViewById(R.id.textKod);
            TextView data = view.findViewById(R.id.rejdata);
            TextView placowka = view.findViewById(R.id.textPlacowka);

            final Zamowienia s= (Zamowienia) this.getItem(i);

            kod.setText(s.getKod());
            data.setText(s.getData());
            placowka.setText(s.getPlacowka());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, s.getKod(), Toast.LENGTH_SHORT).show();
                    globalClass.setZamowienieId(s.getKod());
                    Intent intent = new Intent(kody.this, kody_szcz.class);
                    startActivity(intent);

                }
            });

            return view;
        }
        public void setSpacecrafts(ArrayList<Zamowienia> filteredSpacecrafts)
        {
            this.zamowienias=filteredSpacecrafts;

        }
        @Override
        public Filter getFilter() {
            if(filterHelper==null)
            {
                filterHelper=new FilterHelperZamowienia(currentList,this,c);
            }

            return filterHelper;
        }
        public void refresh(){
            notifyDataSetChanged();
        }
}

    private class FilterHelperZamowienia extends Filter {
        ArrayList<Zamowienia> currentList;
        ListViewAdapterZamowienia adapter;
        Context c;

        public FilterHelperZamowienia(ArrayList<Zamowienia> currentList, ListViewAdapterZamowienia adapter, Context c) {
            this.currentList = currentList;
            this.adapter = adapter;
            this.c = c;
        }

        /*
        - Perform actual filtering.
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                //CHANGE TO UPPER
                constraint = constraint.toString().toUpperCase();

                //HOLD FILTERS WE FIND
                ArrayList<Zamowienia> foundFilters = new ArrayList<>();

                Zamowienia spacecraft = null;

                //ITERATE CURRENT LIST
                for (int i = 0; i < currentList.size(); i++) {
                    spacecraft = currentList.get(i);

                    //SEARCH
                    if (spacecraft.getData().toUpperCase().contains(constraint)) {
                        //ADD IF FOUND
                        foundFilters.add(spacecraft);
                    }
                }

                //SET RESULTS TO FILTER LIST
                filterResults.count = foundFilters.size();
                filterResults.values = foundFilters;
            } else {
                //NO ITEM FOUND.LIST REMAINS INTACT
                filterResults.count = currentList.size();
                filterResults.values = currentList;
            }

            //RETURN RESULTS
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.setSpacecrafts((ArrayList<Zamowienia>) filterResults.values);
            adapter.refresh();
        }
    }
}
