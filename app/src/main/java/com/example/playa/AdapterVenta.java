package com.example.playa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playa.model.Venta;

import java.util.Calendar;
import java.util.List;

public class AdapterVenta extends RecyclerView.Adapter<AdapterVenta.VentaViewHolder> implements View.OnClickListener {

    List<Venta> ventas;
    View.OnClickListener listener;

    public AdapterVenta(List<Venta> ventas) {
        this.ventas = ventas;
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowrecycler, parent, false);
        VentaViewHolder holder = new VentaViewHolder(v);

        v.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {



        Venta venta = ventas.get(position);
        if (venta.getEntrada() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(venta.getEntrada());
            holder.tvrvEntrada.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        }
        if (venta.getSalida() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(venta.getSalida());
            holder.tvrvSalida.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        }
        if (venta.getPatente() != null){
            holder.tvrvPatente.setText(venta.getPatente().toUpperCase());

        }
        if (venta.getEntrada() != null){

            holder.tvrvTotal.setText(String.valueOf(venta.getCosto()));
        }
    }



    @Override
    public int getItemCount() {
        return ventas.size();
    }

    public void setOnclickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {

        if(this.listener != null){
            listener.onClick(v);
        }
    }

    public static class VentaViewHolder extends RecyclerView.ViewHolder{

        TextView tvrvEntrada, tvrvSalida, tvrvPatente, tvrvTotal;

        public VentaViewHolder(@NonNull View v) {
            super(v);

            tvrvEntrada = v.findViewById(R.id.tvrvEntrada);
            tvrvSalida = v.findViewById(R.id.tvrvSalida);
            tvrvPatente = v.findViewById(R.id.tvrvPatente);
            tvrvTotal = v.findViewById(R.id.tvrvTotal);
        }
    }
}
