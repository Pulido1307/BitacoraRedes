package com.polar.industries.bitacoraredes;

import static com.polar.industries.bitacoraredes.Helpers.NombresDirectorios.NOMBRE_DIRECTORIO;
import static com.polar.industries.bitacoraredes.Helpers.NombresDirectorios.NOMBRE_DOCUMENTO;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.polar.industries.bitacoraredes.Entidades.Reporte;
import com.polar.industries.bitacoraredes.Helpers.ConexionSQLite;
import com.polar.industries.bitacoraredes.Helpers.Constantes;
import com.polar.industries.bitacoraredes.Helpers.DateHelper;
import com.polar.industries.bitacoraredes.Helpers.NombresDirectorios;
import com.polar.industries.bitacoraredes.Helpers.flipper.DocumentFileCompat;
import com.polar.industries.bitacoraredes.Helpers.flipper.OperationFailedException;
import com.polar.industries.bitacoraredes.Helpers.flipper.Root;
import com.polar.industries.bitacoraredes.Helpers.flipper.StorageManagerCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import harmony.java.awt.Color;

public class MainActivity extends AppCompatActivity {

    private ConexionSQLite conexionSQLite;
    private FloatingActionButton floatingAdd;
    private FloatingActionButton floatingReports;
    private TextView textView_sin_data;
    private ListView listView_Data;
    private String ERROR = "Campo requerido";
    private SQLiteDatabase db;
    private ArrayAdapter arrayAdapterListView;
    private ArrayList<String> listaInformacion;
    private ArrayList<Reporte> listaReportes = null;
    private ArrayList<Reporte> listaReportesFiltrados = null;
    private final int REQUEST_CODE_ASK_PERMISSION = 111;
    private StorageManagerCompat manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexionSQLite = new ConexionSQLite(MainActivity.this, Constantes.NOMBRE_BD, null, Constantes.VERSION_BD);
        floatingAdd = findViewById(R.id.floatingAdd);
        floatingReports = findViewById(R.id.floatingReports);
        textView_sin_data = findViewById(R.id.textView_sin_data);
        listView_Data = findViewById(R.id.listView_Data);

        listaInformacion = new ArrayList<String>();
        listaReportes = new ArrayList<Reporte>();
        listaReportesFiltrados = new ArrayList<Reporte>();

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
                   SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

        MaterialButton materialButton_generar_pdf = dialogPDF.findViewById(R.id.materialButton_generar_pdf);
        MaterialButton materialButton_cancelar_pdf = dialogPDF.findViewById(R.id.materialButton_cancelar_pdf);
        TextInputEditText textInputEditText_fecha = dialogPDF.findViewById(R.id.textInputEditText_fecha);

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

        materialButton_generar_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textInputEditText_fecha.getText().toString().isEmpty()){
                    listaReportesFiltrados.clear();
                    String aux = textInputEditText_fecha.getText().toString();
                    Log.e("Aux: ", aux);
                    for(Reporte reporte : listaReportes){
                        Log.e("Aux2: ", reporte.getFecha());
                        if(reporte.getFecha().equals(aux)){
                            listaReportesFiltrados.add(reporte);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        crearPDFAndroidQPlus();
                    } else {
                        crearPdf();
                    }
                }else{
                    textInputEditText_fecha.setError(ERROR);
                    Toast.makeText(MainActivity.this, "Se debe seleccionar una fecha para generar el reporte.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        materialButton_cancelar_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPDF.cancel();
            }
        });
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

    private void crearPdf() {
        if (solicitarPermiso()) {
            try {
                Document documento = new Document(PageSize.LETTER.rotate());
                File f = null;
                    if (listaReportesFiltrados.size() != 0) {
                        f = crearFichero(NOMBRE_DOCUMENTO.texto + "_" + DateHelper.obtenerFecha() + ".pdf");
                        FileOutputStream ficheroPdf = new FileOutputStream(Objects.requireNonNull(f).getAbsolutePath());
                        dibujarPDF(documento, ficheroPdf);
                        Toast.makeText(MainActivity.this, "Se creo tu archivo pdf en " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No hay datos para generar el pdf.", Toast.LENGTH_SHORT).show();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Para guardar el archivo PDF necesita conceder los permisos.", Toast.LENGTH_LONG).show();
        }
    }

    public static File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null)
            fichero = new File(ruta, nombreFichero);
        return fichero;
    }

    public static File getRuta() {
        // El fichero sera almacenado en un directorio dentro del directorio descargas
        File ruta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            ruta = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    NOMBRE_DIRECTORIO.texto);

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        } else {
        }
        return ruta;
    }

    private boolean solicitarPermiso() {
        int permiso = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permiso != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void crearPDFAndroidQPlus() {
        manager = new StorageManagerCompat(getApplicationContext());
        Root root = manager.getRoot(StorageManagerCompat.DEF_MAIN_ROOT);

        if (root == null || !root.isAccessGranted(getApplicationContext())) {
            Intent data = manager.requireExternalAccess(getApplicationContext());
            startActivityForResult(data, 100);
            Log.e("Root: ", "null");
        } else {

            Log.e("Root: ", root.getUri().toString());
            DocumentFile f = root.toRootDirectory(getApplicationContext());
            DocumentFile subFolder = DocumentFileCompat.getSubFolder(f, NOMBRE_DIRECTORIO.texto);
            DocumentFile myFile = null;
                if (listaReportesFiltrados.size() != 0) {
                    try {
                        myFile = DocumentFileCompat.getFile(subFolder, NOMBRE_DOCUMENTO.texto + "_" + DateHelper.obtenerFecha() + ".pdf", "");
                        Document documento = new Document(PageSize.LETTER.rotate());
                        OutputStream os = getContentResolver().openOutputStream(Objects.requireNonNull(myFile).getUri());
                        dibujarPDF(documento, (FileOutputStream) os);
                        Toast.makeText(MainActivity.this, "Se creo tu archivo pdf " + myFile.getUri().getPath(), Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos para generar el pdf.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void dibujarPDF(Document documento, FileOutputStream ficheroPdf) {
        try {
            /*Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_cb);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            writer.setPageEvent(new WaterMark(imagen));*/

            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            Font fontHeaderFooter = FontFactory.getFont(FontFactory.TIMES_ROMAN, FontFactory.defaultEncoding, FontFactory.defaultEmbedding, 12, Font.BOLD, Color.BLACK);
            Paragraph paragraphHeader = new Paragraph("POLAR INDUSTRIES\n" +
                    "BITACORA DE FALLAS EN LA RED\n\n", fontHeaderFooter);

            Phrase phraseFooter = new Phrase("Polar Industries", fontHeaderFooter);

            HeaderFooter cabecera = new HeaderFooter(new Phrase(Objects.requireNonNull(paragraphHeader)), false);
            cabecera.setAlignment(Element.ALIGN_CENTER);

            HeaderFooter pie = new HeaderFooter(new Phrase(phraseFooter), false);
            pie.setAlignment(Element.ALIGN_CENTER);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            Phrase macOrigen = new Phrase("MAC Origen", fontHeaderFooter);
            Phrase macDestino = new Phrase("MAC Destino", fontHeaderFooter);
            Phrase ipOrigen = new Phrase("IP Origen", fontHeaderFooter);
            Phrase ipDestino = new Phrase("IP Destino", fontHeaderFooter);
            Phrase puertoOrigen = new Phrase("Puerto Origen", fontHeaderFooter);
            Phrase puertoDestino = new Phrase("Puerto Destino", fontHeaderFooter);
            Phrase horaHecho = new Phrase("Hora", fontHeaderFooter);
            Phrase observaciones = new Phrase("Observaciones", fontHeaderFooter);

            PdfPCell cellMacOrigen = new PdfPCell(macOrigen);
            cellMacOrigen.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellMacOrigen.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellMacDestino = new PdfPCell(macDestino);
            cellMacDestino.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellMacDestino.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellIpOrigen = new PdfPCell(ipOrigen);
            cellIpOrigen.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellIpOrigen.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellIpDestino = new PdfPCell(ipDestino);
            cellIpDestino.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellIpDestino.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellPuertoOrigen = new PdfPCell(puertoOrigen);
            cellPuertoOrigen.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellPuertoOrigen.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellPuertoDestino = new PdfPCell(puertoDestino);
            cellPuertoDestino.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellPuertoDestino.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellHoraHecho = new PdfPCell(horaHecho);
            cellHoraHecho.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellHoraHecho.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellObservaciones = new PdfPCell(observaciones);
            cellObservaciones.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellObservaciones.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPTable tabla = new PdfPTable(8);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{(float) 12, (float) 12, (float) 13, (float) 13, (float) 12.5, (float) 12.5, (float) 12.5, (float) 12.5});
            tabla.addCell(cellMacOrigen);
            tabla.addCell(cellMacDestino);
            tabla.addCell(cellIpOrigen);
            tabla.addCell(cellIpDestino);
            tabla.addCell(cellPuertoOrigen);
            tabla.addCell(cellPuertoDestino);
            tabla.addCell(cellHoraHecho);
            tabla.addCell(cellObservaciones);
            tabla.setHeaderRows(1);

            for (int i = 0; i < listaReportesFiltrados.size(); i++) {
                Log.e("Si entra al for", "");
                tabla.addCell(listaReportesFiltrados.get(i).getMac_origen());
                tabla.addCell(listaReportesFiltrados.get(i).getMac_destino());
                tabla.addCell(listaReportesFiltrados.get(i).getIp_origen());
                tabla.addCell(listaReportesFiltrados.get(i).getIp_destino());
                tabla.addCell(listaReportesFiltrados.get(i).getPuerto_destino());
                tabla.addCell(listaReportesFiltrados.get(i).getPuerto_origen());
                tabla.addCell(listaReportesFiltrados.get(i).getHora());
                tabla.addCell(listaReportesFiltrados.get(i).getObservaciones());
            }
            tabla.setHorizontalAlignment(Element.ALIGN_CENTER);

            documento.open();
            documento.add(tabla);
            documento.close();
            writer.close();

        } catch (DocumentException e) {
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Root root = manager.addRoot(getApplicationContext(), StorageManagerCompat.DEF_MAIN_ROOT, data);
            //  Log.e("root: ", Objects.requireNonNull(root).getUri().toString());
            if (root == null)
                return;
            DocumentFile f = root.toRootDirectory(getApplicationContext());
            if (f == null)
                return;
            try {
                DocumentFile subFolder = DocumentFileCompat.getSubFolder(f, NOMBRE_DIRECTORIO.texto);
                DocumentFile myFile = null;
                if (listaReportesFiltrados.size() != 0) {
                    try {
                        myFile = DocumentFileCompat.getFile(subFolder, NOMBRE_DOCUMENTO.texto + "_" + DateHelper.obtenerFecha() + ".pdf", "");
                        Document documento = new Document(PageSize.LETTER.rotate());
                        OutputStream os = getContentResolver().openOutputStream(Objects.requireNonNull(myFile).getUri());
                        dibujarPDF(documento, (FileOutputStream) os);
                        Toast.makeText(MainActivity.this, "Se creo tu archivo pdf " + myFile.getUri().getPath(), Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos para generar el pdf.", Toast.LENGTH_SHORT).show();
                }
            } catch (OperationFailedException e) {
                e.printStackTrace();
            }
        }
    }
}





