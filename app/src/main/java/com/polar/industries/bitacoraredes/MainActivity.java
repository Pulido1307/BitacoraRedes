package com.polar.industries.bitacoraredes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.polar.industries.bitacoraredes.Entidades.Reporte;
import com.polar.industries.bitacoraredes.Helpers.ConexionSQLite;
import com.polar.industries.bitacoraredes.Helpers.Constantes;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ConexionSQLite conexionSQLite;
    private FloatingActionButton floatingAdd;
    private FloatingActionButton floatingReports;
    private TextInputEditText textInputEditText_fecha;
    private TextView textView_sin_data;
    private ListView listView_Data;
    private String ERROR = "Campo requerido";
    private SQLiteDatabase db;
    private ArrayAdapter arrayAdapterListView;
    private ArrayList<String> listaInformacion;
    private ArrayList<Reporte> listaReportes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexionSQLite = new ConexionSQLite(MainActivity.this, Constantes.NOMBRE_BD, null, Constantes.VERSION_BD);
        floatingAdd = findViewById(R.id.floatingAdd);
        floatingReports = findViewById(R.id.floatingReports);
        textInputEditText_fecha = findViewById(R.id.textInputEditText_fecha);
        textView_sin_data = findViewById(R.id.textView_sin_data);
        listView_Data = findViewById(R.id.listView_Data);

        listaInformacion = new ArrayList<String>();
        listaReportes = new ArrayList<Reporte>();


        arrayAdapterListView = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, listaInformacion);
        listView_Data.setAdapter(arrayAdapterListView);

        buttons();
        consultaSQLite();
    }


    private void buttons() {

        floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAdd();
            }
        });

        floatingReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogGeneraPDF();
            }
        });

        textInputEditText_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);

                DatePickerDialog pickerDialogFecha = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yearD, int monthD, int dayD) {
                        int mesActual = monthD + 1;
                        String diaFormateado = (dayD < 10)? "0" + String.valueOf(dayD):String.valueOf(dayD);
                        String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);
                        textInputEditText_fecha.setText(diaFormateado + "/" + mesFormateado + "/" + yearD);
                    }
                }, year, month, day);
                pickerDialogFecha.show();
            }
        });

        listView_Data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showDialogUpdate(listaReportes.get(position));
            }
        });
    }

    private void showDialogUpdate(Reporte reporte){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_actualizar_reportes, null);
        builder.setView(view).setTitle("Actualizar reporte");

        final AlertDialog dialogUpdate = builder.create();
        dialogUpdate.setCancelable(false);
        dialogUpdate.show();


        final MaterialButton materialButton_Modificar = dialogUpdate.findViewById(R.id.materialButton_Modificar);
        final MaterialButton materialButton_Eliminar = dialogUpdate.findViewById(R.id.materialButton_Eliminar);
        final MaterialButton materialButton_Salir_update = dialogUpdate.findViewById(R.id.materialButton_Salir_update);


        final TextInputEditText textInputEditText_observaciones = dialogUpdate.findViewById(R.id.textInputEditText_observaciones_update);
        final TextInputEditText textInputEditText_hora = dialogUpdate.findViewById(R.id.textInputEditText_hora_update);
        final TextInputEditText textInputEditText_ipD = dialogUpdate.findViewById(R.id.textInputEditText_ipD_update);
        final TextInputEditText textInputEditText_ipO = dialogUpdate.findViewById(R.id.textInputEditText_ipO_update);
        final TextInputEditText textInputEditText_macD = dialogUpdate.findViewById(R.id.textInputEditText_macD_update);
        final TextInputEditText textInputEditText_macO = dialogUpdate.findViewById(R.id.textInputEditText_macO_update);
        final TextInputEditText textInputEditText_puertoO = dialogUpdate.findViewById(R.id.textInputEditText_portOUp);
        final TextInputEditText textInputEditText_puertoD = dialogUpdate.findViewById(R.id.textInputEditText_portDUp);

        textInputEditText_macO.setText(reporte.getMac_origen());
        textInputEditText_macD.setText(reporte.getMac_destino());
        textInputEditText_ipO.setText(reporte.getIp_origen());
        textInputEditText_ipD.setText(reporte.getIp_destino());

        textInputEditText_puertoO.setText(reporte.getPuerto_origen());
        textInputEditText_puertoD.setText(reporte.getPuerto_origen());

        textInputEditText_hora.setText(reporte.getHora());
        textInputEditText_observaciones.setText(reporte.getObservaciones());

        materialButton_Modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag_macO = false;
                boolean flag_macD = false;
                boolean flag_ipO = false;
                boolean flag_ipD = false;
                boolean flag_puertoD = false;
                boolean flag_puertoO = false;
                boolean flag_hora = false;
                String observaciones = "";

                if (!textInputEditText_macO.getText().toString().equals("") && !textInputEditText_macO.getText().toString().isEmpty()){
                    flag_macO = true;
                } else{
                    textInputEditText_macO.setError(ERROR);
                }

                if (!textInputEditText_macO.getText().toString().equals("") && !textInputEditText_macD.getText().toString().isEmpty()){
                    flag_macD = true;
                } else{
                    textInputEditText_macD.setError(ERROR);
                }

                if (!textInputEditText_ipO.getText().toString().equals("") && !textInputEditText_ipO.getText().toString().isEmpty()){
                    flag_ipO = true;
                } else{
                    textInputEditText_ipO.setError(ERROR);
                }

                if (!textInputEditText_ipD.getText().toString().equals("") && !textInputEditText_ipD.getText().toString().isEmpty()){
                    flag_ipD = true;
                } else{
                    textInputEditText_ipO.setError(ERROR);
                }

                if (!textInputEditText_puertoO.getText().toString().equals("") && !textInputEditText_puertoO.getText().toString().isEmpty()){
                    flag_puertoO = true;
                } else{
                    textInputEditText_puertoO.setError(ERROR);
                }

                if (!textInputEditText_puertoD.getText().toString().equals("") && !textInputEditText_puertoD.getText().toString().isEmpty()){
                    flag_puertoD = true;
                } else{
                    textInputEditText_puertoD.setError(ERROR);
                }

                if (!textInputEditText_hora.getText().toString().equals("") && !textInputEditText_hora.getText().toString().isEmpty()){
                    flag_hora = true;
                } else{
                    textInputEditText_hora.setError(ERROR);
                }

                if(textInputEditText_observaciones.getText().toString().equals("") && textInputEditText_observaciones.getText().toString().isEmpty()){
                    observaciones = "Sin observaciones";
                }else{
                    observaciones = textInputEditText_observaciones.getText().toString();
                }

                if(flag_macO && flag_macD && flag_ipO && flag_ipD && flag_puertoO && flag_puertoD && flag_hora){
                    db = conexionSQLite.getWritableDatabase();

                    String parametros[] = {String.valueOf(reporte.getId())};
                    ContentValues values = new ContentValues();

                    values.put(Constantes.CAMPO_MAC_ORIGEN, textInputEditText_macO.getText().toString());
                    values.put(Constantes.CAMPO_MAC_DESTINO, textInputEditText_macD.getText().toString());
                    values.put(Constantes.CAMPO_IP_ORIGEN, textInputEditText_ipO.getText().toString());
                    values.put(Constantes.CAMPO_IP_DESTINO, textInputEditText_ipD.getText().toString());
                    values.put(Constantes.CAMPO_PUERTO_ORIGEN, textInputEditText_puertoO.getText().toString());
                    values.put(Constantes.CAMPO_PUERTO_DESTINO, textInputEditText_puertoD.getText().toString());
                    values.put(Constantes.CAMPO_FECHA, reporte.getFecha());
                    values.put(Constantes.CAMPO_HORA, textInputEditText_hora.getText().toString());
                    values.put(Constantes.CAMPO_OBSERVACIONES, observaciones);


                    db.update(Constantes.TABLA_REPORTE, values, "id=?", parametros);

                    consultaSQLite();
                    arrayAdapterListView.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "¡Registro actualizado!", Toast.LENGTH_SHORT).show();
                    dialogUpdate.dismiss();
                }else {
                    Toast.makeText(MainActivity.this, "¡Algunos de los datos ingresados son inválidos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        materialButton_Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogConfirm = new AlertDialog.Builder(MainActivity.this);
                dialogConfirm.setTitle("Eliminar este reporte");
                dialogConfirm.setMessage("¿Esta seguro de borrar este reporte?");
                dialogConfirm.setCancelable(false);
                dialogConfirm.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db = conexionSQLite.getWritableDatabase();
                        String parametros[] = {String.valueOf(reporte.getId())};

                        db.delete(Constantes.TABLA_REPORTE, "id=?", parametros);
                        Toast.makeText(MainActivity.this, "¡Registro Eliminado con Éxito!", Toast.LENGTH_SHORT).show();
                        consultaSQLite();
                        arrayAdapterListView.notifyDataSetChanged();
                        dialogUpdate.dismiss();
                    }
                });
                dialogConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogConfirm.show();
            }
        });

        materialButton_Salir_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUpdate.dismiss();
            }
        });
    }


    private void showDialogAdd(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_agregar, null);
        builder.setView(view).setTitle("Agregar reporte");

        final AlertDialog dialogAdd = builder.create();
        //dialogAdd.setCancelable(false);
        dialogAdd.show();

        final MaterialButton materialButton_Salir_add = dialogAdd.findViewById(R.id.materialButton_Salir_add);
        final MaterialButton materialButton_Registrar = dialogAdd.findViewById(R.id.materialButton_Registrar);

        final TextInputEditText textInputEditText_observaciones = dialogAdd.findViewById(R.id.textInputEditText_observaciones);
        final TextInputEditText textInputEditText_hora = dialogAdd.findViewById(R.id.textInputEditText_hora);
        final TextInputEditText textInputEditText_puertoD = dialogAdd.findViewById(R.id.textInputEditText_puertoD);
        final TextInputEditText textInputEditText_puertoO = dialogAdd.findViewById(R.id.textInputEditText_puertoO);
        final TextInputEditText textInputEditText_ipD = dialogAdd.findViewById(R.id.textInputEditText_ipD);
        final TextInputEditText textInputEditText_ipO = dialogAdd.findViewById(R.id.textInputEditText_ipO);
        final TextInputEditText textInputEditText_macD = dialogAdd.findViewById(R.id.textInputEditText_macD);
        final TextInputEditText textInputEditText_macO = dialogAdd.findViewById(R.id.textInputEditText_macO);

        textInputEditText_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                final int[] hora = {c.get(Calendar.HOUR_OF_DAY)};
                final int[] minuto = {c.get(Calendar.MINUTE)};

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hora[0] = selectedHour;
                        minuto[0] = selectedMinute;

                        textInputEditText_hora.setText(String.format(Locale.getDefault(), "%02d:%02d", hora[0], minuto[0]));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, onTimeSetListener, hora[0], minuto[0], true);
                timePickerDialog.setTitle("Seleccionar hora");
                timePickerDialog.show();
            }
        });


       materialButton_Registrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               boolean flag_macO = false;
               boolean flag_macD = false;
               boolean flag_ipO = false;
               boolean flag_ipD = false;
               boolean flag_puertoD = false;
               boolean flag_puertoO = false;
               boolean flag_hora = false;
               String observaciones = "";

               if (!textInputEditText_macO.getText().toString().equals("") && !textInputEditText_macO.getText().toString().isEmpty()){
                   flag_macO = true;
               } else{
                   textInputEditText_macO.setError(ERROR);
               }

               if (!textInputEditText_macO.getText().toString().equals("") && !textInputEditText_macD.getText().toString().isEmpty()){
                   flag_macD = true;
               } else{
                   textInputEditText_macD.setError(ERROR);
               }

               if (!textInputEditText_ipO.getText().toString().equals("") && !textInputEditText_ipO.getText().toString().isEmpty()){
                   flag_ipO = true;
               } else{
                   textInputEditText_ipO.setError(ERROR);
               }

               if (!textInputEditText_ipD.getText().toString().equals("") && !textInputEditText_ipD.getText().toString().isEmpty()){
                   flag_ipD = true;
               } else{
                   textInputEditText_ipO.setError(ERROR);
               }

               if (!textInputEditText_puertoO.getText().toString().equals("") && !textInputEditText_puertoO.getText().toString().isEmpty()){
                   flag_puertoO = true;
               } else{
                   textInputEditText_puertoO.setError(ERROR);
               }

               if (!textInputEditText_puertoD.getText().toString().equals("") && !textInputEditText_puertoD.getText().toString().isEmpty()){
                   flag_puertoD = true;
               } else{
                   textInputEditText_puertoD.setError(ERROR);
               }

               if (!textInputEditText_hora.getText().toString().equals("") && !textInputEditText_hora.getText().toString().isEmpty()){
                   flag_hora = true;
               } else{
                   textInputEditText_hora.setError(ERROR);
               }

               if(textInputEditText_observaciones.getText().toString().equals("") && textInputEditText_observaciones.getText().toString().isEmpty()){
                   observaciones = "Sin observaciones";
               }else{
                   observaciones = textInputEditText_observaciones.getText().toString();
               }

               if(flag_macO && flag_macD && flag_ipO && flag_ipD && flag_puertoO && flag_puertoD && flag_hora){
                   db = conexionSQLite.getWritableDatabase();

                   ContentValues values = new ContentValues();
                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                   Date date = new Date();

                   String fecha = dateFormat.format(date);

                   values.put(Constantes.CAMPO_MAC_ORIGEN, textInputEditText_macO.getText().toString());
                   values.put(Constantes.CAMPO_MAC_DESTINO, textInputEditText_macD.getText().toString());
                   values.put(Constantes.CAMPO_IP_ORIGEN, textInputEditText_ipO.getText().toString());
                   values.put(Constantes.CAMPO_IP_DESTINO, textInputEditText_ipD.getText().toString());
                   values.put(Constantes.CAMPO_PUERTO_ORIGEN, textInputEditText_puertoO.getText().toString());
                   values.put(Constantes.CAMPO_PUERTO_DESTINO, textInputEditText_puertoD.getText().toString());
                   values.put(Constantes.CAMPO_FECHA, fecha);
                   values.put(Constantes.CAMPO_HORA, textInputEditText_hora.getText().toString());
                   values.put(Constantes.CAMPO_OBSERVACIONES, observaciones);

                   Long idResultante = db.insert(Constantes.TABLA_REPORTE, Constantes.CAMPO_ID, values);

                   Toast.makeText(MainActivity.this, "¡Reporte registrado con éxito!", Toast.LENGTH_SHORT).show();
                   consultaSQLite();
                   arrayAdapterListView.notifyDataSetChanged();
                   dialogAdd.dismiss();
               }else {
                   Toast.makeText(MainActivity.this, "Algunos de los datos ingresados son inválidos", Toast.LENGTH_SHORT).show();
               }
           }
       });

       materialButton_Salir_add.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               dialogAdd.dismiss();
           }
       });
    }

    private void showDialogGeneraPDF(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_generar_pdf, null);
        builder.setView(view).setTitle("Generar PDF");

        final AlertDialog dialogPDF = builder.create();
        dialogPDF.show();

    }

    private void consultaSQLite() {
        db = conexionSQLite.getReadableDatabase();

        Reporte reporte;

        if(listaReportes.size() != 0){
            listaReportes.clear();
        }

        Cursor puntero = db.rawQuery(Constantes.CONSULTA_TABLA_REPORTE, null);

        while (puntero.moveToNext()) {
            reporte = new Reporte(puntero.getInt(0), puntero.getString(1), puntero.getString(2), puntero.getString(3), puntero.getString(4), puntero.getString(5), puntero.getString(6),
                    puntero.getString(7), puntero.getString(8), puntero.getString(9));

            listaReportes.add(reporte);
        }

        setLista();
    }

    private void setLista(){
        listaInformacion.clear();
        int i = 0;
        if (listaReportes.size() != 0){
            for(Reporte reporte: listaReportes){
                i++;
                listaInformacion.add("Informe #"+i+"\nFecha:"+reporte.getFecha());
            }
            listView_Data.setVisibility(View.VISIBLE);
            textView_sin_data.setVisibility(View.GONE);
        }else {
            listView_Data.setVisibility(View.GONE);
            textView_sin_data.setVisibility(View.VISIBLE);
        }
    }

}





