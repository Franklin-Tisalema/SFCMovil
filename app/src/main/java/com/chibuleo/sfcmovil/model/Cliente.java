package com.chibuleo.sfcmovil.model;

public class Cliente {
    private int numeroCliente;
    private String cedulaCliente;
    private String nombreCliente;
    private String codigoCuenta;

    public Cliente() {
    }

    public Cliente(int numeroCliente, String cedulaCliente, String nombreCliente, String codigoCuenta) {
        this.numeroCliente = numeroCliente;
        this.cedulaCliente = cedulaCliente;
        this.nombreCliente = nombreCliente;
        this.codigoCuenta = codigoCuenta;
    }

    public int getNumeroCliente() {
        return numeroCliente;
    }

    public void setNumeroCliente(int numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }
}
