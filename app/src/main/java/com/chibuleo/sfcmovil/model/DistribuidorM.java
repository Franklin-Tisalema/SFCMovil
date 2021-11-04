package com.chibuleo.sfcmovil.model;

public class DistribuidorM {
    private String usuario;
    private String fecha;
    private Boolean isLocal;
    private Boolean isLinea;

    public DistribuidorM() {
    }

    public DistribuidorM(String usuario, String fecha, Boolean isLocal, Boolean isLinea, String nombreImpresora) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.isLocal = isLocal;
        this.isLinea = isLinea;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Boolean getLocal() {
        return isLocal;
    }

    public void setLocal(Boolean local) {
        isLocal = local;
    }

    public Boolean getLinea() {
        return isLinea;
    }

    public void setLinea(Boolean linea) {
        isLinea = linea;
    }

}