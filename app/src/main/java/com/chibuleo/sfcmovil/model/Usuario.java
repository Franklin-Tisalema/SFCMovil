package com.chibuleo.sfcmovil.model;

public class Usuario {
    private String clave;
    private String imei;
    private String numeroTelefono;
    private Boolean estado;
    private String mensaje1;
    private String mensaje2;
    private Boolean ingresoConHuella;

    public Usuario() {
    }

    public Usuario(String clave, String imei,
                   String numeroTelefono, Boolean estado, String mensaje1,
                   String mensaje2, Boolean ingresoConHuella) {
        this.clave = clave;
        this.imei = imei;
        this.numeroTelefono = numeroTelefono;
        this.estado = estado;
        this.mensaje1 = mensaje1;
        this.mensaje2 = mensaje2;
        this.ingresoConHuella = ingresoConHuella;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getMensaje1() {
        return mensaje1;
    }

    public void setMensaje1(String mensaje1) {
        this.mensaje1 = mensaje1;
    }

    public String getMensaje2() {
        return mensaje2;
    }

    public void setMensaje2(String mensaje2) {
        this.mensaje2 = mensaje2;
    }

    public Boolean getIngresoConHuella() {
        return ingresoConHuella;
    }

    public void setIngresoConHuella(Boolean ingresoConHuella) {
        this.ingresoConHuella = ingresoConHuella;
    }
}
