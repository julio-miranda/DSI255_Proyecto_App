package com.cm2d.hbbc_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    public FirebaseFirestore firebaseFirestore;
    public FirebaseDatabase db;
    public DocumentReference documento;

    public TextView texto;

    public EditText correo;
    public EditText contra;

    public Button iniciar, registrarse;

    public FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=FirebaseDatabase.getInstance();

        texto = findViewById(R.id.textView);
        correo = findViewById(R.id.correoIniciarSesionTxt);
        contra = findViewById(R.id.contraIniciarSesionTxt);
        iniciar = findViewById(R.id.btnIniciarSesion);
        registrarse = findViewById(R.id.btnRegistrarse);

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarUsuario();
            }
        });
//
//        //verificar si hay una sesion activa
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
//        mAuth.getInstance().signOut();
        if(user != null){
//        mAuth.getInstance().signOut();
//            Toast.makeText(this, "entro", Toast.LENGTH_SHORT).show();
            extraerDatosFireStore(user.getEmail().toString());

        }

        //ir a registro
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Registro_usuarios.class);
                startActivity(i);
            }
        });
//

    }

    public void verificarUsuario(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        String Correo=correo.getText().toString();
        String Contrasenia=contra.getText().toString();

        if (!Correo.isEmpty() && !Contrasenia.isEmpty()){
            //iniciando sesion

            auth.signInWithEmailAndPassword(Correo,Contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        extraerDatosFireStore(Correo);


                        Toast.makeText(MainActivity.this, "inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ////////////////////
        }else{
            Toast.makeText(MainActivity.this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void extraerDatosFireStore(String correo) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        if(correo.equals(document.get("Correo").toString())){
                            //trasladandose al inicio
                            Intent intent= new Intent(MainActivity.this,Inicio.class);
                            intent.putExtra("correo", correo);
                            intent.putExtra("nombre", document.get("nombre").toString());
                            startActivity(intent);
                            finish();
                            break;

                        }
                    }
                }
            }
        });
    }
}