package com.example.playa.ui.gallery;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.playa.R;
import com.example.playa.model.Venta;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GalleryFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {
    List<Venta> listaVentas = new ArrayList<Venta>();
    private GalleryViewModel galleryViewModel;
    int numeroPlaya = 1;
    CalendarView cv;
    Button btnReportes;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView tvCantAutos, tvTotalDia;
    Double totalVentas;
    int totalAutos;
    String fecha;


    String[] playa = { "1", "2", "3"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);


        inicializarFirebase();
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) view.findViewById(R.id.spPlayas);
        spin.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,playa);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        cv = view.findViewById(R.id.calendarView);
        btnReportes = view.findViewById(R.id.btnReportes);
        tvCantAutos = view.findViewById(R.id.tvCanAutos);
        tvTotalDia = view.findViewById(R.id.tvTotalMoney);
        btnReportes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), ""+cv.getDateTextAppearance(), Toast.LENGTH_SHORT).show();
                listarDatos();

            }
        });
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {


                Calendar cal = new Calendar.Builder().setDate(year, month, dayOfMonth).build();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                fecha = sdf.format(cal.getTime());

            }
        });
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listarDatos() {
        databaseReference.child(fecha+"/"+numeroPlaya+"/Venta").orderByChild("salida").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaVentas.clear();

                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Venta v = objSnapshot.getValue(Venta.class);

                    listaVentas.add(v);
                }

                totalAutos = 0;
                totalVentas = 0.0;
                //Toast.makeText(getContext(), "Buscar", Toast.LENGTH_SHORT).show();
                if(listaVentas != null){
                    for (Venta venta: listaVentas
                    ) {
                        totalVentas+= venta.getCosto();
                        totalAutos++;
                    }
                }
                tvCantAutos.setText(""+totalAutos);
                tvTotalDia.setText("$"+totalVentas);


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
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       // Toast.makeText(getContext(),country[position] , Toast.LENGTH_LONG).show();

        numeroPlaya = Integer.parseInt(playa[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}