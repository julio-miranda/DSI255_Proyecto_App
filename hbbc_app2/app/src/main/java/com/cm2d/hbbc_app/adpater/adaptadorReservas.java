package com.cm2d.hbbc_app.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cm2d.hbbc_app.R;
import com.cm2d.hbbc_app.modelo.Reserva;

import java.util.ArrayList;

public class adaptadorReservas extends BaseAdapter {
    public ArrayList<Reserva> dataReserva;
    public Context context;

    public adaptadorReservas(ArrayList<Reserva> dataReserva, Context context) {
        this.dataReserva = dataReserva;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataReserva.size();
    }

    @Override
    public Object getItem(int position) {
        return dataReserva.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Reserva reserva = (Reserva)getItem(position);
        view = LayoutInflater.from(context).inflate(R.layout.item_reserva,null);

        TextView bungalow = view.findViewById(R.id.nombreBungalowTxt);
        TextView fechaInicio = view.findViewById(R.id.fechaInicioTxt);
        TextView fechaFinal = view.findViewById(R.id.fechaFinalTxt);
        TextView precioTotal = view.findViewById(R.id.precioTotalTxt);

        bungalow.setText(reserva.getBungalow());
        fechaInicio.setText(reserva.getFechaInicio());
        fechaFinal.setText(reserva.getFechaInicio());
        precioTotal.setText(reserva.getPrecioTotal());


        return view ;
    }
}
