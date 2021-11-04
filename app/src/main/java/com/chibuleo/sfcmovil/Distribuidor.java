package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;
import com.chibuleo.sfcmovil.model.ValorxPagar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;




public class Distribuidor extends AppCompatActivity {

    private Utilitarios utilitario;
    private ProgressDialog pDialog;
    private Parametros parametro = new Parametros();
    private ImageButton btnPagoPrestamo;
    private ImageButton btnConsultarPagos;
    private ImageButton btnCargarDatos;
    private TextView btnBajarDatos;
    private ImageButton btnConsultarClientes;
    private ImageButton btnConsultarTotales;
    private ImageButton btnDepositoCuenta;
    private ImageButton btnCotizadorDPF;
    private ImageButton btnCotizadorPrestamos;
    private TextView lblUsuario;
    private TextView lblFecha;
    private ImageView logo;
    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private ValorxPagar valxPagar; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    /*
    private String usuario;
    private String fecha;
    private Boolean esLocal;
    private Boolean enLinea;
    private String nombreImpresora;

    private String numeroCliente;
    private String numeroPagare;
    private String valor;
    private String fechaPago;
    */

    int cuantos;
   /* @Override
    public void onPause() {
        super.onPause();
        Distribuidor.this.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        Distribuidor.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent principal = new Intent(Distribuidor.this, MainActivity.class);
        finish();
        startActivity(principal);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribuidor);
        distriModel = new DistribuidorM();
        impresora = new Impresora();
        valxPagar = new ValorxPagar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        logo = (ImageView) findViewById(R.id.imageView);

        btnPagoPrestamo = (ImageButton) findViewById(R.id.btnPagoPrestamo);
        btnDepositoCuenta = (ImageButton) findViewById(R.id.btnDepositoCuenta);
        btnCotizadorDPF = (ImageButton) findViewById(R.id.btnCotizadorDPF);
        btnConsultarPagos = (ImageButton) findViewById(R.id.btnConsultarPagos);
        btnConsultarTotales = (ImageButton) findViewById(R.id.btnConsultarTotales);
        btnCotizadorPrestamos = (ImageButton) findViewById(R.id.btnCotizadorPrestamos);
        //btnCargarDatos = (ImageButton) findViewById(R.id.btnCargarDatos);
        //btnBajarDatos = (TextView) findViewById(R.id.btnBajarDatos);
        //btnConsultarClientes = (ImageButton) findViewById(R.id.btnConsultaClientes);

        Bundle datos = this.getIntent().getExtras();
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(Distribuidor.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", Distribuidor.this);
            Intent principal = new Intent(Distribuidor.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(Distribuidor.this);

        if(datos!=null) {
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));
            if(!distriModel.getLinea()) {
               logo.setImageResource(R.mipmap.logooff);
            }
            else {
                btnDepositoCuenta.setEnabled(true);
                btnPagoPrestamo.setEnabled(true);
            }
            if(distriModel.getLinea() && distriModel.getLocal())
            {
                btnBajarDatos.setEnabled(true);
                btnCargarDatos.setEnabled(true);
            }

            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());

            btnCotizadorDPF.setOnClickListener(new View.OnClickListener() {
                @Override




                public void onClick(View v) {
                    try {
                        //Aqui llama a la otra activity
                        Intent cotizadorDPF = new Intent(Distribuidor.this, CotizadorDPF.class);
                        cotizadorDPF.putExtra("usuario", distriModel.getUsuario());
                        cotizadorDPF.putExtra("fecha", distriModel.getFecha());
                        cotizadorDPF.putExtra("esLocal", distriModel.getLocal());
                        cotizadorDPF.putExtra("enLinea", distriModel.getLinea());
                        cotizadorDPF.putExtra("impresora", impresora.getNombreImpresora());
                        cotizadorDPF.putExtra("numeroCliente", 0);





                        finish();
                        startActivity(cotizadorDPF);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                        btnCotizadorDPF.setEnabled(false);
                    }
                }
            });

            btnPagoPrestamo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Aqui llama a la otra activity
                        Intent prestamo = new Intent(Distribuidor.this, ConsultaPrestamo.class);
                        prestamo.putExtra("usuario", distriModel.getUsuario());
                        prestamo.putExtra("fecha", distriModel.getFecha());
                        prestamo.putExtra("esLocal", distriModel.getLocal());
                        prestamo.putExtra("enLinea", distriModel.getLinea());
                        prestamo.putExtra("impresora", impresora.getNombreImpresora());
                        prestamo.putExtra("numeroCliente", 0);


                        finish();
                        startActivity(prestamo);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                        btnPagoPrestamo.setEnabled(false);
                    }
                }
            });


            btnDepositoCuenta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Aqui llama a la otra activity
                        Intent cuenta = new Intent(Distribuidor.this, ConsultaCuenta.class);
                        cuenta.putExtra("usuario", distriModel.getUsuario());
                        cuenta.putExtra("fecha", distriModel.getFecha());
                        cuenta.putExtra("esLocal", distriModel.getLocal());
                        cuenta.putExtra("enLinea", distriModel.getLinea());
                        cuenta.putExtra("impresora", impresora.getNombreImpresora());
                        cuenta.putExtra("numeroCliente", 0);



                        finish();
                        startActivity(cuenta);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                        btnDepositoCuenta.setEnabled(false);
                    }
                }
            });

             btnConsultarPagos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Aqui llama a la otra activity
                        Intent pagos = new Intent(Distribuidor.this, ConsultaTransacciones.class);
                        pagos.putExtra("usuario", distriModel.getUsuario());
                        pagos.putExtra("fecha", distriModel.getFecha());
                        pagos.putExtra("esLocal", distriModel.getLocal());
                        pagos.putExtra("enLinea",distriModel.getLinea());
                        pagos.putExtra("impresora",impresora.getNombreImpresora());


                        finish();
                        startActivity(pagos);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                        btnConsultarPagos.setEnabled(false);
                    }
                }
            });

            btnConsultarTotales.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Aqui llama a la otra activity
                        Intent pagos = new Intent(Distribuidor.this, ConsultaTotalesPagos.class);
                        pagos.putExtra("usuario", distriModel.getUsuario());
                        pagos.putExtra("fecha", distriModel.getFecha());
                        pagos.putExtra("esLocal", distriModel.getLocal());
                        pagos.putExtra("enLinea",distriModel.getLinea());
                        pagos.putExtra("impresora",impresora.getNombreImpresora());

                        finish();
                        startActivity(pagos);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                        btnConsultarTotales.setEnabled(false);
                    }
                }
            });

            btnConsultarClientes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Aqui llama a la otra activity
                        Intent pagos = new Intent(Distribuidor.this, ConsultaClientes.class);
                        pagos.putExtra("usuario", distriModel.getUsuario());
                        pagos.putExtra("fecha", distriModel.getFecha());
                        pagos.putExtra("esLocal", distriModel.getLocal());
                        pagos.putExtra("enLinea",distriModel.getLinea());
                        pagos.putExtra("impresora",impresora.getNombreImpresora());

                        finish();
                        startActivity(pagos);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                        btnConsultarTotales.setEnabled(false);
                    }




                }
            });
            btnCargarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                try {
                    if (distriModel.getLinea() && distriModel.getLocal()) {

                        pDialog = new ProgressDialog(getApplicationContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.setCancelable(true);
                        pDialog.setMax(100);
                        pDialog.setProgress(0);

                        CargarDatosPrestamosClientes tarea = new CargarDatosPrestamosClientes();
                        tarea.execute();
                        pDialog = ProgressDialog.show(Distribuidor.this, "Por favor espere", "Cargando Prestamos al Movil", true, false);
                    } else {
                        utilitario.MostrarMensaje("Solo se puede realizar la carga En Linea y En Oficina", Distribuidor.this);
                    }
                }
                catch(Exception error){
                    utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                }

                }
            });



            btnBajarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (distriModel.getLinea() && distriModel.getLocal()) {
                            parametro.DefinirMetodo("DescargaCobrosOffLine", distriModel.getLocal(),getApplicationContext());

                            pDialog = new ProgressDialog(getApplicationContext());
                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pDialog.setCancelable(true);
                            pDialog.setMax(100);
                            pDialog.setProgress(0);


                            DescargarDatosPrestamosClientes tarea = new DescargarDatosPrestamosClientes();
                            tarea.execute();
                            pDialog = ProgressDialog.show(Distribuidor.this, "Por favor espere", "Grabando Datos en el SFC", true, false);
                        } else {
                            utilitario.MostrarMensaje("Solo se puede realizar la descarga En Linea y En Oficina", Distribuidor.this);
                        }
                    }
                    catch(Exception error){
                        utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
                    }
                }
            });
        }
        else {
            utilitario.MostrarMensaje("Error al obtener datos", Distribuidor.this);
            btnConsultarPagos.setEnabled(false);
            btnPagoPrestamo.setEnabled(false);
        }
    }

    private Integer DevuelveNumeroRegistros()
    {
        Integer respuesta=0;
        try {
            parametro.DefinirMetodo("CuantosRegistros", distriModel.getLocal(),getApplicationContext());
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoUsuario", distriModel.getUsuario());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

            SSLConnection.allowAllSSL();
            transporte.call(parametro.getSoapAction(), envelope);
            SoapPrimitive resSoap = (SoapPrimitive) envelope.getResponse(); //obtiene respuesta

            respuesta = Integer.parseInt(resSoap.toString());
       }
        catch(Exception error){
            respuesta=0;
        }

        return respuesta;
    }

     private Boolean InsertarDatosPrestamosCientes(){
        Boolean respuesta = false;
         Integer numeroRegistros =0;
         Integer iteraciones =0;
        BaseDatos baseDatos = new BaseDatos(Distribuidor.this, BaseDatos.DB_NAME, null, BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String elSql="";
        if(distriModel.getLinea()) {
            elSql = "select * from TransaccionesMovil";

            Cursor tabla = db.rawQuery(elSql, null);
            if(tabla.getCount()<=0) {
                numeroRegistros = DevuelveNumeroRegistros();
                if(numeroRegistros>0) {

                    if(numeroRegistros<=100)
                    {
                        iteraciones = 1;
                    }
                    else
                    {
                        iteraciones= numeroRegistros/100;
                        if(numeroRegistros%100!=0)
                        {
                            iteraciones = iteraciones+1;
                        }
                    }

                    parametro.DefinirMetodo("ListaDatosPrestamoCliente",
                            distriModel.getLocal(),getApplicationContext());
                    Integer numeroUltimoPagare =0;
                    db.execSQL("delete from DatosPrestamosClientes");
                    for(int j=1; j<=iteraciones; j++) {
                        SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
                        request.addProperty("unCodigoUsuario", distriModel.getUsuario());
                        request.addProperty("unNumeroPagareUltimo", numeroUltimoPagare);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;

                        envelope.setOutputSoapObject(request);

                        HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

                        try {
                            SSLConnection.allowAllSSL();
                            transporte.call(parametro.getSoapAction(), envelope);
                            SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                            cuantos = resSoap.getPropertyCount();

                            if (cuantos > 0) {
                                for (int i = 0; i < cuantos; i++) {
                                    SoapObject obDatoCliente = (SoapObject) resSoap.getProperty(i);

                                    SoapObject obDatoPrestamo = (SoapObject) obDatoCliente.getProperty(0);
                                    //SoapPrimitive res = (SoapPrimitive) obDatoPrestamo.getProperty(0);
                                    String numeroPagare = obDatoPrestamo.getProperty(0).toString();
                                    String numeroCliente = obDatoPrestamo.getProperty(1).toString();
                                    String nombreCliente = obDatoPrestamo.getProperty(2).toString();
                                    String tipoCredito = obDatoPrestamo.getProperty(3).toString();
                                    String codigo = obDatoPrestamo.getProperty(4).toString();
                                    String montoCredito = obDatoPrestamo.getProperty(5).toString();
                                    String saldoPrestamo = obDatoPrestamo.getProperty(6).toString();
                                    String saldoAtrasado = obDatoPrestamo.getProperty(7).toString();
                                    String totalInteres = obDatoPrestamo.getProperty(8).toString();
                                    String totalMora = obDatoPrestamo.getProperty(9).toString();
                                    String totalOtros = obDatoPrestamo.getProperty(10).toString();
                                    String fechaUltimoPago = obDatoPrestamo.getProperty(11).toString();
                                    String valorCuota = obDatoPrestamo.getProperty(12).toString();
                                    String totalAPagar = obDatoPrestamo.getProperty(13).toString();
                                    String valorAlDia = obDatoPrestamo.getProperty(14).toString();
                                    String paraCancelar = obDatoPrestamo.getProperty(15).toString();
                                    String valorProximoPago = obDatoPrestamo.getProperty(16).toString();
                                    String fechaProximoPago = obDatoPrestamo.getProperty(17).toString();
                                    String estado = obDatoPrestamo.getProperty(18).toString();
                                    String numeroTelefono = obDatoCliente.getProperty(1).toString();
                                    String direccion = obDatoCliente.getProperty(2).toString();
                                    String diasMora = obDatoCliente.getProperty(3).toString();
                                    String comentario = obDatoCliente.getProperty(4).toString();
                                    String garante1 = obDatoCliente.getProperty(5).toString();
                                    String garante2 = obDatoCliente.getProperty(6).toString();
                                    String saldoAhorros = obDatoCliente.getProperty(7).toString();


                                    elSql = "insert into DatosPrestamosClientes(numeroPagare, numeroCliente, nombreCliente, tipoCredito, codigo, " +
                                            " montoCredito, saldoPrestamo, saldoAtrasado, totalInteres, totalMora , totalOtros, " +
                                            " fechaUltimoPago, valorCuota, totalAPagar, valorAlDia, paraCancelar, valorProximoPago," +
                                            " fechaProximoPago , estado, numeroTelefono, direccion, diasMora, comentario,garante1,garante2,saldoAhorros) values ('"
                                            + numeroPagare + "','" + numeroCliente + "','" + nombreCliente + "','" + tipoCredito + "','" +
                                            codigo + "','" + montoCredito + "','" + saldoPrestamo + "','" + saldoAtrasado + "','" + totalInteres +
                                            "','" + totalMora + "','" + totalOtros + "','" + fechaUltimoPago + "','" + valorCuota + "','" + totalAPagar +
                                            "','" + valorAlDia + "','" + paraCancelar + "','" + valorProximoPago + "','" + fechaProximoPago + "','" +
                                            estado + "','" + numeroTelefono + "','" + direccion + "','" + diasMora + "','" + comentario +
                                            "','" + garante1 + "','" + garante2 + "','" + saldoAhorros +"')";
                                    db.execSQL(elSql);
                                    numeroUltimoPagare = Integer.parseInt(numeroPagare);
                                }

                                respuesta = true;
                            }
                        } catch (Exception e) {

                            respuesta = false;
                        }
                    }//for de iteraciones
                }
                else //numero de registros
                {
                    respuesta = false;
                }
            }
            else
            {
                respuesta=false;
            }
        }
         db.close();
        if(respuesta)
            cuantos = numeroRegistros;
         else
            cuantos=0;
        return  respuesta;
    }

    private class CargarDatosPrestamosClientes extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = true;
            try {
                if (!InsertarDatosPrestamosCientes()) {
                    respuesta = false;
                }
            } catch (Exception e) {
                respuesta = false;
            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            try {
                if (!result) {
                    utilitario.MostrarMensaje("No se puede cargar datos, No tiene Préstamos asignados o tiene datos pendientes de descargar al SFC", Distribuidor.this);
                } else {
                    utilitario.MostrarMensaje("Carga de Datos Exitosa, " + String.valueOf(cuantos) + " Préstamos cargados", Distribuidor.this);
                }
            } catch (Exception error) {
                utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);

            }
        }
    }

    private class DescargarDatosPrestamosClientes extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = true;
            try {
                if (!EnviarDatosPrestamosCientes()) {
                    respuesta = false;
                }
            } catch (Exception e) {
                respuesta = false;
            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            try {
                if (!result) {
                    utilitario.MostrarMensaje("Error al Descargar Datos al SFC", Distribuidor.this);
                } else {
                    BaseDatos baseDatos = new BaseDatos(Distribuidor.this, BaseDatos.DB_NAME, null, BaseDatos.v_db);
                    SQLiteDatabase db = baseDatos.getWritableDatabase();
                    db.execSQL("delete from TransaccionesMovil");
                    utilitario.MostrarMensaje("Descarga de Datos Exitosa, " + String.valueOf(cuantos) + " Pagos descargados", Distribuidor.this);
                }
            } catch (Exception error) {
                utilitario.MostrarMensaje(error.getMessage(), Distribuidor.this);
            }
        }
    }

    private Boolean EnviarDatosPrestamosCientes(){
        Boolean respuesta = false;
        BaseDatos baseDatos = new BaseDatos(Distribuidor.this, BaseDatos.DB_NAME, null, BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String elSql="";
        if(distriModel.getLinea()) {
            elSql = "select * from TransaccionesMovil where estado ='O' ";

            Cursor tabla = db.rawQuery(elSql, null);
            if(tabla.getCount()>0) {
                tabla = db.rawQuery("select cliente, codigo, valor, fecha from TransaccionesMovil where estado ='O'",null);
                cuantos = tabla.getCount();
                try
                {
                    if (tabla.moveToFirst())
                    {
                        do {

                            valxPagar.setNumeroCliente(tabla.getString(tabla.getColumnIndex("cliente")));
                            valxPagar.setNumeroPagare(tabla.getString(tabla.getColumnIndex("codigo")));
                            valxPagar.setValor(tabla.getString(tabla.getColumnIndex("valor")));
                            valxPagar.setFechaPago(tabla.getString(tabla.getColumnIndex("fecha")));

                            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
                            request.addProperty("unNumeroCliente", valxPagar.getNumeroCliente());
                            request.addProperty("unNumeroPagare", valxPagar.getNumeroPagare());
                            request.addProperty("unaFecha", valxPagar.getFechaPago());
                            request.addProperty("unValor", valxPagar.getValor());
                            request.addProperty("unCodigoUsuario", distriModel.getUsuario());
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;

                            envelope.setOutputSoapObject(request);

                            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());
                            transporte.debug = true;

                            try
                            {
                                SSLConnection.allowAllSSL();
                                transporte.call(parametro.getSoapAction(), envelope);
                                SoapPrimitive resSoap =(SoapPrimitive)envelope.getResponse(); //obtiene respuesta

                                //SoapPrimitive res = (SoapPrimitive) resSoap.getProperty(0);
                                respuesta = Boolean.parseBoolean(resSoap.toString());
                            }
                            catch (Exception e)
                            {
                                respuesta = false;
                            }
                        } while (tabla.moveToNext());
                    }
                }
                catch (Exception ex)
                {
                    respuesta = false;
                }
                db.close();
            }
            else
            {
                respuesta=true;
            }
        }

        return  respuesta;
    }

}
