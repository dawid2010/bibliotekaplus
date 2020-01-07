package mobile.bibliotekaplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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