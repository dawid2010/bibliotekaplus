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

public class DialogDeleteUser extends AppCompatDialogFragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idZamowienia="";
    private GlobalClass globalClass;
    Context c;

    public DialogDeleteUser(Context context) {
        c = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        globalClass =(GlobalClass) getActivity().getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Usuwanie konta")
                .setMessage("Czy chcesz usunąć konto")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DocumentReference ksiazka = db.document("ksiazka/" + newSp.getId());
                        // DocumentReference uzytkownik = db.document("uzytkownicy/" + globalClass.getUserId());
                        db.collection("uzytkownicy").document(globalClass.getUserId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(getActivity(), "Konto usunięto", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(c, logowanie.class);
                                        //startActivity(intent);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        c.startActivity(intent);

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
