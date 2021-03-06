package mobile.bibliotekaplus;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FirebaseRankDownload{
    GlobalClass globalClass;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //SAVE/RETRIEVE URLS
    private Spacecraft s;
    //INSTANCE FIELDS
    private final Context c;
    private int i;

    public FirebaseRankDownload(Context c) {
        this.c = c;
        globalClass=(GlobalClass) c.getApplicationContext();
    }
    /*
    Fetch JSON Data
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Spacecraft> retrieve(final ListView mListView, final ProgressBar myProgressBar)
    {
        final ArrayList<Spacecraft> downloadedData=new ArrayList<>();
        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        db.collection("zamowienie")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                i=0;
                                Boolean decyzja = document.getBoolean("zrealizowany");
                                if(decyzja) {
                                    DocumentReference uzytkownik = document.getDocumentReference("uzytkownik");
                                    Date dataZam = document.getDate("data");
                                    try {
                                        if (dataZam.after(globalClass.getRankStart()) && dataZam.before(globalClass.getRankEnd())) {
                                            DocumentReference ksiazka = document.getDocumentReference("ksiazka");
                                            ksiazka.get().
                                                    addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task3) {

                                                            DocumentSnapshot daneUzytkownika = task3.getResult();
                                                            String id = daneUzytkownika.getId();
                                                            if (!findCustomerByid(id, downloadedData)) {
                                                                i = 1;
                                                                String name = daneUzytkownika.getString("tytul");
                                                                String propellant = daneUzytkownika.getString("autor");
                                                                String epoka = daneUzytkownika.getString("epoka");
                                                                String gatunek = daneUzytkownika.getString("gatunek");
                                                                String rodzaj = daneUzytkownika.getString("rodzaj");
                                                                Double kaucja = daneUzytkownika.getDouble("kaucja");
                                                                String techExists = "t";
                                                                String imageURL = daneUzytkownika.getString("okladka");

                                                                s = new Spacecraft();
                                                                s.setId(id);
                                                                s.setName(name);
                                                                s.setPropellant(propellant);
                                                                s.setEpoka(epoka);
                                                                s.setGatunek(gatunek);
                                                                s.setRodzaj(rodzaj);
                                                                s.setKaucja(kaucja);
                                                                s.setImageURL(imageURL);
                                                                s.setLiczba(i);
                                                                s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);
                                                                    downloadedData.add(s);

                                                            } else {
                                                                downloadedData.remove(downloadedData.size()-1);
                                                                i++;
                                                                String name = daneUzytkownika.getString("tytul");
                                                                String propellant = daneUzytkownika.getString("autor");
                                                                String epoka = daneUzytkownika.getString("epoka");
                                                                String gatunek = daneUzytkownika.getString("gatunek");
                                                                String rodzaj = daneUzytkownika.getString("rodzaj");
                                                                Double kaucja = daneUzytkownika.getDouble("kaucja");
                                                                String techExists = "t";
                                                                String imageURL = daneUzytkownika.getString("okladka");

                                                                s = new Spacecraft();
                                                                s.setId(id);
                                                                s.setName(name);
                                                                s.setPropellant(propellant);
                                                                s.setEpoka(epoka);
                                                                s.setGatunek(gatunek);
                                                                s.setRodzaj(rodzaj);
                                                                s.setKaucja(kaucja);
                                                                s.setImageURL(imageURL);
                                                                s.setLiczba(i);
                                                                s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);

                                                                downloadedData.add(s);
                                                            }
                                                        }

                                                    });
                                        }
                                    }
                                    catch (java.lang.NullPointerException e){

                                    }
                                }
                            }
                            myProgressBar.setVisibility(View.GONE);
                        }
                    }

                });
        //Spacecraft compare;
        Collections.sort(downloadedData, new Comparator<Spacecraft>() {
            @Override
            public int compare(Spacecraft o1, Spacecraft o2) {
                if(o1.getLiczba() > o1.getLiczba()){
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return downloadedData;
    }


    class Sortbyroll implements Comparator<Spacecraft>
    {
        // Used for sorting in ascending order of
        // roll number

        @Override
        public int compare(Spacecraft o1, Spacecraft o2) {
            return o1.getLiczba() - o2.getLiczba();
        }
    }

    private Boolean findCustomerByid(String id, ArrayList<Spacecraft> customers ){
        for (Spacecraft customer : customers) {
            if (customer.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}