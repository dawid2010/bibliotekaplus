package mobile.bibliotekaplus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirebaseKoszykDonloader {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    GlobalClass globalClass;
    private String name;
    private String propellant;
    private String techExists;
    private String imageURL;
    private Spacecraft s;
    //SAVE/RETRIEVE URLS

    //INSTANCE FIELDS
    private final Context c;

    public FirebaseKoszykDonloader(Context c) {
        this.c = c;
        globalClass=(GlobalClass) c.getApplicationContext();
    }
    /*
    Fetch JSON Data
     */
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
                                                        String id = daneUzytkownika.getId();
                                                        if(!findCustomerByid(id,downloadedData)) {
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
                                                            s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);

                                                            downloadedData.add(s);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                            myProgressBar.setVisibility(View.GONE);
                        }
                    }

                });
        return downloadedData;
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