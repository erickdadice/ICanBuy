package com.icb.icanbuy.ui.perfil;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.icb.icanbuy.MainActivity;
import com.icb.icanbuy.R;
import com.icb.icanbuy.models.Usuario.Usuario;

import java.util.Map;


public class PerfilFragment extends Fragment {
private TextView tv_name, tv_fecha,tv_Correo, tv_telefono, tv_tipoID,
        tv_Cedula, tv_Compras, tv_Cupones, titulo_telefono, titulo_tipo, titulo_ced;
private ImageView iv_fotoperfil;
private FirebaseDatabase database;
private DatabaseReference reference;
private static final String USERS="users";
   private String email;
   private final String TAG=this.getClass().getName().toUpperCase();
    private Map<String, String> userMap;
    private FirebaseUser user;
    private String userID;
    private Button btnEditarPerfil, btn_BorrarPerfil;
    private static final int  PHOTO_PERFIL = 1;

    private String nombreString, apellidoString, correoString, fechaString,
    cedulaString, tipoIDString, telefonoString;

    private FirebaseAuth mAuth;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_perfil, container, false);

       user= FirebaseAuth.getInstance().getCurrentUser();
       reference=FirebaseDatabase.getInstance().getReference("Users");
       userID=user.getUid();

       mAuth = FirebaseAuth.getInstance();
       storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pic");



       // Log.v("USERID", userRef.getKey());

        tv_name=root.findViewById(R.id.tv_name);
        tv_fecha=root.findViewById(R.id.tv_fecha);
        tv_Correo=root.findViewById(R.id.tv_Correo);
        tv_telefono=root.findViewById(R.id.tv_telefono);
        tv_tipoID=root.findViewById(R.id.tv_tipoID);
        tv_Cedula=root.findViewById(R.id.tv_cedula);
        iv_fotoperfil=root.findViewById(R.id.iv_fotoperfil);
        //tv_Compras=root.findViewById(R.id.tv_compras);
       // tv_Cupones=root.findViewById(R.id.tv_cupones);
        btnEditarPerfil=root.findViewById(R.id.btn_EditaPerfil);
        btn_BorrarPerfil=root.findViewById(R.id.btn_BorrarPerfil);

        titulo_telefono=root.findViewById(R.id.titulo_telefono);
        titulo_tipo=root.findViewById(R.id.titulo_tipo);
        titulo_ced=root.findViewById(R.id.titulo_ced);

        iv_fotoperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i, "Selecciona una foto"),PHOTO_PERFIL);
            }
        });

        if(user!=null && reference!=null && userID!=null){
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            Usuario userProfile = snapshot.getValue(Usuario.class);

                            if (userProfile != null) {
                                String fullname = (userProfile.getNombre()).concat(" ".concat(userProfile.getApellido()));
                                String correo = userProfile.getCorreo();
                                String fechaNac = userProfile.getFechaNac();

                                if(userProfile.getCedula()!=null){
                                    String cedula = userProfile.getCedula();
                                    tv_Cedula.setText(cedula);
                                    cedulaString=userProfile.getCedula();

                                }
                                if(userProfile.getTelefono()!=null){
                                    String telefono = userProfile.getTelefono();
                                    tv_telefono.setText(telefono);
                                    telefonoString=userProfile.getTelefono();
                                }
                                if(userProfile.getTipoID()!=null){
                                    String tipoID = userProfile.getTipoID();
                                    tv_tipoID.setText(tipoID);
                                    tipoIDString=userProfile.getTipoID();
                                }

                                tv_name.setText(fullname);
                                tv_fecha.setText(fechaNac);
                                tv_Correo.setText(correo);

                                nombreString=userProfile.getNombre();
                                apellidoString=userProfile.getApellido();
                                correoString=userProfile.getCorreo();
                                fechaString=userProfile.getFechaNac();



                            }
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "??Error!", Toast.LENGTH_LONG).show();
                }
            });




            btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditarPerfil editarPerfil = new EditarPerfil();

                    Bundle bundle = new Bundle();
                    bundle.putString("nombre", nombreString);
                    bundle.putString("apellido", apellidoString);
                    bundle.putString("correo", correoString);
                    bundle.putString("fechaNac", fechaString);
                    bundle.putString("telefono", telefonoString);
                    bundle.putString("tipoID", tipoIDString);
                    bundle.putString("cedula", cedulaString);

                    editarPerfil.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentPerfil, editarPerfil);

                    transaction.commit();
                }
            });//editar perfil

        }//verificar que ingres?? con Firebase
        else{
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                tv_name.setText(personName);
                tv_Correo.setText(personEmail);
                tv_fecha.setText(" ");
                tv_telefono.setText(" ");
                tv_Cedula.setText(" ");
                tv_tipoID.setText(" ");
                titulo_telefono.setText(" ");
                titulo_tipo.setText(" ");
                titulo_ced.setText(" ");

            }else{
                tv_name.setText(" ");
                tv_Correo.setText(" ");
                tv_fecha.setText(" ");
                tv_telefono.setText(" ");
                tv_Cedula.setText(" ");
                tv_tipoID.setText(" ");
                titulo_telefono.setText(" ");
                titulo_tipo.setText(" ");
                titulo_ced.setText(" ");
            }
        }



        btn_BorrarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.bt_eliminar)
                        .setMessage(R.string.validacion_eliminacion_cuenta)
                        .setPositiveButton(
                                "S??",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Query profileQuery = reference.child(userID).orderByChild("correo").equalTo(correoString);

                                                            profileQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for(DataSnapshot perfilSnapshot: snapshot.getChildren()){
                                                                        perfilSnapshot.getRef().removeValue();
                                                                        Toast.makeText(getContext(),"Usuario borrado con ??xito", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Log.e(TAG, "Cancelado", error.toException());
                                                                    //Toast.makeText(getContext(),"Error al borrar datos de usuario", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            Toast.makeText(getContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(getContext(), MainActivity.class);
                                                            startActivity(i);


                                                            Log.d(TAG, "Cuenta borrada");
                                                        }else{
                                                            Toast.makeText(getContext(),"Error al borrar usuario", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });



        return root;
    }




}