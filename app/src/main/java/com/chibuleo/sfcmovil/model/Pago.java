package com.chibuleo.sfcmovil.model;

public class Pago {
    private String tipoPagoInteres;
    private String monto;
    private String plazo;
    private String tasa;
    private String sobretasa;
    private String interes;
    private String retencion;
    private String netoARecibir;
    private String esIFI;

    public Pago() {
        monto ="";
        plazo="";
        tasa="";
        sobretasa="";
        tasa="";
        netoARecibir="";
        retencion="";
        interes="";
        tipoPagoInteres="001";
    }

    public Pago(String tipoPagoInteres, String monto, String plazo, String tasa, String sobretasa,
                String interes, String retencion, String netoARecibir, String esIFI) {
        this.tipoPagoInteres = tipoPagoInteres;
        this.monto = monto;
        this.plazo = plazo;
        this.tasa = tasa;
        this.sobretasa = sobretasa;
        this.interes = interes;
        this.retencion = retencion;
        this.netoARecibir = netoARecibir;
        this.esIFI = esIFI;
    }

    public String getTipoPagoInteres() {
        return tipoPagoInteres;
    }

    public void setTipoPagoInteres(String tipoPagoInteres) {
        this.tipoPagoInteres = tipoPagoInteres;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getPlazo() {
        return plazo;
    }

    public void setPlazo(String plazo) {
        this.plazo = plazo;
    }

    public String getTasa() {
        return tasa;
    }

    public void setTasa(String tasa) {
        this.tasa = tasa;
    }

    public String getSobretasa() {
        return sobretasa;
    }

    public void setSobretasa(String sobretasa) {
        this.sobretasa = sobretasa;
    }

    public String getInteres() {
        return interes;
    }

    public void setInteres(String interes) {
        this.interes = interes;
    }

    public String getRetencion() {
        return retencion;
    }

    public void setRetencion(String retencion) {
        this.retencion = retencion;
    }

    public String getNetoARecibir() {
        return netoARecibir;
    }

    public void setNetoARecibir(String netoARecibir) {
        this.netoARecibir = netoARecibir;
    }

    public String getEsIFI() {
        return esIFI;
    }

    public void setEsIFI(String esIFI) {
        this.esIFI = esIFI;
    }
    public class PagoL {
        private Float montoL;
        private Float tasaL;
        private Float plazoL;
        private Float sobreTasaL;
        private Float interesL;
        private Float retencionL;
        private Float netoARecibirL;
        private Float porcentajeRetiene;
        private boolean seRetiene;

        public PagoL() {
        }

        public PagoL(Float montoL, Float tasaL, Float plazoL, Float sobreTasaL,
                     Float interesL, Float retencionL, Float netoARecibirL, Float porcentajeRetiene,
                     boolean seRetiene) {
            this.montoL = montoL;
            this.tasaL = tasaL;
            this.plazoL = plazoL;
            this.sobreTasaL = sobreTasaL;
            this.interesL = interesL;
            this.retencionL = retencionL;
            this.netoARecibirL = netoARecibirL;
            this.porcentajeRetiene = porcentajeRetiene;
            this.seRetiene = seRetiene;
        }

        public Float getMontoL() {
            return montoL;
        }

        public void setMontoL(Float montoL) {
            this.montoL = montoL;
        }

        public Float getTasaL() {
            return tasaL;
        }

        public void setTasaL(Float tasaL) {
            this.tasaL = tasaL;
        }

        public Float getPlazoL() {
            return plazoL;
        }

        public void setPlazoL(Float plazoL) {
            this.plazoL = plazoL;
        }

        public Float getSobreTasaL() {
            return sobreTasaL;
        }

        public void setSobreTasaL(Float sobreTasaL) {
            this.sobreTasaL = sobreTasaL;
        }

        public Float getInteresL() {
            return interesL;
        }

        public void setInteresL(Float interesL) {
            this.interesL = interesL;
        }

        public Float getRetencionL() {
            return retencionL;
        }

        public void setRetencionL(Float retencionL) {
            this.retencionL = retencionL;
        }

        public Float getNetoARecibirL() {
            return netoARecibirL;
        }

        public void setNetoARecibirL(Float netoARecibirL) {
            this.netoARecibirL = netoARecibirL;
        }

        public Float getPorcentajeRetiene() {
            return porcentajeRetiene;
        }

        public void setPorcentajeRetiene(Float porcentajeRetiene) {
            this.porcentajeRetiene = porcentajeRetiene;
        }

        public boolean isSeRetiene() {
            return seRetiene;
        }

        public void setSeRetiene(boolean seRetiene) {
            this.seRetiene = seRetiene;
        }
    }
}
