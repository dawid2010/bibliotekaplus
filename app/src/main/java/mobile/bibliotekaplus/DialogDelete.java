package mobile.bibliotekaplus;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DialogDelete extends AppCompatDialogFragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idZamowienia="";
    private GlobalClass globalClass;
    Context c;

    public DialogDelete(Context context) {
        c = context;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        globalClass =(GlobalClass) getActivity().getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Usuwanie książki z koszyka")
                .setMessage("Czy chcesz usunąć książkę z koszyka")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // DocumentReference ksiazka = db.document("ksiazka/" + newSp.getId());
                       // DocumentReference uzytkownik = db.document("uzytkownicy/" + globalClass.getUserId());

                        db.collection("zamowienie")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                DocumentReference ksiazka=document.getDocumentReference("ksiazka");
                                                DocumentReference user=document.getDocumentReference("uzytkownik");
                                                String  ksiazkaID= ksiazka.getId();
                                                String userID = user.getId();
                                                if(ksiazkaID.equals(globalClass.getKsiazkaId())) {
                                                    if (userID.equals(globalClass.getUserId())) {
                                                        idZamowienia = document.getId();
                                                        db.collection("zamowienie").document(idZamowienia)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            }

                                            Intent intent = new Intent(c, koszyk.class);
                                            //startActivity(intent);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            c.startActivity(intent);
                                        } else {
                                            String taskExc = task.getException()+"";
                                        }
                                    }

                                });


                    }
                });

        return builder.create();
    }


}
