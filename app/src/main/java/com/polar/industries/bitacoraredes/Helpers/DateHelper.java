package com.polar.industries.bitacoraredes.Helpers;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper {

    /**
     *
     * @return retorna la fecha en formato año-mes-dia hrs:min:seg formato 24 hs
     */
    public static String obtenerFecha() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    /**
     *
     * @return retorna la hora actual en formato de 12 hrs
     */
    public static String obtenerHora12AM()
    {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm  aa");
        return  df.format(new Date()).toUpperCase();
    }

    /**
     *
     * @return retorna la fecha actual en formato de 24 hrs
     */
    public static String obtenerHora24()
    {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return  df.format(new Date()).toUpperCase();
    }

    /**
     *
     * @return retorna a fecha actual en formato dia-mes-año
     */
    public static String obtenerFechaActual()
    {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return  df.format(new Date());
    }
    /**
     * @param fechaFirebase fecha tomada de la asesoría
     * @param horaFin hora de fin de la asesoría
     * @return retorna true si la fecha de la asesoría es antes de la actual de lo contrario false
     * @throws ParseException genera una excepcion si la {@param fechaFirebase} y {@param horaFin} no se puede convertir a Date object
     * @throws NullPointerException genera una excepción si {@param fechaFirebase} y {@param horaFin} llegan null
     *
     * @implNote Tomar el atributo hora_fin de la asesoría
     */
    public static boolean expiracionFecha(String fechaFirebase,String horaFin) throws ParseException, NullPointerException
    {
        // dd= día formato numero
        // MM = mes formato numero
        // yyyy= año
        // HH= hora formato 24 hrs
        // hh = formato 12
        // mm = minutos
        // aa  formato de 12 horas AM o PM
        SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MM-yyyy hh:mm:ss aa");
        Date dateActual = new Date();
        dateFormat.format(dateActual);
       Log.e("err","DateActual: " + dateFormat.format(dateActual));
       //15-07-2021 12:54 p. m.                        15-07-2021        12:54:00                                   p.                                   m.
        Date dateFirebase = dateFormat.parse(fechaFirebase+" "+horaFin.substring(0,5)+":00 "+horaFin.substring(6,7).toLowerCase()+". "+horaFin.substring(7).toLowerCase()+".");

        //System.out.println("DateFirebase: " + dateFormat.format(dateFirebase));
        //System.out.println("DateActual: " + dateFormat.format(dateActual));
        return dateFirebase.before(dateActual);
    }

    /**
     * @param fechaFirebase fecha tomada de la asesoría
     * @param horaFin hora de fin de la asesoría
     * @param horaInicio hora de inicio de la asesoría
     * @return retorna true si la fecha de la asesoría inicio es antes de la fecha de aseoria fin de lo contrario false
     * @throws ParseException genera una excepcion si la {@param fechaFirebase} y {@param horaFin} no se puede convertir a Date object
     * @throws NullPointerException genera una excepción si {@param fechaFirebase} y {@param horaFin} llegan null
     *
     * @implNote Tomar el atributo hora_fin de la asesoría
     */
    public static boolean expiracionFecha(String fechaFirebase,String horaFin,String horaInicio) throws ParseException, NullPointerException
    {
        // dd= día formato numero
        // MM = mes formato numero
        // yyyy= año
        // HH= hora formato 24 hrs
        // hh = formato 12
        // mm = minutos
        // aa  formato de 12 horas AM o PM
        SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MM-yyyy hh:mm:ss aa");



        //15-07-2021 12:54 p. m.                        15-07-2021        12:54:00                                   p.                                   m.
        Date datefin = dateFormat.parse(fechaFirebase+" "+horaFin.substring(0,5)+":00 "+horaFin.substring(6,7).toLowerCase()+". "+horaFin.substring(7).toLowerCase()+".");
        Date dateInicio = dateFormat.parse(fechaFirebase+" "+horaInicio.substring(0,5)+":00 "+horaInicio.substring(6,7).toLowerCase()+". "+horaInicio.substring(7).toLowerCase()+".");

        //System.out.println("DateFirebase: " + dateFormat.format(dateFirebase));
        //System.out.println("DateActual: " + dateFormat.format(dateActual));
        return dateInicio.before(datefin);
    }

    /**
     *
     * @param horaFin
     * @return retorna la diferencia de horas con respecto a la fecha actual
     * @throws ParseException
     */
    public static long diferenciaHoras(String horaFin) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date dateFirebase = sdf.parse(horaFin.substring(0,5)+":00 "+horaFin.substring(6,7).toLowerCase()+". "+horaFin.substring(7).toLowerCase()+".");
        Date now = new Date();
        now = sdf.parse(sdf.format(now));
        //System.out.println("DateFirebase: " + sdf.format(dateFirebase));
        //System.out.println("DateActual: " + sdf.format(now));
        long diferencia = now.getTime() - dateFirebase.getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(diferencia);
        System.out.println(hours);
        return hours*-1;
    }

    /**
     *
     * @param horaInicio
     * @param horaFin
     * @return retorna la diferencia de horas entre la hora de inicio y la hora de fin
     * @throws ParseException
     */
    public static long[] diferenciaHoras(String horaInicio,String horaFin) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date dateFin = sdf.parse(horaFin.substring(0,5)+":00 "+horaFin.substring(6,7).toLowerCase()+". "+horaFin.substring(7).toLowerCase()+".");
        Date dateInicio = sdf.parse(horaInicio.substring(0,5)+":00 "+horaInicio.substring(6,7).toLowerCase()+". "+horaInicio.substring(7).toLowerCase()+".");
        long diferencia = dateFin.getTime() - dateInicio.getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(diferencia);
        long min = TimeUnit.MILLISECONDS.toMinutes(diferencia)%60;
        min=min>0?min:min*-1;
        hours= hours>0?hours:hours*-1;
        return new long[]{hours, min};
    }



}
