package mobile.bibliotekaplus;

import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;

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