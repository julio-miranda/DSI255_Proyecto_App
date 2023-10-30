package com.cm2d.hbbc_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro_usuarios extends AppCompatActivity {

    public EditText correo, contra, nombre, dui, telefono;
    public Button btnRegistrarse;
    public FirebaseAuth auth;
    public FirebaseFirestore firebaseFirestore;
    public DocumentReference documento;
    public FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        correo = findViewById(R.id.campoCorreo);
        contra = findViewById(R.id.campoContrasenia);
        nombre = findViewById(R.id.campoNombre);
        dui = findViewById(R.id.campoDui);
        telefono = findViewById(R.id.campoTelefono);
        btnRegistrarse = findViewById(R.id.btnAceptar);
        auth = FirebaseAuth.getInstance();

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    String email = correo.getText().toString();
                    String pass = contra.getText().toString();
                    String name = nombre.getText().toString();
                    String DUI = dui.getText().toString();
                    String Telefono = telefono.getText().toString();
                    if(email.equals("") || pass.equals("") || name.equals("") || DUI.equals("") || Telefono.equals("")){
                        Toast.makeText(Registro_usuarios.this, "Llene todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){


                                // referencia a firestore
//                                documento = firebaseFirestore.collection("usuarios").document(user.getUid());

                                // creando un mapa con los datos a agregar
                                Map<String, Object> datos = new HashMap<>();
                                datos.put("nombre", name);
                                datos.put("Correo", email);
                                datos.put("dui", DUI);
                                datos.put("telefono", Telefono);
                                datos.put("Tipo_de_usuario", "Cliente");

                                // agregando los datos al documento en Firestore
//                                documento.set(datos);
                                db.collection("usuarios").document().set(datos)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(Registro_usuarios.this, "Agregado con exito", Toast.LENGTH_SHORT).show();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Registro_usuarios.this, "Error al agregar usuario", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                finish();

                            }else{
                            }
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(Registro_usuarios.this, "Error "+e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}