package com.polar.industries.bitacoraredes.Entidades;

public class Reporte {
    private Integer id;
    private String mac_origen;
    private String mac_destino;
    private String ip_origen;
    private String ip_destino;
    private String puerto_origen;
    private String puerto_destino;
    private String fecha;
    private String hora;
    private String observaciones;

    public Reporte(Integer id, String mac_origen, String mac_destino, String ip_origen, String ip_destino, String puerto_origen, String puerto_destino,
                   String fecha, String hora, String observaciones) {
        this.id = id;
        this.mac_origen = mac_origen;
        this.mac_destino = mac_destino;
        this.ip_origen = ip_origen;
        this.ip_destino = ip_destino;
        this.puerto_origen = puerto_origen;
        this.puerto_destino = puerto_destino;
        this.fecha = fecha;
        this.hora = hora;
        this.observaciones = observaciones;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMac_origen() {
        return mac_origen;
    }

    public void setMac_origen(String mac_origen) {
        this.mac_origen = mac_origen;
    }

    public String getMac_destino() {
        return mac_destino;
    }

    public void setMac_destino(String mac_destino) {
        this.mac_destino = mac_destino;
    }

    public String getIp_origen() {
        return ip_origen;
    }

    public void setIp_origen(String ip_origen) {
        this.ip_origen = ip_origen;
    }

    public String getIp_destino() {
        return ip_destino;
    }

    public void setIp_destino(String ip_destino) {
        this.ip_destino = ip_destino;
    }

    public String getPuerto_origen() {
        return puerto_origen;
    }

    public void setPuerto_origen(String puerto_origen) {
        this.puerto_origen = puerto_origen;
    }

    public String getPuerto_destino() {
        return puerto_destino;
    }

    public void setPuerto_destino(String puerto_destino) {
        this.puerto_destino = puerto_destino;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
