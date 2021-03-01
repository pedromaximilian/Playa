package com.example.playa.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playa.AdapterVenta;
import com.example.playa.R;
import com.example.playa.model.Venta;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    int autos = 0;
    int numeroPlaya =1;
    private HomeViewModel homeViewModel;
    List<Venta> listaVentas = new ArrayList<Venta>();
    ArrayAdapter<Venta> ventaArrayAdapter;
    Venta ventaSelected;
    private FirebaseAuth mAuth;
    TextView tvAutos;
    View llItem;


    RecyclerView rvLista;
    AdapterVenta adapterVenta;

    Button btnEntrada;
    Button btnSalida;
    EditText etPatente;
    View view;
    ListView lvEntradas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;



    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(HomeViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        btnEntrada = view.findViewById(R.id.btnEntrada);
        btnSalida = view.findViewById(R.id.btnSalida);
        etPatente = view.findViewById(R.id.etPatente);
        tvAutos = view.findViewById(R.id.tvAutos);
        llItem = view.findViewById(R.id.llItem);

        rvLista = view.findViewById(R.id.rvLista);


        inicializarFirebase();
        listarDatos();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        adapterVenta = new AdapterVenta(listaVentas);
        rvLista.setLayoutManager(new LinearLayoutManager(getContext()));


        etPatente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSalida.setEnabled(false);
                etPatente.setText("");
                ventaSelected = new Venta();
            }
        });

        btnSalida.setEnabled(false);

        btnEntrada.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                grabarSalida();
                btnSalida.setEnabled(false);
                etPatente.setText("");
                ventaSelected = new Venta();
                etPatente.setTextColor(Color.BLACK);
                return false;
            }


        });

        adapterVenta.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventaSelected = (Venta) listaVentas.get(rvLista.getChildAdapterPosition(v));


                etPatente.setText(ventaSelected.getPatente());
                btnSalida.setEnabled(true);

                TextView tv =v.findViewById(R.id.tvrvPatente);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.DKGRAY);
                etPatente.setTextColor(Color.RED);
            }
        });

        return view;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listarDatos() {
        databaseReference.child(LocalDate.now().toString()+"/"+numeroPlaya+"/Venta").orderByChild("salida").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaVentas.clear();
                autos = 0;
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Venta v = objSnapshot.getValue(Venta.class);
                    listaVentas.add(v);
                    if(v.getSalida() == null){
                        autos++;
                        tvAutos.setText("    Pendientes: " + autos);
                    }else{
                        tvAutos.setText(autos+"");
                    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean grabarEntrada(String patente) {

        if (patente.isEmpty()){
            etPatente.setError("Este campo no debe estar vacio");
        }else{
            Venta venta = new Venta();
            venta.setUid(UUID.randomUUID().toString());
            venta.setPatente(etPatente.getText().toString());
            venta.setEntrada(new Date());
            venta.setTimestamp(ServerValue.TIMESTAMP);
            venta.setUsuario(user.getEmail());
            databaseReference.child(LocalDate.now().toString()).child(numeroPlaya+"/Venta").child(venta.getUid()).setValue(venta);

        }
       return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void grabarSalida() {
        if (ventaSelected.getSalida() == null){
            ventaSelected.setSalida(new Date());

            long minutos = tiempoEstadia(ventaSelected.getSalida(), ventaSelected.getEntrada());

            double costo = calculoEstadia(minutos);
            ventaSelected.setCosto(costo);
            databaseReference.child(LocalDate.now().toString()+"/"+numeroPlaya+"/Venta").child(ventaSelected.getUid()).setValue(ventaSelected);
            Toast.makeText(view.getContext(), "Salida", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Esta patente ya posee salida", Toast.LENGTH_SHORT).show();
        }

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

