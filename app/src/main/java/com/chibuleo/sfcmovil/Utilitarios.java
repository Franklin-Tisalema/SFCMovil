package com.chibuleo.sfcmovil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;



public class Utilitarios {

    public static boolean EstaVacio(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String ObtenerNumeroTelefonico(Context elContexto ){
        TelephonyManager mTelephonyManager;
        mTelephonyManager = (TelephonyManager) elContexto.getSystemService(elContexto.TELEPHONY_SERVICE);
        return mTelephonyManager.getLine1Number();
    }

   public static String LeerUltimoAcceso(Context contexto)
    {
        BaseDatos baseDatos = new BaseDatos(contexto,BaseDatos.DB_NAME,null,BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String dato="";
        String sentenciaSQL="select fechaHoraUltimoAcceso from ValidaSesion";

        Cursor tabla = db.rawQuery(sentenciaSQL,null);
        try
        {
            if (tabla.moveToFirst())
            {
                do {
                    dato= tabla.getString(tabla.getColumnIndex("fechaHoraUltimoAcceso"));
                } while (tabla.moveToNext());
            }
        }
        catch (Exception ex)
        {
            dato="";
        }
        db.close();
        return dato;
    }

   public static void RegistroUltimoAcceso(Context contexto)
   {
        try
        {
            BaseDatos baseDatos = new BaseDatos(contexto,BaseDatos.DB_NAME,null,BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            Calendar calendario = Calendar.getInstance();
            db.execSQL("delete from ValidaSesion");
            int tiempo = calendario.get(Calendar.HOUR_OF_DAY)+ calendario.get(Calendar.MINUTE) ;//+ calendario.get(Calendar.SECOND); //(int) new Date().getTime();
            String elSql = "insert into ValidaSesion(fechaHoraUltimoAcceso) values (" + String.valueOf(tiempo) + ")";
            db.execSQL(elSql);
            db.close();
        }
        catch (Exception error)
        {
        }
    }

    public static Boolean ValidaCaducidadSesion(Context contexto) {
        Boolean estaActiva=false;
        try {
            String ultimoAcceso= LeerUltimoAcceso(contexto);
            Calendar calendario = Calendar.getInstance();
            int horaActual = calendario.get(Calendar.HOUR_OF_DAY)+ calendario.get(Calendar.MINUTE) ;//+ calendario.get(Calendar.SECOND); //(int) new Date().getTime();
            int diferencia = horaActual- Integer.parseInt(ultimoAcceso);
            if(diferencia<=5)
                estaActiva=true;
        } catch (Exception error) {
            estaActiva=false;
        }
        return  estaActiva;
    }



    /*public static String ObtenerIMEI(Context elContexto){

        TelephonyManager mngr = (TelephonyManager) elContexto.getSystemService(elContexto.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }*/

    public static String ObtenerIMEI(Context elContexto) {

        TelephonyManager mngr = (TelephonyManager) elContexto.getSystemService(elContexto.TELEPHONY_SERVICE);
        String imei = "";
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //Hacemos la validación de métodos, ya que el método getDeviceId() ya no se admite para android Oreo en adelante, debemos usar el método getImei()
            imei = mngr.getImei(0);
        } else {
            imei = mngr.getDeviceId();
        }*/
        imei = Settings.Secure.getString(elContexto.getContentResolver(), Settings.Secure.ANDROID_ID);
        return imei;
    }

    public static void MostrarMensaje(String mensaje,Context elContexto)
    {
        //LayoutInflater inflater =  LayoutInflater.from(elContexto);//  getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) elContexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout,null);//(ViewGroup) findViewById(R.id.toast_layout_root));
        Toast toast = new Toast(elContexto);
        toast.setView(layout);
        TextView text = (TextView) layout.findViewById(R.id.toastText);
        text.setText(mensaje);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

        //Toast.makeText(elContexto, mensaje, Toast.LENGTH_LONG).show();
    }

    public static void MostrarMensajeCorto(String mensaje,Context elContexto)
    {
        //LayoutInflater inflater =  LayoutInflater.from(elContexto);//  getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) elContexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout,null);//(ViewGroup) findViewById(R.id.toast_layout_root));
        Toast toast = new Toast(elContexto);
        toast.setView(layout);
        TextView text = (TextView) layout.findViewById(R.id.toastText);
        text.setText(mensaje);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

        //Toast.makeText(elContexto, mensaje, Toast.LENGTH_LONG).show();
    }


    public static class Respuesta{
        private Boolean estado;
        private String mensaje1;
        private String mensaje2;
        private String mensaje3;

        public Boolean getEstado()
        {
            return this.estado;
        }
        public String getMensaje1() { return this.mensaje1;  }
        public String getMensaje2() { return this.mensaje2;  }
        public String getMensaje3()
        {
            return this.mensaje3;
        }

        public Respuesta(SoapObject objeto)
        {
            SoapPrimitive res = (SoapPrimitive) objeto.getProperty(0);
            this.estado = Boolean.parseBoolean(res.toString());
            this.mensaje1 = objeto.getProperty(1).toString();
            this.mensaje2 = objeto.getProperty(2).toString();
            this.mensaje3 = objeto.getProperty(3).toString();
        }

    }

    public static class DatosComunes {
        private String usuarioComun;
        private String fechaComun;
        private Boolean esLocalComun;
        private Boolean enLineaComun;
        private String nombreImpresoraComun;

        public Boolean getEsLocalComun() {
            return this.esLocalComun;
        }

        public Boolean getEnLineaComun() {
            return this.enLineaComun;
        }

        public String getUsuarioComun() {
            return this.usuarioComun;
        }

        public String getFechaComun() {
            return this.fechaComun;
        }

        public String getNombreImpresoraComun() {
            return this.nombreImpresoraComun;
        }
        public DatosComunes(String usuarioComun,String fechaComun,Boolean esLocalComun,Boolean enLineaComun,String nombreImpresoraComun)
        {
            this.usuarioComun=usuarioComun;
            this.fechaComun=fechaComun;
            this.esLocalComun=esLocalComun;
            this.enLineaComun=enLineaComun;
            this.nombreImpresoraComun = nombreImpresoraComun;
        }
    }


    public static class DatosCotizadorDPF {
        private String interes;
        private String retencion;
        private String netoARecibir;
        private String error;

        public String getInteres() {
            return this.interes;
        }

        public String getRetencion() {
            return this.retencion;
        }

        public String getNetoARecibir() {
            return this.netoARecibir;
        }

        public String getError() {
            return this.error;
        }

        public DatosCotizadorDPF(SoapObject objeto) {
            this.interes = objeto.getProperty(0).toString();
            this.retencion = objeto.getProperty(1).toString();
            this.netoARecibir = objeto.getProperty(2).toString();
            this.error = objeto.getProperty(3).toString();
        }

    }
    public static class PrestamoLigth{
        private String pagare;
        private String tipoPestamo;
        private String monto;
        private String estado;
        private String numeroCliente;

        // public void setPagare(String elPagare) { this.pagare = elPagare; }
        public String getPagare()
        {
            return this.pagare;
        }
        //public void setTipoPrestamo(String elTipoPrestamo) { this.tipoPestamo = elTipoPrestamo; }
        public String getTipoPestamo()
        {
            return this.tipoPestamo;
        }
        //public void setMonto(String elMonto){ this.monto = elMonto; }
        public String getMonto()
        {
            return this.monto;
        }
        //public void setEstado(String elEstado){ this.estado = elEstado; }
        public String getEstado() {return this.estado; }
        public String getNumeroCliente() {return this.numeroCliente; }

        PrestamoLigth(String unPagare, String unTipoPrestamo, String unMonto, String unEstado, String unNumeroCliente){
            this.pagare = unPagare;
            this.tipoPestamo = unTipoPrestamo;
            this.monto = unMonto;
            this.estado = unEstado;
            this.numeroCliente = unNumeroCliente;

        }
    }

    public static class CuentaLigth{
        private String codigo;
        private String tipoCuenta;
        private String saldo;
        private String estado;
        private String numeroCliente;
        private String disponible;

        public String getCodigo()
        {
            return this.codigo;
        }
        public String getTipoCuenta()
        {
            return this.tipoCuenta;
        }
        public String getSaldo()
        {
            return this.saldo;
        }
        public String getEstado() {return this.estado; }
        public String getNumeroCliente() {return this.numeroCliente; }
        public String getDisponible() {return this.disponible; }

        CuentaLigth(String unCodigo, String unTipoCuenta, String unSaldo, String unEstado, String unNumeroCliente,String unDisponible){
            this.codigo = unCodigo;
            this.tipoCuenta = unTipoCuenta;
            this.saldo = unSaldo;
            this.estado = unEstado;
            this.numeroCliente = unNumeroCliente;
            this.disponible=unDisponible;

        }
    }
    public static class TransaccionCuenta{
        private String fecha;
        private String documento;
        private String valor;
        private String saldo;
        private String usuario;
        private String oficina;
        private String transaccion;

        public String getFecha()
        {
            return this.fecha;
        }
        public String getDocumento()
        {
            return this.documento;
        }
        public String getValor()
        {
            return this.valor;
        }
        public String getSaldo() {return this.saldo; }
        public String getUsuario() {return this.usuario; }
        public String getOficina() {return this.oficina; }
        public String getTransaccion() {return this.transaccion; }

        TransaccionCuenta(String unaFecha, String unDocumento, String unValor, String unSaldo, String unUsuario,String unaOficina, String unaTransaccion){
            this.fecha = unaFecha;
            this.documento = unDocumento;
            this.valor = unValor;
            this.saldo = unSaldo;
            this.usuario = unUsuario;
            this.oficina=unaOficina;
            this.transaccion = unaTransaccion;

        }
    }

    public static class TransaccionPrestamo{
        private String fecha;
        private String documento;
        private String valor;
        private String saldo;
        private String usuario;
        private String oficina;
        private String transaccion;

        public String getFecha()
        {
            return this.fecha;
        }
        public String getDocumento()
        {
            return this.documento;
        }
        public String getValor()
        {
            return this.valor;
        }
        public String getSaldo() {return this.saldo; }
        public String getUsuario() {return this.usuario; }
        public String getOficina() {return this.oficina; }
        public String getTransaccion() {return this.transaccion; }

        TransaccionPrestamo(String unaFecha, String unDocumento, String unValor, String unSaldo, String unUsuario,String unaOficina, String unaTransaccion){
            this.fecha = unaFecha;
            this.documento = unDocumento;
            this.valor = unValor;
            this.saldo = unSaldo;
            this.usuario = unUsuario;
            this.oficina=unaOficina;
            this.transaccion = unaTransaccion;

        }
    }

    public static class ClienteLigth{
        private String direccion;
        private String telefono;
        private String diasMora;
        private String nombre;
        private String numeroCliente;
        private String saldoAhorros;
        private String garante1;
        private String garante2;

        public String getDireccion()
        {
            return this.direccion;
        }
        public String getTelefono()
        {
            return this.telefono;
        }
        public String getDiasMora()
        {
            return this.diasMora;
        }
        public String getNombre() {return this.nombre; }
        public String getNumeroCliente() {return this.numeroCliente; }

        public String getSaldoAhorros()
        {
            return this.saldoAhorros;
        }
        public String getGarante1() {return this.garante1; }
        public String getGarante2() {return this.garante2; }

        ClienteLigth(String unNumeroCliente, String unTelefono, String unosDiasMora, String unNombre, String unaDireccion,
                     String unSaldoAhorros, String unGarante1, String unGarante2){
            this.direccion = unaDireccion;
            this.telefono = unTelefono;
            this.nombre = unNombre;
            this.diasMora = unosDiasMora;
            this.numeroCliente = unNumeroCliente;
            this.saldoAhorros = unSaldoAhorros;
            this.garante1 = unGarante1;
            this.garante2 = unGarante2;
        }
    }

}
