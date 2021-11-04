package com.chibuleo.sfcmovil.model;

public class ValorxPagar {
    private String numeroCliente;
    private String numeroPagare;
    private String valor;
    private String fechaPago;

    public ValorxPagar() {
    }

    public ValorxPagar(String numeroCliente, String numeroPagare, String valor, String fechaPago) {
        this.numeroCliente = numeroCliente;
        this.numeroPagare = numeroPagare;
        this.valor = valor;
        this.fechaPago = fechaPago;
    }

    public String getNumeroCliente() {
        return numeroCliente;
    }

    public void setNumeroCliente(String numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public String getNumeroPagare() {
        return numeroPagare;
    }

    public void setNumeroPagare(String numeroPagare) {
        this.numeroPagare = numeroPagare;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }
    public class Prestamo {
        private String prestamo;
        private String numeroCliente;
        private String saldoPrestamo;
        private String montoPrestamo;

        public Prestamo() {
        }

        public Prestamo(String prestamo, String numeroCliente, String saldoPrestamo, String montoPrestamo) {
            this.prestamo = prestamo;
            this.numeroCliente = numeroCliente;
            this.saldoPrestamo = saldoPrestamo;
            this.montoPrestamo = montoPrestamo;
        }

        public String getPrestamo() {
            return prestamo;
        }

        public void setPrestamo(String prestamo) {
            this.prestamo = prestamo;
        }

        public String getNumeroCliente() {
            return numeroCliente;
        }

        public void setNumeroCliente(String numeroCliente) {
            this.numeroCliente = numeroCliente;
        }

        public String getSaldoPrestamo() {
            return saldoPrestamo;
        }

        public void setSaldoPrestamo(String saldoPrestamo) {
            this.saldoPrestamo = saldoPrestamo;
        }

        public String getMontoPrestamo() {
            return montoPrestamo;
        }

        public void setMontoPrestamo(String montoPrestamo) {
            this.montoPrestamo = montoPrestamo;
        }
    }
}

