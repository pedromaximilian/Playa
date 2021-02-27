package com.example.playa.ui.home;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playa.AdapterVenta;
import com.example.playa.R;
import com.example.playa.model.Venta;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    List<Venta> listaVentas = new ArrayList<Venta>();
    ArrayAdapter<Venta> ventaArrayAdapter;
    Venta ventaSelected;


    RecyclerView rvLista;
    AdapterVenta adapterVenta;

    Button btnEntrada;
    Button btnSalida;
    EditText etPatente;
    View view;
    ListView lvEntradas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(HomeViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        btnEntrada = view.findViewById(R.id.btnEntrada);
        btnSalida = view.findViewById(R.id.btnSalida);
        etPatente = view.findViewById(R.id.etPatente);

        rvLista = view.findViewById(R.id.rvLista);


        inicializarFirebase();
        listarDatos();

        adapterVenta = new AdapterVenta(listaVentas);
        rvLista.setLayoutManager(new LinearLayoutManager(getContext()));


        btnSalida.setEnabled(false);

        btnEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarEntrada(etPatente.getText().toString().trim());
                etPatente.setText("");
                esconderTeclado(getActivity());

            }
        });

        btnSalida.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onLongClick(View v) {
                if (ventaSelected.getSalida() == null){
                    ventaSelected.setSalida(new Date());

                    long minutos = tiempoEstadia(ventaSelected.getSalida(), ventaSelected.getEntrada());

                    double costo = calculoEstadia(minutos);
                    ventaSelected.setCosto(costo);
                    databaseReference.child("Venta").child(ventaSelected.getUid()).setValue(ventaSelected);
                    Toast.makeText(view.getContext(), "Salida", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Esta patente ya posee salida", Toast.LENGTH_SHORT).show();
                }
                btnSalida.setEnabled(false);
                etPatente.setText("");
                ventaSelected = new Venta();



                return false;
            }
        });

        adapterVenta.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ventaSelected = (Venta) parent.getItemAtPosition(position);


                etPatente.setText(listaVentas.get(rvLista.getChildAdapterPosition(v)).getPatente());
                btnSalida.setEnabled(true);
            }
        });



        return view;
    }


    private void onItemVlic(){

        ;


    }

    private double calculoEstadia(double minutos) {

        double precio = 20;
        double fraccion = 20;
        minutos = Math.abs(minutos);

        if (minutos <= 60) {
            return  60;
        } else {
            if (minutos % fraccion == 0) {
                return  (minutos / fraccion) * precio;
            } else {
                return Math.round(minutos/fraccion)*precio;
            }

        }


    }

    private void listarDatos() {
        databaseReference.child("Venta").orderByChild("patente").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaVentas.clear();
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Venta v = objSnapshot.getValue(Venta.class);
                    listaVentas.add(v);

                    rvLista.setAdapter(adapterVenta);

                }
                adapterVenta.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private boolean grabarEntrada(String patente) {

        if (patente.isEmpty()){
            etPatente.setError("Este campo no debe estar vacio");
        }else{
            Venta venta = new Venta();
            venta.setUid(UUID.randomUUID().toString());
            venta.setPatente(etPatente.getText().toString());
            venta.setEntrada(new Date());
            venta.setTimestamp(ServerValue.TIMESTAMP);
            databaseReference.child("Venta").child(venta.getUid()).setValue(venta);

        }
       return true;
    }

    public static void esconderTeclado(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static long tiempoEstadia(Date entrada, Date salida) {
        long diff = salida.getTime() - entrada.getTime();
        long diffMinutes = (diff / 1000) / 60;


        return diffMinutes;
    }


}

