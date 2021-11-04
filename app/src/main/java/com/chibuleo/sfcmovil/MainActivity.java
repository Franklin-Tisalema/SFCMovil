package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.CredencialLocal;
import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;
import com.chibuleo.sfcmovil.model.Usuario;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    Parametros parametro = new Parametros();
    Utilitarios utilitario;
    /*
    String clave;
    String imei;
    String numeroTelefono;
    String usuario;
    Boolean esLocal;
    Boolean enLinea;
    String nombreImpresora;
    Boolean estado;
    String mensaje1;
    String mensaje2;
    Boolean ingresoConHuella;
    */
    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Usuario usuario; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear

    private EditText txtUsuario;
    private EditText txtContrasena;
    private Button btnIngresar;
    private Button btnIngresarHuella;
    private Button btnSalir;
    private Button btnCambiarServidor;
    private Button btnCambiarTelefono;
    private CheckBox chkEsLocal;
    private CheckBox chkEnLinea;
    private TextView txtActualizarApp;
    private String idAndroid;

     public void onResume()
    {
        super.onResume();
        this.txtContrasena.setText("");
        this.txtUsuario.setText("");
        this.chkEsLocal.setChecked(false);
        this.chkEnLinea.setChecked(true);
    }
    @Override
    public void onPause()   {
        super.onPause();
        this.txtContrasena.setText("");
        this.txtUsuario.setText("");
        this.chkEsLocal.setChecked(false);
        this.chkEnLinea.setChecked(true);
    }

    public void onStop()   {
        super.onStop();
        this.txtContrasena.setText("");
        this.txtUsuario.setText("");
        this.chkEsLocal.setChecked(false);
        this.chkEnLinea.setChecked(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        distriModel = new DistribuidorM();
        usuario = new Usuario();
        impresora = new Impresora();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Esto se debe activar si no se desea utilizar AsyncTask
   /*     if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtContrasena = (EditText) findViewById(R.id.txtContrasena);
        chkEsLocal = (CheckBox) findViewById(R.id.chkEsLocal);
        chkEnLinea = (CheckBox) findViewById(R.id.chkEnLinea);
        txtActualizarApp = (TextView) findViewById(R.id.txtActualizarApp);

        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnIngresarHuella = (Button) findViewById(R.id.btnIngresarHuella);
        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnCambiarServidor = (Button) findViewById(R.id.btnCambiarServidor);
        btnCambiarTelefono = (Button) findViewById(R.id.btnCambiarTelefono);

        txtActualizarApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distriModel.setLocal(chkEsLocal.isChecked());
                String urlActualizacion = "";
                /*if(esLocal)
                    urlActualizacion = "http://192.168.61.11/SFCMovil/SFCMovil.apk";
                else
                    urlActualizacion = "http://190.95.194.26:8086/SFCMovil/SFCMovil.apk";*/

                urlActualizacion = "https://enlinea.chibuleo.com/SFCMovil/SFCMovil.apk";


                Uri adress= Uri.parse(urlActualizacion);
                Intent browser= new Intent(Intent.ACTION_VIEW, adress);
                startActivity(browser);
            }
        });

        chkEnLinea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (chkEnLinea.isChecked()) {
                        chkEsLocal.setEnabled(true);
                    } else {
                        chkEsLocal.setEnabled(false);
                    }
                } catch (Exception error) {
                    utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                }
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finish();
                    System.exit(0);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        btnCambiarServidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                     Intent registroServidor = new Intent(MainActivity.this, RegistroServidorApp.class);
                     startActivity(registroServidor);
                } catch (Exception error) {
                    utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                }

            }
        });

        btnCambiarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent registroTelefono = new Intent(MainActivity.this, RegistroNumeroTelefono.class);
                    startActivity(registroTelefono);
                } catch (Exception error) {
                    utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                }
            }
        });
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (!Utilitarios.EstaVacio(txtUsuario) && !Utilitarios.EstaVacio(txtContrasena)) {
                        String usuarioMaestro = txtUsuario.getText().toString().trim().toUpperCase();
                        String contraseniaMestra = txtContrasena.getText().toString().trim();
                            distriModel.setLocal(chkEsLocal.isChecked());
                            distriModel.setLinea(chkEnLinea.isChecked());
                            usuario.setIngresoConHuella(false);
                            usuario.setClave(EncriptaCadena.getStringMessageDigest(txtUsuario.getText().toString().trim().toUpperCase() + txtContrasena.getText().toString(),
                                    EncriptaCadena.SHA1).toUpperCase());//"A314B7ACC484C44038798F234EA3A37A2A94FE71";
                            distriModel.setUsuario(txtUsuario.getText().toString());
                            if (distriModel.getLinea()) {
                                //parametro.DefinirMetodo("ValidarCredencialesDeAcceso", esLocal);
                                usuario.setNumeroTelefono(""); //utilitario.ObtenerNumeroTelefonico(MainActivity.this);//"084283860";
                                usuario.setImei( utilitario.ObtenerIMEI(MainActivity.this));//"358944062002099";
                                if(usuario.getNumeroTelefono().equals("")) usuario.setNumeroTelefono("0");
                                if (usuario.getNumeroTelefono().trim().length() < 9) {
                                    try {
                                        LeerNumeroTelefono();
                                        if (usuario.getNumeroTelefono().trim().length() == 0)
                                            utilitario.MostrarMensaje("Número de Teléfono no encontrado", MainActivity.this);
                                    } catch (Exception error) {
                                        utilitario.MostrarMensaje("Número de Télefono no encontrado", MainActivity.this);
                                    }
                                }
                                pDialog = new ProgressDialog(MainActivity.this);
                                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                pDialog.setCancelable(true);
                                pDialog.setMax(100);
                                pDialog.setProgress(0);

                                ValidarLogin tarea = new ValidarLogin();
                                tarea.execute();
                                pDialog = ProgressDialog.show(MainActivity.this, "Por favor espere", "Validando Credenciales", true, false);
                            }//En Linea
                            else {
                                CredencialLocal unaCredencial = new CredencialLocal();
                                try {
                                    unaCredencial.LeerCredenciales(MainActivity.this);
                                    if (distriModel.getUsuario().trim().toUpperCase().equals(unaCredencial.getUsuario().toUpperCase().trim()) &&
                                            usuario.getClave().trim().toUpperCase().equals(unaCredencial.getContrasenia().toUpperCase().trim())) {

                                        Intent prestamo = new Intent(MainActivity.this, Distribuidor.class);
                                        prestamo.putExtra("usuario", distriModel.getUsuario());
                                        prestamo.putExtra("fecha", unaCredencial.getFecha().trim());
                                        prestamo.putExtra("esLocal", distriModel.getLocal());
                                        prestamo.putExtra("enLinea", distriModel.getLinea());
                                        prestamo.putExtra("impresora", unaCredencial.getNombreImpresora().trim());
                                        finish();
                                        startActivity(prestamo);
                                    } else {
                                        utilitario.MostrarMensaje("Credenciales de Acceso no Válidas", MainActivity.this);
                                    }

                                } catch (Exception error) {
                                    utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                                }
                            }


                    } else {
                        utilitario.MostrarMensaje("Ingrese los datos solicitados", MainActivity.this);
                    }
                } catch (Exception error) {
                    utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                }
            }
        });

        btnIngresarHuella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                        CredencialLocal unaCredencial = new CredencialLocal();
                        try {
                            distriModel.setLocal(chkEsLocal.isChecked());
                            distriModel.setLinea(chkEnLinea.isChecked());
                            usuario.setIngresoConHuella(true);


                            unaCredencial.LeerCredenciales(MainActivity.this);
                            if (!unaCredencial.getUsuario().toUpperCase().trim().equals("") && !unaCredencial.getContrasenia().toUpperCase().trim().equals("")) {
                                if (distriModel.getLinea()) {
                                    //parametro.DefinirMetodo("ValidarCredencialesDeAcceso", esLocal);
                                    usuario.setNumeroTelefono(""); //utilitario.ObtenerNumeroTelefonico(MainActivity.this);//"084283860";
                                    usuario.setImei(utilitario.ObtenerIMEI(MainActivity.this));//"358944062002099";
                                    distriModel.setUsuario(unaCredencial.getUsuario().toUpperCase().trim());
                                    usuario.setClave(unaCredencial.getContrasenia().toUpperCase().trim());
                                    if(usuario.getNumeroTelefono() == null) usuario.setNumeroTelefono("0");
                                    if (usuario.getNumeroTelefono().trim().length() < 9) {
                                        try {
                                            LeerNumeroTelefono();
                                            if (usuario.getNumeroTelefono().trim().length() == 0)
                                                utilitario.MostrarMensaje("Número de Teléfono no encontrado", MainActivity.this);
                                        } catch (Exception error) {
                                            utilitario.MostrarMensaje("Número de Télefono no encontrado", MainActivity.this);
                                        }
                                    }
                                    pDialog = new ProgressDialog(MainActivity.this);
                                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    pDialog.setCancelable(true);
                                    pDialog.setMax(100);
                                    pDialog.setProgress(0);


                                    ValidarLogin tarea = new ValidarLogin();
                                    tarea.execute();
                                    pDialog = ProgressDialog.show(MainActivity.this, "Por favor espere", "Validando Credenciales", true, false);
                                }//En Linea
                                else {
                                    try {
                                        Intent loginConHuella = new Intent(MainActivity.this, FingerprintActivity.class);
                                        loginConHuella.putExtra("usuario", unaCredencial.getUsuario().toUpperCase().trim());
                                        loginConHuella.putExtra("fecha", unaCredencial.getFecha().trim());
                                        loginConHuella.putExtra("esLocal", distriModel.getLocal());
                                        loginConHuella.putExtra("enLinea", distriModel.getLinea());
                                        loginConHuella.putExtra("impresora", unaCredencial.getNombreImpresora().trim());
                                        finish();
                                        startActivity(loginConHuella);
                                    } catch (Exception error) {
                                        utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                                    }
                                }

                            }
                            else {
                                utilitario.MostrarMensaje("Debe ingresar por primera vez con un usuario y contraseña válidos", MainActivity.this);
                            }

                        } catch (Exception error) {
                            utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                        }
                } catch (Exception error) {
                    utilitario.MostrarMensaje(error.getMessage(), MainActivity.this);
                }

            }
        });
    }

    private void LeerNumeroTelefono()
    {
        BaseDatos baseDatos = new BaseDatos(this.getApplicationContext(),BaseDatos.DB_NAME,null,BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        Cursor tabla = db.rawQuery("select numeroTelefono from Telefono",null);

        usuario.setNumeroTelefono("");
        try
        {
            if (tabla.moveToFirst())
            {
                do {
                    usuario.setNumeroTelefono(tabla.getString(tabla.getColumnIndex("numeroTelefono")));
                } while (tabla.moveToNext());
            }
        }
        catch (Exception ex)
        {
        }
        db.close();
    }

   public int ValidarVersion(){
        Boolean respuestaWS = false;
       int respuesta=0;
        parametro.DefinirMetodo("VersionCorrecta", distriModel.getLocal(), getApplicationContext());
        SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
        request.addProperty("unaVersionApp",parametro.getVersionApp());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlSeguridad());
        transporte.debug = true;

        try
        {
            SSLConnection.allowAllSSL();
            transporte.call(parametro.getSoapAction(), envelope);
            SoapPrimitive resSoap = (SoapPrimitive) envelope.getResponse(); //obtiene respuesta
            respuestaWS = Boolean.parseBoolean(resSoap.toString());
            if(respuestaWS)
                respuesta=0;
            else
                respuesta=1;

        }
        catch (Exception e)
        {
            respuesta=2;
        }

        return respuesta;
    }

    public Boolean ValidarCredenciales(){
        parametro.DefinirMetodo("ValidarCredencialesDeAcceso", distriModel.getLocal(),getApplicationContext());
        SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
        request.addProperty("unNumeroTelefonico",usuario.getNumeroTelefono());
        request.addProperty("unIMEI",usuario.getImei());
        request.addProperty("unCodigoUsuario",distriModel.getUsuario());
        request.addProperty("unaClave",usuario.getClave());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlSeguridad());
        transporte.debug = true;

        try {
            SSLConnection.allowAllSSL();
            transporte.call(parametro.getSoapAction(), envelope);
            SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

            Utilitarios.Respuesta respuesta = new Utilitarios.Respuesta(resSoap);


            usuario.setEstado(respuesta.getEstado());
            usuario.setMensaje1(respuesta.getMensaje1());
            usuario.setMensaje2(respuesta.getMensaje2());
            impresora.setNombreImpresora(respuesta.getMensaje3());

        }
        catch (Exception e)
        {
            usuario.setEstado(false);
            usuario.setMensaje1("Error al Validar Credenciales de Acceso");
        }

        return usuario.getEstado();
    }



    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class ValidarLogin extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            try
            {
                int validaVersion= ValidarVersion();
                if(validaVersion==0) {
                    usuario.setEstado(ValidarCredenciales());
                }
                else
                {
                    if(validaVersion==1) {
                        usuario.setMensaje1("Debe Actualizar la Versión del Aplicativo");
                        usuario.setEstado(false);
                    }
                    else{
                        usuario.setMensaje1("Error de Conexion, verifique su red de datos");
                        usuario.setEstado(false);
                    }
                }
            }
            catch (Exception e)
            {
                usuario.setEstado(false);
                //mensaje1 = "Error al Validar Credenciales de Acceso";

                usuario.setMensaje1("Error de Conexion, verifique su red de datos");
            }

            return usuario.getEstado();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (!result) {
                utilitario.MostrarMensaje(usuario.getMensaje1(), MainActivity.this);
            }
            else
            {

                CredencialLocal unaCredencial = new CredencialLocal(distriModel.getUsuario(),usuario.getClave(),usuario.getMensaje2(),
                        impresora.getNombreImpresora());
                try
                {
                    unaCredencial.GuardarCredencial(MainActivity.this);

                }
                catch (Exception error)
                {
                    utilitario.MostrarMensaje("Error al guardar credenciales locales", MainActivity.this);
                }
                //Aqui llama a la otra activity
                //txtUsuario.setText("");
                //txtContrasena.setText("");
                utilitario.RegistroUltimoAcceso(MainActivity.this);
                if(usuario.getIngresoConHuella()) {
                    Intent loginConHuella = new Intent(MainActivity.this, FingerprintActivity.class);
                    loginConHuella.putExtra("usuario", impresora.getNombreImpresora());
                    loginConHuella.putExtra("fecha", usuario.getMensaje2());
                    loginConHuella.putExtra("esLocal", distriModel.getLocal());
                    loginConHuella.putExtra("enLinea", distriModel.getLinea());
                    loginConHuella.putExtra("impresora", impresora.getNombreImpresora());
                    finish();
                    startActivity(loginConHuella);
                }
                else
                {
                    Intent prestamo = new Intent(MainActivity.this, Distribuidor.class);
                    prestamo.putExtra("usuario", distriModel.getUsuario());
                    prestamo.putExtra("fecha", usuario.getMensaje2());
                    prestamo.putExtra("esLocal", distriModel.getLocal());
                    prestamo.putExtra("enLinea", distriModel.getLinea());
                    prestamo.putExtra("impresora", impresora.getNombreImpresora());
                    finish();
                    startActivity(prestamo);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_obtener_id) {
            txtUsuario.setText("");
            txtContrasena.setText("");
            txtUsuario.requestFocus();
        }
        if (id == R.id.action_acerca) {
            utilitario.MostrarMensaje("Desarrollado Por: Javier Jerez"+
                    "\n"+
                    "Cooperativa de Ahorro y Credito Chibuleo Ltda.", MainActivity.this);
        }
        if (id == R.id.action_obtener_id) {
            idAndroid = utilitario.ObtenerIMEI(MainActivity.this);
            utilitario.MostrarMensaje(idAndroid, MainActivity.this);

        }

        return super.onOptionsItemSelected(item);
    }
}