package com.chibuleo.sfcmovil.model;

public class Impresora {
    private String nombreImpresora;
    private String datosRecibo;
    private String nombreRecibo;
    private String datosEnvioImpresora;

    public Impresora() {
    }

    public Impresora(String nombreImpresora, String datosRecibo, String nombreRecibo,
                     String datosEnvioImpresora) {
        this.nombreImpresora = nombreImpresora;
        this.datosRecibo = datosRecibo;
        this.nombreRecibo = nombreRecibo;
        this.datosEnvioImpresora = datosEnvioImpresora;
    }

    public String getNombreImpresora() {
        return nombreImpresora;
    }

    public void setNombreImpresora(String nombreImpresora) {
        this.nombreImpresora = nombreImpresora;
    }

    public String getDatosRecibo() {
        return datosRecibo;
    }

    public void setDatosRecibo(String datosRecibo) {
        this.datosRecibo = datosRecibo;
    }

    public String getNombreRecibo() {
        return nombreRecibo;
    }

    public void setNombreRecibo(String nombreRecibo) {
        this.nombreRecibo = nombreRecibo;
    }

    public String getDatosEnvioImpresora() {
        return datosEnvioImpresora;
    }

    public void setDatosEnvioImpresora(String datosEnvioImpresora) {
        this.datosEnvioImpresora = datosEnvioImpresora;
    }
}
