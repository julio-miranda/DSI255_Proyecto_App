package com.cm2d.hbbc_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cm2d.hbbc_app.adpater.adaptadorReservas;
import com.cm2d.hbbc_app.modelo.Reserva;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class verReservas extends AppCompatActivity {
    public FirebaseAuth auth;
    public String nombreCliente;
    public ListView listaReservasLV;
    public ArrayList<Reserva> ListaReservas;
    public adaptadorReservas adapter;
    public FirebaseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reservas);
        //verificar si hay una sesion activa
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Toast.makeText(this, "email usuario: "+user.getEmail(), Toast.LENGTH_SHORT).show();

        nombreCliente = getIntent().getStringExtra("nombre");
        Toast.makeText(this, "nombre cliente "+nombreCliente, Toast.LENGTH_SHORT).show();

        listaReservasLV = findViewById(R.id.listaReservaListView);

        ListaReservas = new ArrayList<>();
        cargarReservas();



    }

    public void cargarReservas(){
        ListaReservas = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("reservas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        if(nombreCliente.equals(document.get("nombre_Cliente").toString())){
                        Toast.makeText(verReservas.this, " cccccc: "+nombreCliente, Toast.LENGTH_SHORT).show();
                            Reserva r = new Reserva();
                            r.setBungalow("Bungalow: "+document.get("Bungalow").toString());
                            r.setFechaInicio("Fecha entrada: "+document.get("fechainicio").toString());
                            r.setFechaFinal("Fecha salida: "+document.get("fechafinal").toString());
                            r.setPrecioTotal("Precio total: "+document.get("Total").toString());
                            ListaReservas.add(r);

                        }
                    }
                    adapter = new adaptadorReservas(ListaReservas,verReservas.this);
                    listaReservasLV.setAdapter(adapter);
                }
            }
        });




    }

    public void cerrarSesion(){

        auth = FirebaseAuth.getInstance();

//        auth.getInstance().signOut();

    }
}