package mobile.bibliotekaplus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;

public class JSONDownloader {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //SAVE/RETRIEVE URLS

    //INSTANCE FIELDS
    private final Context c;

    public JSONDownloader(Context c) {
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

        db.collection("ksiazka")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        JSONObject jo;
                        Spacecraft s;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id= document.getId();
                                String name=document.getString("tytul");
                                String propellant=document.getString("autor");
                                String epoka=document.getString("epoka");
                                String gatunek=document.getString("gatunek");
                                String rodzaj=document.getString("rodzaj");
                                Double kaucja=document.getDouble("kaucja");
                                String techExists="t";
                                String imageURL=document.getString("okladka");

                                s= new Spacecraft();
                                s.setId(id);
                                s.setName(name);
                                s.setPropellant(propellant);
                                s.setEpoka(epoka);
                                s.setGatunek(gatunek);
                                s.setRodzaj(rodzaj);
                                s.setKaucja(kaucja);
                                s.setImageURL(imageURL);
                                s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);

                                downloadedData.add(s);
                            }
                            myProgressBar.setVisibility(View.GONE);

                        }
                    }

                });
        return downloadedData;
    }
}