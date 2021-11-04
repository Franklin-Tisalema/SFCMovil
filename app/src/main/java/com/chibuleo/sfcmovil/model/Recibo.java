package com.chibuleo.sfcmovil.model;

public class Recibo {
    private String nombreRecibo;
    private String documentoRecibo;
    private String datosRecibo;
    private String montoRecibo;
    private String codigoRecibo;
    private String estadoPago;
    private String producto;

    public Recibo() {
    }

    public Recibo(String nombreRecibo, String documentoRecibo, String datosRecibo,
                       String montoRecibo, String codigoRecibo, String estadoPago,
                    String producto) {
        this.nombreRecibo = nombreRecibo;
        this.documentoRecibo = documentoRecibo;
        this.datosRecibo = datosRecibo;
        this.montoRecibo = montoRecibo;
        this.codigoRecibo = codigoRecibo;
        this.estadoPago = estadoPago;
        this.producto = producto;
    }

    public String getNombreRecibo() {
        return nombreRecibo;
    }

    public void setNombreRecibo(String nombreRecibo) {
        this.nombreRecibo = nombreRecibo;
    }

    public String getDocumentoRecibo() {
        return documentoRecibo;
    }

    public void setDocumentoRecibo(String documentoRecibo) {
        this.documentoRecibo = documentoRecibo;
    }

    public String getDatosRecibo() {
        return datosRecibo;
    }

    public void setDatosRecibo(String datosRecibo) {
        this.datosRecibo = datosRecibo;
    }

    public String getMontoRecibo() {
        return montoRecibo;
    }

    public void setMontoRecibo(String montoRecibo) {
        this.montoRecibo = montoRecibo;
    }

    public String getCodigoRecibo() {
        return codigoRecibo;
    }

    public void setCodigoRecibo(String codigoRecibo) {
        this.codigoRecibo = codigoRecibo;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }
    public class Transaccion {
        private String fechaTransaccion;
        private String documentoTransaccion;
        private String valorTransaccion;
        private String tipoTransaccion;
        private String oficinaTransaccion;

        public Transaccion() {
        }

        public Transaccion(String fechaTransaccion, String documentoTransaccion, String valorTransaccion,
                           String tipoTransaccion, String oficinaTransaccion) {
            this.fechaTransaccion = fechaTransaccion;
            this.documentoTransaccion = documentoTransaccion;
            this.valorTransaccion = valorTransaccion;
            this.tipoTransaccion = tipoTransaccion;
            this.oficinaTransaccion = oficinaTransaccion;
        }

        public String getFechaTransaccion() {
            return fechaTransaccion;
        }

        public void setFechaTransaccion(String fechaTransaccion) {
            this.fechaTransaccion = fechaTransaccion;
        }

        public String getDocumentoTransaccion() {
            return documentoTransaccion;
        }

        public void setDocumentoTransaccion(String documentoTransaccion) {
            this.documentoTransaccion = documentoTransaccion;
        }

        public String getValorTransaccion() {
            return valorTransaccion;
        }

        public void setValorTransaccion(String valorTransaccion) {
            this.valorTransaccion = valorTransaccion;
        }

        public String getTipoTransaccion() {
            return tipoTransaccion;
        }

        public void setTipoTransaccion(String tipoTransaccion) {
            this.tipoTransaccion = tipoTransaccion;
        }

        public String getOficinaTransaccion() {
            return oficinaTransaccion;
        }

        public void setOficinaTransaccion(String oficinaTransaccion) {
            this.oficinaTransaccion = oficinaTransaccion;
        }
    }
}