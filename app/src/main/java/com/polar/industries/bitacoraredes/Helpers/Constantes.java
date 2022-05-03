package com.polar.industries.bitacoraredes.Helpers;

public class Constantes {
    public static final int VERSION_BD = 1;
    public static final String NOMBRE_BD = "bd_reportes";
    public static final String  TABLA_REPORTE = "reportes";

    public static final String CAMPO_ID = "id";
    public static final String CAMPO_MAC_ORIGEN = "mac_origen";
    public static final String CAMPO_MAC_DESTINO = "mac_destino";
    public static final String CAMPO_IP_ORIGEN = "ip_origen";
    public static final String CAMPO_IP_DESTINO = "ip_destino";
    public static final String CAMPO_PUERTO_ORIGEN = "puerto_origen";
    public static final String CAMPO_PUERTO_DESTINO = "puerto_destino";
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_HORA = "hora";
    public static final String CAMPO_OBSERVACIONES = "observaciones";

    public static final String CREAR_TABLA_REPORTE = "CREATE TABLE " + TABLA_REPORTE +"(id INTEGER PRIMARY KEY, mac_origen TEXT, mac_destino TEXT, ip_origen TEXT, ip_destino TEXT, puerto_origen TEXT," +
            "puerto_destino TEXT, fecha TEXT, hora TEXT, observaciones TEXT)";

    public static final String CONSULTA_TABLA_REPORTE = "SELECT * FROM " + TABLA_REPORTE ;



}
