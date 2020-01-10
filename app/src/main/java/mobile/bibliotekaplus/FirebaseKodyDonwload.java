package mobile.bibliotekaplus;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FirebaseKodyDonwload {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    GlobalClass globalClass;
    private String name;
    private String propellant;
    private String techExists;
    private String imageURL;
    private Zamowienia s;
    //SAVE/RETRIEVE URLS

    //INSTANCE FIELDS
    private final Context c;

    public FirebaseKodyDonwload(Context c) {
        this.c = c;
        globalClass=(GlobalClass) c.getApplicationContext();
    }
    /*
    Fetch JSON Data
     */
    public ArrayList<Zamowienia> retrieve(final ListView mListView, final ProgressBar myProgressBar)
    {
        final ArrayList<Zamowienia> downloadedData=new ArrayList<>();
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
                                if(decyzja) {
                                    DocumentReference uzytkownik = document.getDocumentReference("uzytkownik");
                                    String idUser = uzytkownik.getId();
                                    if (idUser.equals(globalClass.getUserId())) {
                                        String kod = "";
                                        String data = "";
                                        String placowka = "";
                                        Boolean platnosc = false;
                                                            String id = document.getId();
                                                            try {
                                                                 kod = document.getString("kod");
                                                                 Date cal = document.getDate("data");
                                                                  platnosc = document.getBoolean("platnosc");
                                                                Calendar c = Calendar.getInstance();
                                                                c.setTime(cal);
                                                                  data = c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
                                                                 placowka = "Placówka nr: "+document.getDocumentReference("placowka").getId()+"";
                                                            }
                                                            catch (java.lang.NullPointerException e){

                                                            }

                                                            s = new Zamowienia();
                                                            s.setId(id);
                                                            s.setKod(kod);
                                                            s.setData(data);
                                                            s.setPlacowka(placowka);
                                                            s.setPlatnosc(platnosc);
                                        if (!findCustomerByid(kod, downloadedData)) {
                                            downloadedData.add(s);
                                        }

                                    }
                                }
                            }
                            myProgressBar.setVisibility(View.GONE);
                        }
                    }

                });
        return downloadedData;
    }

    private Boolean findCustomerByid(String id, ArrayList<Zamowienia> customers ){
        for (Zamowienia customer : customers) {
            if (customer.getKod().equals(id)) {
                return true;
            }
        }
        return false;
    }
}