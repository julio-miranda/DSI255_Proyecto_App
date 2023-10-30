package com.cm2d.hbbc_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cm2d.hbbc_app.modelo.Bungalows;
import com.cm2d.hbbc_app.modelo.Reserva;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Inicio extends AppCompatActivity {

    public Spinner spOpciones;

    public EditText datePickerIn;
    public EditText datePickerOut;

    public DatePicker datePickerEntrada;
    public DatePicker datePickerSalida;

    public int diaEntrada;
    public int mesEntrada;
    public int anioEntrada;
    public FirebaseFirestore firebaseFirestore;

    public ArrayList<String> opciones;


    public ArrayList<Bungalows> listaBungalows;

    public ArrayList<Bungalows> listaBungalowsDisponibles;
    public TextView detallesReserva;
    public Button btnAceptar;

    public int posicionBungalowLista = 0;
    double totalPrecioReserva;
    public String nombreCliente;
    public String correoCliente;

    public Button btnVerReservas;
    public FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        spOpciones = findViewById(R.id.spOpciones);
        datePickerIn = findViewById(R.id.dateIn);
        datePickerOut = findViewById(R.id.dateOut);
        listaBungalowsDisponibles = new ArrayList<>();

        detallesReserva = findViewById(R.id.txtViewDetallesReserva);
        btnAceptar = findViewById(R.id.btnAceptar);


        //obtener extra
        nombreCliente = getIntent().getStringExtra("nombre");
        correoCliente = getIntent().getStringExtra("correo");
        btnVerReservas = findViewById(R.id.btnVerReservas);

        //Toast.makeText(this, "nombre cliente: "+nombreCliente, Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();



        datePickerIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialogEntrada();
                datePickerOut.setText("");
                opciones = new ArrayList<>();
                opciones.add("-");
                //spinner
                ArrayAdapter<String> Adapter = new ArrayAdapter<>(Inicio.this, android.R.layout.simple_spinner_item, opciones);
                Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spOpciones.setAdapter(Adapter);
                //fin llenar spinner

            }
        });

        datePickerOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verificar que se haya seleccionado una entrada primero
                if(datePickerIn.getText().toString().equals("")){
                    Toast.makeText(Inicio.this, "Seleccione fecha de ingreso primero", Toast.LENGTH_SHORT).show();
                }else{
                    showDatePickerDialogSalida();

                }
            }
        });


        opciones = new ArrayList<>();
        opciones.add("Seleccione una fecha primero");
        //spinner
        ArrayAdapter<String> Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spOpciones.setAdapter(Adapter);
        //fin llenar spinner



        spOpciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!listaBungalowsDisponibles.isEmpty()){
                    if (position == 0) {
                        Toast.makeText(Inicio.this, "Es cero", Toast.LENGTH_SHORT).show();
                    }else{
                        Bungalows b = new Bungalows();
                        posicionBungalowLista = position;
                        b = listaBungalowsDisponibles.get(position - 1);

                        //Calcular cantidad dias de la reserva
                        int cantidadDias = cantidadDiasReserva(StringToDate(datePickerIn.getText().toString()), StringToDate(datePickerOut.getText().toString()));


                        //Calcular precio de la reserva
                        Toast.makeText(Inicio.this, "cantidad dias "+cantidadDias, Toast.LENGTH_SHORT).show();
                        totalPrecioReserva = cantidadDias * Double.parseDouble(b.getPrecio());
                        detallesReserva.setText("Bungalow: "+b.getId()+"\nDias a reservar: "+cantidadDias+"\nPrecio total: "+totalPrecioReserva);

                    }
                }else{
//                    Toast.makeText(Inicio.this, "Esta vacia", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarReserva();
            }
        });

        btnVerReservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Inicio.this, verReservas.class);
                i.putExtra("nombre", nombreCliente);
                startActivity(i);
            }
        });

    }

    public void guardarReserva(){

        try {
            if(posicionBungalowLista == 0){
                Toast.makeText(this, "Seleccione un bungalo primero", Toast.LENGTH_SHORT).show();
            }else{
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Bungalows b = listaBungalowsDisponibles.get(posicionBungalowLista -1);
                Map<String, Object> data = new HashMap<>();
                data.put("Bungalow","Codigo: "+ b.getId()+", Precio: $"+b.getPrecio());
                data.put("Disponibilidad", "Ocupado");
                data.put("Total", totalPrecioReserva);
                data.put("fechafinal", datePickerOut.getText().toString());
                data.put("fechainicio", datePickerIn.getText().toString());
                data.put("menu", "");
                data.put("nombre_Cliente", nombreCliente);
                data.put("utileria", "");
                db.collection("reservas").document().set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Inicio.this, "Agregado con exito "+nombreCliente, Toast.LENGTH_SHORT).show();
                                limpiarReserva();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Inicio.this, "Ocurrio un problema", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        }catch (Exception e){
            Toast.makeText(this, "error "+e, Toast.LENGTH_SHORT).show();
        }


    }

    public void limpiarReserva(){
        //limpia fechas
        datePickerIn.setText("");
        datePickerOut.setText("");

//        limpia spinner
        opciones = new ArrayList<>();
        opciones.add("Seleccione una feccha primero");
        //spinner
        ArrayAdapter<String> Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spOpciones.setAdapter(Adapter);
        //fin llenar spinner

        detallesReserva.setText("");

    }

    //funcion selector de fecha
    public void showDatePickerDialogEntrada(){
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(calendar.YEAR);
        int currentMonth = calendar.get(calendar.MONTH);
        int currentDay = calendar.get(calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialogEntrada = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar1 = Calendar.getInstance();

                diaEntrada = dayOfMonth;
                mesEntrada = month;
                anioEntrada = year;
                String seleteDate = dayOfMonth + "/" +(month + 1) + "/" + year;
                datePickerIn.setText(seleteDate);
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialogEntrada.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialogEntrada.show();

    }

    public void showDatePickerDialogSalida(){

        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(calendar.YEAR);
        int currentMonth = calendar.get(calendar.MONTH);
        int currentDay = calendar.get(calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialogSalida = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String seleteDate = dayOfMonth + "/" +(month + 1) + "/" + year;
                datePickerOut.setText(seleteDate);
                verificarDisponibilidadBungalows();


            }
        }, currentYear, currentMonth, currentDay);
        calendar.set(anioEntrada, mesEntrada, diaEntrada);
        datePickerDialogSalida.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialogSalida.show();



    }

    public void verificarDisponibilidadBungalows(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        ArrayList<Bungalows> listaBungalowsDisponibles = new ArrayList<>();
        ArrayList<Reserva> listaReservas = new ArrayList<>();
        listaBungalows = new ArrayList<>();
        String fechaEntrada, fechaSalida;
        fechaEntrada = datePickerIn.getText().toString();
        fechaSalida = datePickerOut.getText().toString();

//        Toast.makeText(Inicio.this, "ejecutandose desde llenado de spinner"+fechaEntrada+fechaSalida, Toast.LENGTH_SHORT).show();

        String[] datos = new String[4];

        firebaseFirestore.collection("bungalows").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Bungalows b = new Bungalows();
                        b.setId(document.getId());
                        b.setCantidad(document.get("Cantidad").toString());
                        b.setHabitaciones(document.get("Habitaciones").toString());
                        b.setPrecio(document.get("Precio").toString());
                        listaBungalows.add(b);


                    }

                    firebaseFirestore.collection("reservas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
//                                    Toast.makeText(Inicio.this, "fecha inicial: "+document.get("fechainicio"), Toast.LENGTH_SHORT).show();
                                    Reserva r = new Reserva();
                                    r.setBungalow(document.get("Bungalow").toString());
                                    r.setFechaFinal(document.get("fechafinal").toString());
                                    r.setFechaInicio(document.get("fechainicio").toString());
                                    listaReservas.add(r);




                                }

                                llenarSpinerBungalows( listaBungalows,listaReservas);


                            }
                        }
                    });




                }
            }
        });



    }

    public void llenarSpinerBungalows( ArrayList<Bungalows> listaBungalows,ArrayList<Reserva> listaReservas ){
//        Toast.makeText(this, "bungalo: "+listaBungalows.get(0).getId(), Toast.LENGTH_SHORT).show();
        String fechaInicioUsuario = datePickerIn.getText().toString();
        String fechaFinalUsuario = datePickerOut.getText().toString();
        AtomicBoolean bungaloDisponible = new AtomicBoolean(true);

        listaBungalowsDisponibles = new ArrayList<>();
        ArrayList<String> bungalosSpinner = new ArrayList<>();
        bungalosSpinner.add("Seleccionar bungalow");

        listaBungalows.forEach((bungalows -> {
            listaReservas.forEach(reserva -> {
                String fechainicioFireBase = reserva.getFechaInicio();
                String fechaFinalFireBase = reserva.getFechaFinal();
                if(reserva.getBungalow().toString().contains(bungalows.getId())){
//                Toast.makeText(this, "bungalow "+reserva.getBungalow(), Toast.LENGTH_LONG).show();

//                        Toast.makeText(this, "fecha firebase "+fechaFinalFireBase, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(this, "fecha final fb "+fechainicioFireBase, Toast.LENGTH_SHORT).show();
                    if(comprobarFechaRango(StringToDate(fechaInicioUsuario),StringToDate(fechaFinalUsuario), StringToDate(fechainicioFireBase), StringToDate(fechaFinalFireBase))){
                        bungaloDisponible.set(false);
                    }
                 //   Toast.makeText(this, "respuesta rango: "+comprobarFechaRango(StringToDate(fechaInicioUsuario),StringToDate(fechaFinalUsuario), StringToDate(fechainicioFireBase), StringToDate(fechaFinalFireBase)), Toast.LENGTH_SHORT).show();
                }
            });
            if(bungaloDisponible.get()){
                listaBungalowsDisponibles.add(bungalows);
                bungalosSpinner.add("Bungalo: "+bungalows.getId()+" Cantidad: "+ bungalows.getCantidad());
            }
            bungaloDisponible.set(true);
        }));







        //spinner
        ArrayAdapter<String> Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bungalosSpinner);

        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spOpciones.setAdapter(Adapter);
        //fin llenar spinner
    }



    public Date StringToDate(String date){
//        SimpleDateFormat formato = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha = new Date();
        try {
            // Convierte la cadena en un objeto Date
            fecha = formato.parse(date);

            return fecha;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fecha;
    }

    public boolean comprobarFechaRango(Date fechaUsuarioInicio, Date fechaUsuarioFin, Date fechaFireBaseInicio, Date fechaFireBaseFin){
        boolean estaEnRango = false;
        for (Date fecha = fechaUsuarioInicio; fecha.before(fechaUsuarioFin); fecha.setDate(fecha.getDate() + 1)) {
//            Toast.makeText(this, "fecha usuario: "+fecha, Toast.LENGTH_LONG).show();
//            Toast.makeText(this, "fecha firebase: "+fechaFireBaseFin, Toast.LENGTH_LONG).show();
            if (!fecha.before(fechaFireBaseInicio) && !fecha.after(fechaFireBaseFin)) {
                estaEnRango = true;
            }
        }
        return estaEnRango;
    }

    public int cantidadDiasReserva(Date fechaInicio, Date fechaFin){
        int Cantidad = 1;
        for(Date fecha = fechaInicio; fecha.before(fechaFin); fecha.setDate(fecha.getDate() + 1)){
            Cantidad ++;
        }
        return Cantidad;
    }


}