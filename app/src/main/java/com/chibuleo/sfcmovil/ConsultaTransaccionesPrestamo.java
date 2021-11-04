package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.Cliente;
import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;
import com.chibuleo.sfcmovil.model.Recibo;
import com.chibuleo.sfcmovil.model.ValorxPagar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.UUID;

public class ConsultaTransaccionesPrestamo extends AppCompatActivity {

    ProgressDialog pDialog;
    Parametros parametro = new Parametros();
    Utilitarios utilitario;

    private TextView lblUsuario;
    private TextView lblFecha;
    private TextView txtNombre;
    private TextView txtNumeroCliente;
    private TextView txtPrestamo;
    private TextView txtPagare;

    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Cliente cliente; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Recibo recibo; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Recibo.Transaccion trans; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private ValorxPagar valxPagar; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private ValorxPagar.Prestamo prestamo; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    /*
    private String datosEnvioImpresora;
    private String usuario;
    private String fecha;
    private Boolean esLocal;
    private Boolean enLinea;
    private String documentoTransaccion;
    private String valorTransaccion;
    private String fechaTransaccion;
    private String tipoTransaccion;
    private String oficinaTransaccion;
    private String pagare;
    private String prestamo;
    private String numeroCliente;
    private String saldoPrestamo;
    private String montoPrestamo;
    */

    private ImageView logo;

    private ListView lstvTransaccionesPrestamo;
    ArrayAdapter adaptador;

    private String opcion;

    ArrayList<Utilitarios.TransaccionPrestamo> listaTransaccionesPrestamo = new ArrayList<Utilitarios.TransaccionPrestamo>();
    /*
    String nombreImpresora;
    String datosRecibo;
    String nombreRecibo;
    */
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
    public void onPause()   {
        super.onPause();
        ConsultaTransaccionesPrestamo.this.finish();
    }
    @Override
    public void onStop()   {
        super.onStop();
        ConsultaTransaccionesPrestamo.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(ConsultaTransaccionesPrestamo.this,ConsultaPrestamo.class);
        distribuidor.putExtra("usuario",distriModel.getUsuario());
        distribuidor.putExtra("fecha", distriModel.getFecha());
        distribuidor.putExtra("esLocal", distriModel.getLocal());
        distribuidor.putExtra("enLinea", distriModel.getLinea());
        distribuidor.putExtra("impresora",impresora.getNombreImpresora());
        finish();
        startActivity(distribuidor);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_transacciones_prestamo);
        distriModel = new DistribuidorM();
        impresora = new Impresora();
        cliente = new Cliente();
        recibo = new Recibo();
        trans = recibo.new Transaccion();
        valxPagar = new ValorxPagar();
        prestamo = valxPagar.new Prestamo();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        txtPagare = (TextView) findViewById(R.id.txtPagare);
        txtNombre = (TextView) findViewById(R.id.txtNombre);
        txtNumeroCliente = (TextView) findViewById(R.id.txtNumeroCliente);
        txtPrestamo = (TextView) findViewById(R.id.txtPrestamo);




        lstvTransaccionesPrestamo = (ListView) findViewById(R.id.lstvTransaccionesPrestamo);

        logo = (ImageView) findViewById(R.id.imageView);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(ConsultaTransaccionesPrestamo.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", ConsultaTransaccionesPrestamo.this);
            Intent principal = new Intent(ConsultaTransaccionesPrestamo.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(ConsultaTransaccionesPrestamo.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null){

            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));
            recibo.setNombreRecibo(datos.getString("nombreCliente"));
            valxPagar.setNumeroPagare(datos.getString("pagare"));
            prestamo.setPrestamo(datos.getString("prestamo"));
            cliente.setNumeroCliente(Integer.parseInt(datos.getString("numeroCliente")));
            prestamo.setMontoPrestamo(datos.getString("monto"));
            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());
            txtNombre.setText(recibo.getNombreRecibo());
            txtPagare.setText(valxPagar.getNumeroPagare());
            txtNumeroCliente.setText(cliente.getNumeroCliente());
            txtPrestamo.setText(prestamo.getPrestamo());

            if(!distriModel.getLinea()) {
                logo.setImageResource(R.mipmap.logooff);
            }

                    try {
                        try {
                            adaptador.clear();
                        }
                        catch (Exception error){
                            //utilitario.MostrarMensaje(error.getMessage(),ConsultaPrestamo.this);
                        }

                        parametro.DefinirMetodo("ListaTransaccionesPrestamo",distriModel.getLocal(),getApplicationContext());
                        pDialog = new ProgressDialog(getApplicationContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.setCancelable(true);
                        pDialog.setMax(100);
                        pDialog.setProgress(0);

                        CargarListaTransacciones tarea = new CargarListaTransacciones();
                        tarea.execute();
                        pDialog = ProgressDialog.show(ConsultaTransaccionesPrestamo.this, "Por favor espere", "Cargando Lista de Transacciones", true, false);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaTransaccionesPrestamo.this);
                    }

            lstvTransaccionesPrestamo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Utilitarios.TransaccionPrestamo elegido = (Utilitarios.TransaccionPrestamo) parent.getItemAtPosition(position);

                    trans.setFechaTransaccion(elegido.getFecha());
                    trans.setDocumentoTransaccion(elegido.getDocumento());
                    trans.setValorTransaccion(elegido.getValor());
                    prestamo.setSaldoPrestamo(elegido.getSaldo());
                    distriModel.setUsuario(elegido.getUsuario());

                    trans.setTipoTransaccion(elegido.getTransaccion());
                    trans.setOficinaTransaccion(elegido.getOficina());
                    try {

                        parametro.DefinirMetodo("DatosImprimeReciboPrestamo",distriModel.getLocal(),getApplicationContext());
                        pDialog = new ProgressDialog(getApplicationContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.setCancelable(true);
                        pDialog.setMax(100);
                        pDialog.setProgress(0);

                        ImprimeRecibo tareaImprime = new ImprimeRecibo();
                        tareaImprime.execute();
                        pDialog = ProgressDialog.show(ConsultaTransaccionesPrestamo.this, "Por favor espere", "Imprimiendo...", true, false);
                     } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaTransaccionesPrestamo.this);
                    }


                }
            });
        }
        else {
            utilitario.MostrarMensaje("No existen datos", getApplicationContext());
        }
     }


    public ArrayList<Utilitarios.TransaccionPrestamo> ListaTransacciones(){

        ArrayList<Utilitarios.TransaccionPrestamo> lista = new ArrayList<Utilitarios.TransaccionPrestamo>();

        Boolean respuesta = false;

        if(distriModel.getLinea()) {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unNumeroPagare", Integer.parseInt(valxPagar.getNumeroPagare()));
            request.addProperty("unCodigoUsuario", distriModel.getUsuario());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

            try {
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                int cuantos = resSoap.getPropertyCount();
                for (int i = 0; i < cuantos; i++) {

                    SoapObject dato = (SoapObject) resSoap.getProperty(i);
                    lista.add( new Utilitarios.TransaccionPrestamo(dato.getProperty(0).toString(),
                            dato.getProperty(1).toString(),
                            dato.getProperty(2).toString(),
                            dato.getProperty(3).toString(),
                            dato.getProperty(4).toString(),
                            dato.getProperty(5).toString(),
                            dato.getProperty(6).toString()));



                }
                //SoapPrimitive res = (SoapPrimitive) resSoap.getProperty(0);
                respuesta = true;
            } catch (Exception error) {
                respuesta = false;
                lista.add(new Utilitarios.TransaccionPrestamo(error.getMessage(),"","","","","",""));
            }
        }
         return lista;
    }

    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarListaTransacciones extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = false;
            try
            {
                if(ListaTransacciones().size()>0) {
                    listaTransaccionesPrestamo = ListaTransacciones();
                    respuesta = true;
                }
                else
                {
                    respuesta=false;
                }
            }
            catch (Exception e)
            {
                respuesta=false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (!result) {
                utilitario.MostrarMensaje("Se produjo un error al obtener el listado de transacciones", ConsultaTransaccionesPrestamo.this);
            }
            else
            {
                if(listaTransaccionesPrestamo.size()>0) {
                    lstvTransaccionesPrestamo = (ListView) findViewById(R.id.lstvTransaccionesPrestamo);
                    adaptador = new ListaTransaccionesPrestamo(ConsultaTransaccionesPrestamo.this, listaTransaccionesPrestamo);
                    lstvTransaccionesPrestamo.setAdapter(adaptador);
                }
                else {
                    utilitario.MostrarMensaje("No existen transacciones registradas",ConsultaTransaccionesPrestamo.this);
                }
            }
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

            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unNumeroPagare", Integer.parseInt(valxPagar.getNumeroPagare()));
            request.addProperty("unCodigoUsuario", distriModel.getUsuario());




            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                recibo.setDatosRecibo("       COAC CHIBULEO LTDA." +
                        "\n\r" +
                        "        PAGO DE PRESTAMO" +
                        "\n\r" +
                        " NOMBRE: " + txtNombre.getText().toString() +
                        "\n\r" +
                        " PAGARE: " + valxPagar.getNumeroPagare() +
                        "\n\r" +
                        " MONTO: " + prestamo.getMontoPrestamo() + " USD" +
                        "\n\r" +
                        " USUARIO: " + distriModel.getUsuario().toUpperCase() +
                        "\n\r" +
                        " DOCUMENTO: " + trans.getDocumentoTransaccion() +
                        "\n\r" +
                        " FECHA: " + trans.getFechaTransaccion()  +//+ Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                        "\n\r" +
                        " P. EFECTIVO: " + trans.getValorTransaccion() + " USD" +
                        "\n\r\n\r" +
                        " FECHA IMPRIME: " + trans.getFechaTransaccion() + " USD" +
                        "\n\r" +
                        " SALDO A LA FECHA: " + resSoap.getProperty(0).toString() + " USD" +
                        "\n\r" +
                        " F. PROX. PAGO: " + resSoap.getProperty(2).toString() +
                        "\n\r" +
                        " V. PROX. PAGO: " + resSoap.getProperty(1).toString() + " USD" +
                        "\n\r\n\r\n\r" +
                        "    COMPROBANTE REIMPRESO" +
                        "\n\r\n\r\n\r");

                //nombreImpresora = resSoap.getProperty(3).toString();

                respuesta = true;
            } catch (Exception error) {
                respuesta = false;
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
                utilitario.MostrarMensaje("Error desconocido al imprimir", ConsultaTransaccionesPrestamo.this);
            }
            else
            {
                // open bluetooth connection
                if(BuscarImpresora(impresora.getNombreImpresora()))
                {
                    try {
                        AbrirConeccionBluetooth();

                        // send data typed by the user to be printed
                        if(ImprimirRecibo(recibo.getDatosRecibo()))
                        {
                            utilitario.MostrarMensaje("Recibo Impreso con Éxito",ConsultaTransaccionesPrestamo.this);
                        }
                        else
                        {
                            utilitario.MostrarMensaje("Transacción Exitosa sin Impresión de Recibo",ConsultaTransaccionesPrestamo.this);
                        }
                    } catch (IOException e) {
                        utilitario.MostrarMensaje("Error al Imprimir Recibo " + e.getMessage(),ConsultaTransaccionesPrestamo.this);
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
            utilitario.MostrarMensaje(e.getMessage(), ConsultaTransaccionesPrestamo.this);
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
            utilitario.MostrarMensaje("Abrir Conexion " +  e.getMessage(), ConsultaTransaccionesPrestamo.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Abrir Conexion " + e.getMessage(),ConsultaTransaccionesPrestamo.this);
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
            utilitario.MostrarMensaje("Begin Data  " + e.getMessage(), ConsultaTransaccionesPrestamo.this);

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
            utilitario.MostrarMensaje("Cerrar Conexion  " +  e.getMessage(), ConsultaTransaccionesPrestamo.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Cerrar Conexion " + e.getMessage(),ConsultaTransaccionesPrestamo.this);

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
            utilitario.MostrarMensaje(e.getMessage(), ConsultaTransaccionesPrestamo.this);
        }
        return resp;
    }





}
