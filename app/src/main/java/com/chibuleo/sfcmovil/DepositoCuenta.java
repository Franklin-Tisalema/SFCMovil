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

public class DepositoCuenta extends AppCompatActivity {

    private TextView lblUsuario;
    private TextView lblFecha;

    private TextView txtNumeroCliente;
    private TextView txtNombre;
    private TextView txtCodigo;
    private TextView txtCuenta;
    private TextView txtEstado;
    private TextView txtSaldo;
    private TextView txtDisponible;
    private EditText txtEfectivo;

    private ImageView logo;

    private Button btnDepositar;
    private Button btnImprimirRecibo;
    private String datosEnvioImpresora;

    Boolean estado;
    String mensaje1;
    String mensaje2;
    String numeroCliente;
    String saldo;
    String disponible;

    ProgressDialog pDialog;
    Utilitarios utilitario;
    Parametros parametro = new Parametros();
    String nombreImpresora;
    String datosRecibo;
    String documento;
    private String usuario;
    private String codigo;
    private String fecha;
    private String efectivo;
    private Boolean esLocal;
    private Boolean enLinea;

    Boolean conexionBTAbierta;

    String[] datosCuenta;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

   /* @Override
    public void onStop()   {
        super.onStop();
        DepositoCuenta.this.finish();
    }

    @Override
    public void onPause()   {
        super.onPause();
        DepositoCuenta.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent consulta = new Intent(DepositoCuenta.this,ConsultaCuenta.class);
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
        setContentView(R.layout.activity_deposito_cuenta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Esto se debe activar si no se desea utilizar AsyncTask
   /*     if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        txtNombre= (TextView) findViewById(R.id.txtNombre);
        txtCodigo= (TextView) findViewById(R.id.txtCodigo);
        txtCuenta= (TextView) findViewById(R.id.txtCuenta);
        txtEstado= (TextView) findViewById(R.id.txtEstado);
        txtSaldo= (TextView) findViewById(R.id.txtSaldo);
        txtDisponible= (TextView) findViewById(R.id.txtDisponible);
        txtEfectivo= (EditText) findViewById(R.id.txtEfectivo);
        txtNumeroCliente = (TextView) findViewById(R.id.txtNumeroCliente);

        btnDepositar =  (Button) findViewById(R.id.btnDepositar);
        btnImprimirRecibo =  (Button) findViewById(R.id.btnImprimirRecibo);
        btnImprimirRecibo.setEnabled(false);

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);

        logo = (ImageView) findViewById(R.id.imageView);

        conexionBTAbierta =false;

        try {
            //Valida caducidad de la sesion
            if(!utilitario.ValidaCaducidadSesion(DepositoCuenta.this))
            {
                utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", DepositoCuenta.this);
                Intent principal = new Intent(DepositoCuenta.this, MainActivity.class);
                finish();
                startActivity(principal);
            }
            utilitario.RegistroUltimoAcceso(DepositoCuenta.this);
            Bundle datos = this.getIntent().getExtras();

            if (datos != null) {
                usuario = datos.getString("usuario");
                codigo = datos.getString("codigo");
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

                parametro.DefinirMetodo("DatosCuentaCliente",esLocal,getApplicationContext());
                pDialog = new ProgressDialog(getApplicationContext());
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(true);
                pDialog.setMax(100);
                pDialog.setProgress(0);

                CargarDatosCuenta tarea = new CargarDatosCuenta();
                tarea.execute();

                pDialog = ProgressDialog.show(DepositoCuenta.this, "Por favor espere", "Cargando Información de la Cuenta", true, false);

                btnDepositar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if (!Utilitarios.EstaVacio(txtEfectivo) && txtEfectivo.getText().toString().indexOf(".") > -1) {

                                parametro.DefinirMetodo("DepositoCuenta",esLocal,getApplicationContext());

                                efectivo = txtEfectivo.getText().toString();

                                pDialog = new ProgressDialog(getApplicationContext());
                                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                pDialog.setCancelable(true);
                                pDialog.setMax(100);
                                pDialog.setProgress(0);

                                DepositarEnCuenta tarea = new DepositarEnCuenta();
                                tarea.execute();
                                pDialog = ProgressDialog.show(DepositoCuenta.this, "Por favor espere", "Procesando Depósito en la Cuenta", true, false);
                            }
                            else {
                                utilitario.MostrarMensaje("Ingrese un valor con dos decimales", DepositoCuenta.this);
                            }
                        } catch (Exception error) {
                            utilitario.MostrarMensaje(error.getMessage(), DepositoCuenta.this);
                        }

                    }
                });

                btnImprimirRecibo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            datosEnvioImpresora="";
                            if(!btnDepositar.isEnabled()) {
                                if (estado) {

                                    parametro.DefinirMetodo("DatosImprimeReciboCuenta",esLocal,getApplicationContext());

                                    pDialog = new ProgressDialog(getApplicationContext());
                                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    pDialog.setCancelable(true);
                                    pDialog.setMax(100);
                                    pDialog.setProgress(0);

                                    ImprimeRecibo tareaImprime = new ImprimeRecibo();
                                    tareaImprime.execute();
                                    pDialog = ProgressDialog.show(DepositoCuenta.this, "Por favor espere", "Imprimiendo...", true, false);
                                } else {
                                    utilitario.MostrarMensaje("No se puede imprimir; hubo un fallo al depositar en la cuenta", DepositoCuenta.this);
                                }
                            }
                        } catch (Exception error) {
                            utilitario.MostrarMensaje(error.getMessage(), DepositoCuenta.this);
                        }
                    }
                });
            } else {
                utilitario.MostrarMensaje("No existen datos", DepositoCuenta.this);
            }
        }
        catch(Exception error)
        {
            utilitario.MostrarMensaje("Error al Cargar Datos de la Cuenta", DepositoCuenta.this);
        }
        

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public Boolean DatosCuentaCliente(){

        Boolean respuesta = false;

        if(enLinea) {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoCuenta", codigo);
            request.addProperty("unCodigoUsuario", usuario);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlCuentas());

            try {
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                int cuantos = resSoap.getPropertyCount();
                datosCuenta = new String[cuantos];
                for (int i = 0; i < cuantos; i++) {
                    datosCuenta[i] = resSoap.getProperty(i).toString();
                }
                //SoapPrimitive res = (SoapPrimitive) resSoap.getProperty(0);
                respuesta = true;
            } catch (Exception error) {
                respuesta = false;
            }
        }
        return respuesta;
    }

    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarDatosCuenta extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = true;
            try
            {
                if(!DatosCuentaCliente()) {
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
                    utilitario.MostrarMensaje("Se produjo un error al obtener datos de la cuenta", DepositoCuenta.this);
                    btnDepositar.setEnabled(false);
                    btnImprimirRecibo.setEnabled(false);
                } else {
                    txtCodigo.setText(datosCuenta[0]);
                    txtNumeroCliente.setText(datosCuenta[1]);
                    txtNombre.setText(datosCuenta[2]);
                    txtCuenta.setText(datosCuenta[3]);
                    txtSaldo.setText(datosCuenta[4]);
                    txtDisponible.setText(datosCuenta[5]);
                    txtEstado.setText(datosCuenta[6]);

                    txtEfectivo.requestFocus();
                }
            }
            catch (Exception error)
            {
                utilitario.MostrarMensaje(error.getMessage(),DepositoCuenta.this);
            }
        }
    }

    public Boolean ProcesaDepositoCuenta(){

        if(enLinea) {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoCuenta",codigo);
            request.addProperty("unValor", efectivo);
            request.addProperty("unCodigoUsuario", usuario);
            request.addProperty("unaFechaSistema", fecha);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlCuentas());

            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                Utilitarios.Respuesta respuesta = new Utilitarios.Respuesta(resSoap);

                estado = respuesta.getEstado();
                mensaje1 = respuesta.getMensaje1();
                mensaje2 = respuesta.getMensaje2();
                documento=mensaje2;
            } catch (Exception e) {
                estado = false;
                mensaje1 = "Error al Realizar el Depósito en la Cuenta";
            }
        }
        else
        {
            mensaje2="";
            estado = true;
        }
        return estado;
    }

    private class DepositarEnCuenta extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = true;
            try
            {
                if(!ProcesaDepositoCuenta()) {
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
                utilitario.MostrarMensaje("Error desconocido al depositar", DepositoCuenta.this);
            }
            else
            {
                if(enLinea) {
                    GuardarDeposito(DepositoCuenta.this, "I");
                }
                else {
                    GuardarDeposito(DepositoCuenta.this, "O");
                }

                btnDepositar.setEnabled(false);
                btnImprimirRecibo.setEnabled(true);
                txtEfectivo.setEnabled(false);
                btnImprimirRecibo.requestFocus();

                utilitario.MostrarMensaje("Depósito realizado con éxito", DepositoCuenta.this);
            }
        }
    }

    public void GuardarDeposito(Context unContexto,String estado) {
         try {

            BaseDatos baseDatos = new BaseDatos(unContexto, BaseDatos.DB_NAME, null, BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getWritableDatabase();

            String elSql = "";

            elSql = "insert into TransaccionesMovil( cliente, nombre, codigo, producto, valor, documento, fecha, estado, tipoTransaccion ) " +
                    " values ('" + String.valueOf(numeroCliente) + "','" +
                    txtNombre.getText() + "','" +
                    txtCodigo.getText() + "','" +
                    txtCuenta.getText() + "','" +
                    txtEfectivo.getText() + "','" +
                    mensaje2 + "','" +
                    fecha + "'," +
                    "'" + estado +"'," +
                    "'V')";
            db.execSQL(elSql);

            db.close();

         } catch (Exception error) {
             utilitario.MostrarMensaje(error.getMessage(), DepositoCuenta.this);
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
           request.addProperty("unCodigoCuenta", codigo);
           request.addProperty("unNumeroDocumento", documento);
           request.addProperty("unCodigoUsuario", usuario);

           SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
           envelope.dotNet = true;

           envelope.setOutputSoapObject(request);

           HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlCuentas());

           try {
               SSLConnection.allowAllSSL();
               transporte.call(parametro.getSoapAction(), envelope);
               SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta


               datosRecibo = "       COAC CHIBULEO LTDA." +
                       "\n\r" +
                       "      DEPOSITO EN AHORROS" +
                       "\n\r" +
                       " NUMERO: " + numeroCliente.toString() +
                       "\n\r" +
                       " ID: " + resSoap.getProperty(1).toString() +
                       "\n\r" +
                       " NOMBRE: " + txtNombre.getText().toString() +
                       "\n\r" +
                       " CUENTA: " + txtCuenta.getText().toString() +
                       "\n\r" +
                       " USUARIO: " + usuario.toUpperCase() +
                       "\n\r" +
                       " DOCUMENTO: " + mensaje2 +
                       "\n\r" +
                       " FECHA: " + fecha + "  " + horaDispositivo + ":" + minutosDispositivo + //Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                       "\n\r" +
                       " VALOR: " + txtEfectivo.getText().toString() + " USD" +
                       "\n\r" +
                       " SALDO: " + resSoap.getProperty(0).toString() + " USD" +
                       "\n\r\n\r\n\r" +
                       "     ----------------------" +
                       "\n\r" +
                       "       FIRMA DEL CLIENTE" +
                       "\n\r\n\r" +
                       "    GRACIAS POR SU DEPOSITO" +
                       "\n\r\n\r\n\r";

               nombreImpresora = resSoap.getProperty(2).toString();

               respuesta = true;
           } catch (Exception error) {
               respuesta = false;
           }
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
                utilitario.MostrarMensaje(mensaje1, DepositoCuenta.this);
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
                            utilitario.MostrarMensaje("Recibo Impreso con Éxito",DepositoCuenta.this);
                        }
                        else
                        {
                            utilitario.MostrarMensaje("Transacción Exitosa sin Impresión de Recibo",DepositoCuenta.this);
                        }
                    } catch (IOException e) {
                        utilitario.MostrarMensaje("Error al Imprimir Recibo " + e.getMessage(),DepositoCuenta.this);
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
            utilitario.MostrarMensaje(e.getMessage(), DepositoCuenta.this);
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
            utilitario.MostrarMensaje("Abrir Conexion " +  e.getMessage(), DepositoCuenta.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Abrir Conexion " + e.getMessage(),DepositoCuenta.this);
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
            utilitario.MostrarMensaje("Begin Data  " + e.getMessage(), DepositoCuenta.this);

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
            utilitario.MostrarMensaje("Cerrar Conexion  " +  e.getMessage(), DepositoCuenta.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Cerrar Conexion " + e.getMessage(),DepositoCuenta.this);

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
            utilitario.MostrarMensaje(e.getMessage(), DepositoCuenta.this);
        }
        return resp;
    }
}
