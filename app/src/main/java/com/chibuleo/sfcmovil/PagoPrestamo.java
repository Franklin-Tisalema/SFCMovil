package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.UUID;

public class PagoPrestamo extends AppCompatActivity {

    private TextView lblUsuario;
    private TextView lblFecha;

    private TextView txtNombre;
    private TextView txtPagare;
    private TextView txtPrestamo;
    private TextView txtEstado;
    private TextView txtMonto;
    private TextView txtSaldo;
    private TextView txtSaldoAtrasado;
    private TextView txtValorCuota;
    private TextView txtValorInteres;
    private TextView txtValorMora;
    private TextView txtValorOtros;
    private TextView txtFechaProximoPago;
    private TextView txtLiquidar;
    private TextView txtAlDia;
    private TextView txtTotalAPagar;
    private TextView txtValorProximoPago;
    private EditText txtEfectivo;

    private ImageView logo;

    private Button btnAbonar;
    private Button btnImprimirRecibo;

    Boolean estado;
    String mensaje1;
    String mensaje2;
    String numeroCliente;

    ProgressDialog pDialog;
    Utilitarios utilitario;
    Parametros parametro = new Parametros();
    String nombreImpresora;
    String datosRecibo;
    private String usuario;
    private String pagare;
    private String fecha;
    private String efectivo;
    private Boolean esLocal;
    private Boolean enLinea;
    String[] datosPrestamo;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    private String datosEnvioImpresora;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    /*@Override
    public void onStop()   {
        super.onStop();
        PagoPrestamo.this.finish();
    }

    @Override
    public void onPause()   {
        super.onPause();
        PagoPrestamo.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent consulta = new Intent(PagoPrestamo.this,ConsultaPrestamo.class);
        consulta.putExtra("usuario",usuario);
        consulta.putExtra("fecha", fecha);
        consulta.putExtra("esLocal", esLocal);
        consulta.putExtra("enLinea", enLinea);
        consulta.putExtra("impresora", nombreImpresora);

        finish();
        startActivity(consulta);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_prestamo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Esto se debe activar si no se desea utilizar AsyncTask
   /*     if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        txtNombre= (TextView) findViewById(R.id.txtNombre);
        txtPagare= (TextView) findViewById(R.id.txtPagare);
        txtPrestamo= (TextView) findViewById(R.id.txtPrestamo);
        txtEstado= (TextView) findViewById(R.id.txtEstado);
        txtMonto= (TextView) findViewById(R.id.txtMonto);
        txtSaldo= (TextView) findViewById(R.id.txtSaldo);
        txtSaldoAtrasado= (TextView) findViewById(R.id.txtSaldoAtrasado);
        txtValorCuota= (TextView) findViewById(R.id.txtValorCuota);
        txtValorInteres= (TextView) findViewById(R.id.txtValorInteres);
        txtValorMora= (TextView) findViewById(R.id.txtValorMora);
        txtValorOtros= (TextView) findViewById(R.id.txtValorOtros);
        //txtFechaUltimoPago= (TextView) findViewById(R.id.txtFechaUltimoPago);
        txtFechaProximoPago= (TextView) findViewById(R.id.txtFechaProximoPago);
        txtLiquidar= (TextView) findViewById(R.id.txtLiquidar);
        txtAlDia= (TextView) findViewById(R.id.txtAlDia);
        txtTotalAPagar= (TextView) findViewById(R.id.txtTotalAPagar);
        txtValorProximoPago= (TextView) findViewById(R.id.txtValorProximoPago);
        txtEfectivo= (EditText) findViewById(R.id.txtEfectivo);

        btnAbonar =  (Button) findViewById(R.id.btnAbonar);
        btnImprimirRecibo =  (Button) findViewById(R.id.btnImprimirRecibo);
        btnImprimirRecibo.setEnabled(true);

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);

        logo = (ImageView) findViewById(R.id.imageView);

        try {
            //Valida caducidad de la sesion
            if(!utilitario.ValidaCaducidadSesion(PagoPrestamo.this))
            {
                utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", PagoPrestamo.this);
                Intent principal = new Intent(PagoPrestamo.this, MainActivity.class);
                finish();
                startActivity(principal);
            }
            utilitario.RegistroUltimoAcceso(PagoPrestamo.this);
            Bundle datos = this.getIntent().getExtras();

            if (datos != null) {
                usuario = datos.getString("usuario");
                pagare = datos.getString("pagare");
                fecha = datos.getString("fecha");
                esLocal = datos.getBoolean("esLocal");
                enLinea = datos.getBoolean("enLinea");
                numeroCliente = datos.getString("numeroCliente");
                nombreImpresora = datos.getString("impresora");

                if(!enLinea) {
                    logo.setImageResource(R.mipmap.logooff);
                }

                lblUsuario.setText(usuario.toUpperCase());
                lblFecha.setText(fecha);

                parametro.DefinirMetodo("DatosPrestamoCliente",esLocal,getApplicationContext());
                pDialog = new ProgressDialog(getApplicationContext());
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(true);
                pDialog.setMax(100);
                pDialog.setProgress(0);

                CargarDatosPrestamo tarea = new CargarDatosPrestamo();
                tarea.execute();

                pDialog = ProgressDialog.show(PagoPrestamo.this, "Por favor espere", "Cargando Información del Préstamo", true, false);

                btnAbonar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if (!Utilitarios.EstaVacio(txtEfectivo) && txtEfectivo.getText().toString().indexOf(".") > -1) {

                                parametro.DefinirMetodo("PagoPrestamo",esLocal,getApplicationContext());

                                efectivo = txtEfectivo.getText().toString();

                                pDialog = new ProgressDialog(getApplicationContext());
                                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                pDialog.setCancelable(true);
                                pDialog.setMax(100);
                                pDialog.setProgress(0);

                                PagarPrestamo tarea = new PagarPrestamo();
                                tarea.execute();
                                pDialog = ProgressDialog.show(PagoPrestamo.this, "Por favor espere", "Procesando Pago de Préstamo", true, false);
                            }
                            else {
                                utilitario.MostrarMensaje("Ingrese un valor con dos decimales", PagoPrestamo.this);
                            }
                        } catch (Exception error) {
                            utilitario.MostrarMensaje(error.getMessage(), PagoPrestamo.this);
                        }

                    }
                });

                btnImprimirRecibo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            datosEnvioImpresora="";
                            if(!btnAbonar.isEnabled()) {
                                if (estado) {

                                    parametro.DefinirMetodo("DatosImprimeReciboPrestamo",esLocal,getApplicationContext());

                                    pDialog = new ProgressDialog(getApplicationContext());
                                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    pDialog.setCancelable(true);
                                    pDialog.setMax(100);
                                    pDialog.setProgress(0);

                                    ImprimeRecibo tareaImprime = new ImprimeRecibo();
                                    tareaImprime.execute();
                                    pDialog = ProgressDialog.show(PagoPrestamo.this, "Por favor espere", "Imprimiendo...", true, false);
                                } else {
                                    utilitario.MostrarMensaje("No se puede imprimir; hubo un fallo al pagar el préstamo", PagoPrestamo.this);
                                }
                            }
                        } catch (Exception error) {
                            utilitario.MostrarMensaje(error.getMessage(), PagoPrestamo.this);
                        }
                    }
                });
            } else {
                utilitario.MostrarMensaje("No existen datos", PagoPrestamo.this);
            }
        }
        catch(Exception error)
        {
            utilitario.MostrarMensaje("Error al Cargar Datos del Préstamo", PagoPrestamo.this);
        }
    }

    public Boolean DatosPrestamoCliente(){

        Boolean respuesta = false;

        if(enLinea) {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unNumeroPagare", Integer.parseInt(pagare));
            request.addProperty("unCodigoUsuario", usuario);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

            try {
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                int cuantos = resSoap.getPropertyCount();
                datosPrestamo = new String[cuantos];
                for (int i = 0; i < cuantos; i++) {
                    datosPrestamo[i] = resSoap.getProperty(i).toString();
                }
                //SoapPrimitive res = (SoapPrimitive) resSoap.getProperty(0);
                respuesta = true;
            } catch (Exception error) {
                respuesta = false;
            }
        }
        else
        {
            BaseDatos baseDatos = new BaseDatos(PagoPrestamo.this,BaseDatos.DB_NAME,null,BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getReadableDatabase();

            Cursor tabla = db.rawQuery("select numeroPagare, numeroCliente, nombreCliente, tipoCredito, codigo, " +
                                   "  montoCredito, saldoPrestamo, saldoAtrasado, totalInteres, totalMora , totalOtros, " +
                                   "  fechaUltimoPago, valorCuota, totalAPagar, valorAlDia, paraCancelar, valorProximoPago, " +
                                   "  fechaProximoPago , estado from DatosPrestamosClientes where numeroPagare = '"+ pagare.trim() + "'",null);

            try
            {
                datosPrestamo = new String[19];
                if(tabla.getCount()>0) {
                    if (tabla.moveToFirst()) {
                        do {
                            datosPrestamo[0] = tabla.getString(tabla.getColumnIndex("numeroPagare"));
                            datosPrestamo[1] = tabla.getString(tabla.getColumnIndex("numeroCliente"));
                            datosPrestamo[2] = tabla.getString(tabla.getColumnIndex("nombreCliente"));
                            datosPrestamo[3] = tabla.getString(tabla.getColumnIndex("tipoCredito"));
                            datosPrestamo[4] = tabla.getString(tabla.getColumnIndex("codigo"));
                            datosPrestamo[5] = tabla.getString(tabla.getColumnIndex("montoCredito"));
                            datosPrestamo[6] = tabla.getString(tabla.getColumnIndex("saldoPrestamo"));
                            datosPrestamo[7] = tabla.getString(tabla.getColumnIndex("saldoAtrasado"));
                            datosPrestamo[8] = tabla.getString(tabla.getColumnIndex("totalInteres"));
                            datosPrestamo[9] = tabla.getString(tabla.getColumnIndex("totalMora"));
                            datosPrestamo[10] = tabla.getString(tabla.getColumnIndex("totalOtros"));
                            datosPrestamo[11] = tabla.getString(tabla.getColumnIndex("fechaUltimoPago"));
                            datosPrestamo[12] = tabla.getString(tabla.getColumnIndex("valorCuota"));
                            datosPrestamo[13] = tabla.getString(tabla.getColumnIndex("totalAPagar"));
                            datosPrestamo[14] = tabla.getString(tabla.getColumnIndex("valorAlDia"));
                            datosPrestamo[15] = tabla.getString(tabla.getColumnIndex("paraCancelar"));
                            datosPrestamo[16] = tabla.getString(tabla.getColumnIndex("valorProximoPago"));
                            datosPrestamo[17] = tabla.getString(tabla.getColumnIndex("fechaProximoPago"));
                            datosPrestamo[18] = tabla.getString(tabla.getColumnIndex("estado"));

                        } while (tabla.moveToNext());
                    }
                }
                respuesta = true;
            }
            catch (Exception ex)
            {
                respuesta = false;
            }
            db.close();
        }
        return respuesta;
    }

    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarDatosPrestamo extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = true;
            try
            {
                if(!DatosPrestamoCliente()) {
                    respuesta=false;
                }
            }
            catch (Exception e)
            {
                respuesta=false;
            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            try {
                if (!result) {
                    utilitario.MostrarMensaje("Se produjo un error al obtener datos del préstamo", PagoPrestamo.this);
                    btnAbonar.setEnabled(false);
                    btnImprimirRecibo.setEnabled(false);
                } else {
                    txtNombre.setText(datosPrestamo[2]);
                    txtPagare.setText(datosPrestamo[0]);
                    txtPrestamo.setText(datosPrestamo[3]);
                    txtEstado.setText(datosPrestamo[18]);
                    txtMonto.setText(datosPrestamo[5]);
                    txtSaldo.setText(datosPrestamo[6]);
                    txtSaldoAtrasado.setText(datosPrestamo[7]);
                    txtValorCuota.setText(datosPrestamo[12]);
                    txtValorInteres.setText(datosPrestamo[8]);
                    txtValorMora.setText(datosPrestamo[9]);
                    txtValorOtros.setText(datosPrestamo[10]);
                    //txtFechaUltimoPago.setText(datosPrestamo[11]);
                    txtFechaProximoPago.setText(datosPrestamo[17]);
                    txtLiquidar.setText(datosPrestamo[15]);
                    txtAlDia.setText(datosPrestamo[14]);
                    txtTotalAPagar.setText(datosPrestamo[13]);
                    txtValorProximoPago.setText(datosPrestamo[16]);

                    txtEfectivo.requestFocus();
                }
            }
            catch (Exception error)
            {
                utilitario.MostrarMensaje(error.getMessage(),PagoPrestamo.this);
            }
        }
    }

    public Boolean ProcesaPagoPrestamo(){

        if(enLinea) {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unNumeroPagare", Integer.parseInt(pagare));
            request.addProperty("unValor", efectivo);
            request.addProperty("unCodigoUsuario", usuario);
            request.addProperty("unaFechaSistema", fecha);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                Utilitarios.Respuesta respuesta = new Utilitarios.Respuesta(resSoap);

                estado = respuesta.getEstado();
                mensaje1 = respuesta.getMensaje1();
                mensaje2 = respuesta.getMensaje2();

            } catch (Exception e) {
                estado = false;
                mensaje1 = "Error al Realizar el Pago del Préstamo";
            }
        }
        else
        {
            mensaje2="";
            estado = true;
        }
        return estado;
    }

    private class PagarPrestamo extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = true;
            try
            {
                if(!ProcesaPagoPrestamo()) {
                    respuesta=false;
                }
            }
            catch (Exception e)
            {
                respuesta=false;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (!result) {
                utilitario.MostrarMensaje(mensaje1, PagoPrestamo.this);
            }
            else
            {
                if(enLinea) {
                    GuardarPago(PagoPrestamo.this, "I");
                }
                else {
                    GuardarPago(PagoPrestamo.this, "O");
                }

                btnAbonar.setEnabled(false);
                btnImprimirRecibo.setEnabled(true);
                txtEfectivo.setEnabled(false);
                btnImprimirRecibo.requestFocus();

                utilitario.MostrarMensaje("Pago realizado con éxito", PagoPrestamo.this);
            }
        }
    }

    public void GuardarPago(Context unContexto,String estado) {
         try {

            BaseDatos baseDatos = new BaseDatos(unContexto, BaseDatos.DB_NAME, null, BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getWritableDatabase();

            String elSql = "";

            elSql = "insert into TransaccionesMovil( cliente, nombre, codigo, producto, valor, documento, fecha, estado, tipoTransaccion ) " +
                    " values ('" + String.valueOf(numeroCliente) + "','" +
                    txtNombre.getText() + "','" +
                    txtPagare.getText() + "','" +
                    txtPrestamo.getText() + "','" +
                    txtEfectivo.getText() + "','" +
                    mensaje2 + "','" +
                    fecha + "'," +
                    "'" + estado +"',"+
                    "'P')";
            db.execSQL(elSql);

            db.close();

         } catch (Exception error) {
             utilitario.MostrarMensaje(error.getMessage(), PagoPrestamo.this);
         }
     }

   private Boolean ObtieneDatosRecibo(){
       Boolean respuesta = false;
       Calendar calendario = new GregorianCalendar();

       String horaDispositivo="";
       String minutosDispositivo="";

       horaDispositivo = Integer.toString(calendario.get(Calendar.HOUR_OF_DAY));
       minutosDispositivo = Integer.toString(calendario.get(Calendar.MINUTE));

       if (horaDispositivo.trim().length()<2)
           horaDispositivo="0"+horaDispositivo;

       if (minutosDispositivo.trim().length()<2)
           minutosDispositivo="0"+minutosDispositivo;

       if(enLinea) {
           SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
           request.addProperty("unNumeroPagare", Integer.parseInt(pagare));
           request.addProperty("unCodigoUsuario", usuario);

           SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
           envelope.dotNet = true;

           envelope.setOutputSoapObject(request);

           HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

           try {
               SSLConnection.allowAllSSL();
               transporte.call(parametro.getSoapAction(), envelope);
               SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta


               datosRecibo = "       COAC CHIBULEO LTDA." +
                       "\n\r" +
                       "        PAGO DE PRESTAMO" +
                       "\n\r" +
                       " NOMBRE: " + txtNombre.getText().toString() +
                       "\n\r" +
                       " PAGARE: " + pagare +
                       "\n\r" +
                       " MONTO: " + txtMonto.getText().toString() + " USD" +
                       "\n\r" +
                       " USUARIO: " + usuario.toUpperCase() +
                       "\n\r" +
                       " DOCUMENTO: " + mensaje2 +
                       "\n\r" +
                       " FECHA: " + fecha + "  " + horaDispositivo + ":" + minutosDispositivo +//+ Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                       "\n\r" +
                       " P. EFECTIVO: " + efectivo + " USD" +
                       "\n\r\n\r" +
                       " SALDO ACTUAL: " + resSoap.getProperty(0).toString() + " USD" +
                       "\n\r" +
                       " F. PROX. PAGO: " + resSoap.getProperty(2).toString() +
                       "\n\r" +
                       " V. PROX. PAGO: " + resSoap.getProperty(1).toString() + " USD" +
                       "\n\r\n\r\n\r" +
                       "       GRACIAS POR SU PAGO" +
                       "\n\r\n\r\n\r";

               nombreImpresora = resSoap.getProperty(3).toString();

               respuesta = true;
           } catch (Exception error) {
               respuesta = false;
           }
       }
       else
       {
           datosRecibo = "       COAC CHIBULEO LTDA." +
                   "\n\r" +
                   "        PAGO DE PRESTAMO" +
                   "\n\r" +
                   " NOMBRE: " + txtNombre.getText().toString() +
                   "\n\r" +
                   " PAGARE: " + pagare +
                   "\n\r" +
                   " MONTO: " + txtMonto.getText().toString() + " USD" +
                   "\n\r" +
                   " USUARIO: " + usuario.toUpperCase() +
                   "\n\r" +
                   " DOCUMENTO: " +
                   "\n\r" +
                   " FECHA: " + fecha + "  " + horaDispositivo + ":" + minutosDispositivo + //+ Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                   "\n\r" +
                   " P. EFECTIVO: " + efectivo + " USD" +
                   "\n\r\n\r\n\r" +
                   "       GRACIAS POR SU PAGO" +
                   "\n\r\n\r\n\r";

           respuesta = true;

       }
       return respuesta;
   }

    private class ImprimeRecibo extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean respuesta = true;
            try
            {
                if(!ObtieneDatosRecibo()) {
                    respuesta=false;
                }
            }
            catch (Exception e)
            {
                respuesta=false;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (!result) {
                utilitario.MostrarMensaje(mensaje1, PagoPrestamo.this);
            }
            else
            {
                // open bluetooth connection
                if(BuscarImpresora(nombreImpresora))
                {
                    try {
                        AbrirConeccionBluetooth();

                        // send data typed by the user to be printed
                        if(ImprimirRecibo(datosRecibo))
                        {
                            utilitario.MostrarMensaje("Recibo Impreso con Éxito",PagoPrestamo.this);
                        }
                        else
                        {
                            utilitario.MostrarMensaje("Pago Exitoso sin Impresión de Recibo",PagoPrestamo.this);
                        }
                    } catch (IOException e) {
                        utilitario.MostrarMensaje("Error al Imprimir Recibo " + e.getMessage(),PagoPrestamo.this);
                    }

                    try {
                        CerrarConexion();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }
    // This will find a bluetooth printer device
    public Boolean BuscarImpresora(String unNombreImpresora) {
        Boolean resp = false;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                throw new Exception("Adaptador Bluetooth no Habilitado");
             }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().trim().toUpperCase().equals(unNombreImpresora.trim().toUpperCase())) {
                        mmDevice = device;
                        resp=true;
                        break;
                    }
                }
            }
            if(mmDevice==null)
                throw new Exception("Impresora no Encontrada");
         } catch (Exception e) {
            utilitario.MostrarMensaje(e.getMessage(), PagoPrestamo.this);
            resp=false;

        }
        return resp;
    }

    // Tries to open a connection to the bluetooth printer device
    public void AbrirConeccionBluetooth() throws IOException {
        Boolean resp = false;
        try {
            // Standard SerialPortService ID
            mmOutputStream=null;
            mmInputStream=null;
            if (mmDevice != null) {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();

                mmSocket.connect();
                Thread.sleep(1000);
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                beginListenForData();
                resp = true;
                //("Bluetooth Opened");
            } else {
                resp = false;
            }
        } catch (NullPointerException e) {
            utilitario.MostrarMensaje("Abrir Conexion " +  e.getMessage(), PagoPrestamo.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Abrir Conexion " + e.getMessage(),PagoPrestamo.this);
        }
    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[2048];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                datosEnvioImpresora = data;
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (Exception e) {
            utilitario.MostrarMensaje("Begin Data  " + e.getMessage(), PagoPrestamo.this);

        }
    }

    // Close the connection to bluetooth printer.
    void CerrarConexion() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.flush();
            mmOutputStream.close();
            //mmInputStream.reset();
            mmInputStream.close();
            mmSocket.close();
        } catch (NullPointerException e) {
            utilitario.MostrarMensaje("Cerrar Conexion  " +  e.getMessage(), PagoPrestamo.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Cerrar Conexion " + e.getMessage(),PagoPrestamo.this);

        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    public Boolean ImprimirRecibo(String unDatoRecibo) {
        Boolean resp = false;
        try {

            // the text typed by the user
            String msg = unDatoRecibo;

            mmOutputStream.write(msg.getBytes());
            mmOutputStream.flush();

            // tell the user data were sent
           resp = true;
        } catch (Exception e) {
            resp=false;
            utilitario.MostrarMensaje(e.getMessage(), PagoPrestamo.this);
        }
        return resp;
    }

    // Close the connection to bluetooth printer.

}
