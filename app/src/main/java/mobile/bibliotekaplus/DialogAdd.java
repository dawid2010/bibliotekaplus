package mobile.bibliotekaplus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DialogAdd extends AppCompatDialogFragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idZamowienia = "";
    private GlobalClass globalClass;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        globalClass = (GlobalClass) getActivity().getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Dodawnie książki do koszyka")
                .setMessage("Czy chcesz dodać książkę do koszyka")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> user = new HashMap<>();

                        DocumentReference ksiazka = db.document("ksiazka/" + globalClass.getKsiazkaId());
                        DocumentReference uzytkownik = db.document("uzytkownicy/" + globalClass.getUserId());
                        Boolean realizacja = false;
                        user.put("ksiazka", ksiazka);
                        user.put("uzytkownik", uzytkownik);
                        user.put("zrealizowany",realizacja);
                        user.put("wiekU",globalClass.getUserWiek());
                        TextView editTextMail;
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
                });
        return builder.create();
    }
}