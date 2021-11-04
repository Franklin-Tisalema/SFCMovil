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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.Cliente;
import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;

import java.util.ArrayList;

public class ConsultaClientes extends AppCompatActivity {

    ProgressDialog pDialog;
    Parametros parametro = new Parametros();
    Utilitarios utilitario;

    private TextView lblUsuario;
    private TextView lblFecha;
    private Button btnBuscar;
    private Button btnLimpiar;
    private EditText txtDato;

    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Cliente cliente; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear

    /*
    private String usuario;
    private Boolean esLocal;
    private Boolean enLinea;
    private String  nombreImpresora;
    private String fecha;
    private int numeroCliente;
    private String cedulaCliente;
    private String nombreCliente;
    */

    private ImageView logo;

    private RadioButton rbPorNombre;
    private RadioButton rbPorDiasMora;

    private ListView lstvClientes;
    ArrayAdapter adaptador;
    ViewGroup headerView;

    private String opcion;

    ArrayList<Utilitarios.ClienteLigth> listaClientes = new ArrayList<Utilitarios.ClienteLigth>();
    /*@Override
    public void onPause()   {
        super.onPause();
        ConsultaClientes.this.finish();
    }
    @Override
    public void onStop()   {
        super.onStop();
        ConsultaClientes.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(ConsultaClientes.this,Distribuidor.class);
        distribuidor.putExtra("usuario",distriModel.getUsuario());
        distribuidor.putExtra("fecha", distriModel.getFecha());
        distribuidor.putExtra("esLocal", distriModel.getLocal());
        distribuidor.putExtra("enLinea",distriModel.getLinea());
        distribuidor.putExtra("impresora",impresora.getNombreImpresora());

        finish();
        startActivity(distribuidor);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_clientes);
        cliente = new Cliente();
        impresora = new Impresora();
        distriModel = new DistribuidorM();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        btnBuscar =  (Button) findViewById(R.id.btnBuscar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);

        txtDato = (EditText) findViewById(R.id.txtDato);

        lstvClientes = (ListView) findViewById(R.id.lstvTransacciones);

        logo = (ImageView) findViewById(R.id.imageView);

        rbPorNombre = (RadioButton) findViewById(R.id.rbPorNombre);
        rbPorDiasMora = (RadioButton) findViewById(R.id.rbPorDiasMora);

        lstvClientes = (ListView) findViewById(R.id.lstvClientes);
        headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.lyl_cabecera_clientes, lstvClientes, false);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(ConsultaClientes.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", ConsultaClientes.this);
            Intent principal = new Intent(ConsultaClientes.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(ConsultaClientes.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null){
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));

            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());

            if(!distriModel.getLinea()) {
                logo.setImageResource(R.mipmap.logooff);
            }

            rbPorNombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtDato.setHint("Ej: JUAN");
                  }
            });

            rbPorDiasMora.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtDato.setHint("Ej: 15-30");
                }
            });

            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!utilitario.EstaVacio(txtDato)) {
                        try {
                            try {
                                adaptador.clear();
                                lstvClientes.removeHeaderView(headerView);
                            } catch (Exception error) {
                                //utilitario.MostrarMensaje(error.getMessage(),ConsultaPrestamo.this);
                            }
                            String dato="";
                            if (rbPorNombre.isChecked()) {
                                dato = "%" + txtDato.getText() +"%";
                                opcion = " where nombreCliente like '" + dato.trim().toUpperCase() + "'";
                            }
                            if (rbPorDiasMora.isChecked()) {
                                dato =  txtDato.getText().toString().trim();
                                String[] rangos = dato.toString().split("-");
                                String diaInicio = rangos[0];
                                String diaFin  = rangos[1];
                                opcion = " where CAST(diasMora as INTEGER) >= " + rangos[0] + " and CAST(diasMora as INTEGER) <= " + rangos[1];
                            }

                            pDialog = new ProgressDialog(ConsultaClientes.this);
                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pDialog.setCancelable(true);
                            pDialog.setMax(100);
                            pDialog.setProgress(0);

                            CargarListaClientes tarea = new CargarListaClientes();
                            tarea.execute();
                            pDialog = ProgressDialog.show(ConsultaClientes.this, "Por favor espere", "Cargando Lista de Clientes", true, false);
                        } catch (Exception error) {
                            utilitario.MostrarMensaje(error.getMessage(), ConsultaClientes.this);
                        }
                    }
                    else
                    {
                        utilitario.MostrarMensaje("Ingrese un Dato", ConsultaClientes.this);
                    }
                }
            });

            btnLimpiar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        adaptador.clear();
                        lstvClientes.removeHeaderView(headerView);
                        txtDato.setText("");
                        rbPorNombre.setChecked(true);
                    }
                    catch (Exception error){
                        //adaptador.clear();
                        //lstvClientes.removeHeaderView(headerView);
                        txtDato.setText("");
                        rbPorNombre.setChecked(true);
                        //utilitario.MostrarMensaje(error.getMessage(),ConsultaPrestamo.this);
                    }
                }
            });

            lstvClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utilitarios.ClienteLigth elegido = (Utilitarios.ClienteLigth) parent.getItemAtPosition(position);
                    cliente.setNumeroCliente(Integer.parseInt(elegido.getNumeroCliente()));

                    Intent prestamo = new Intent(ConsultaClientes.this, ConsultaPrestamo.class);
                    prestamo.putExtra("usuario", distriModel.getUsuario());
                    prestamo.putExtra("fecha", distriModel.getFecha());
                    prestamo.putExtra("esLocal", distriModel.getLocal());
                    prestamo.putExtra("enLinea", distriModel.getLinea());
                    prestamo.putExtra("impresora", impresora.getNombreImpresora());
                    prestamo.putExtra("numeroCliente", cliente.getNumeroCliente());

                    finish();
                    startActivity(prestamo);
                }
            });
        }
        else {
            utilitario.MostrarMensaje("No existen datos", getApplicationContext());
        }

    }

    public ArrayList<Utilitarios.ClienteLigth> ListaClientes(){

        ArrayList<Utilitarios.ClienteLigth> lista = new ArrayList<Utilitarios.ClienteLigth>();

        try
        {
            BaseDatos baseDatos = new BaseDatos(ConsultaClientes.this,BaseDatos.DB_NAME,null,BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getReadableDatabase();

            String script = "select numeroCliente, nombreCliente, numeroTelefono, direccion, diasMora, saldoAhorros, garante1, garante2 from DatosPrestamosClientes " + opcion;

            Cursor tabla = db.rawQuery(script,null);
            if (tabla.moveToFirst())
            {
                do {
                    String numeroCliente = tabla.getString(tabla.getColumnIndex("numeroCliente"));
                    String nombreCliente = tabla.getString(tabla.getColumnIndex("nombreCliente"));
                    String numeroTelefono = tabla.getString(tabla.getColumnIndex("numeroTelefono"));
                    String direccion = tabla.getString(tabla.getColumnIndex("direccion"));
                    String diasMora = tabla.getString(tabla.getColumnIndex("diasMora"));
                    String saldoAhorros = tabla.getString(tabla.getColumnIndex("saldoAhorros"));
                    String garante1 = tabla.getString(tabla.getColumnIndex("garante1"));
                    String garante2 = tabla.getString(tabla.getColumnIndex("garante2"));

                    lista.add(new Utilitarios.ClienteLigth(numeroCliente,numeroTelefono,diasMora,nombreCliente,direccion,saldoAhorros,garante1,garante2));
                } while (tabla.moveToNext());
            }
            db.close();
        }
        catch (Exception e)
        {
            lista.add(new Utilitarios.ClienteLigth(e.getMessage(),"","","","","","",""));
        }

        return lista;
    }

    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarListaClientes extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = false;
            try
            {
                if(ListaClientes().size()>0) {
                    listaClientes = ListaClientes();
                    respuesta = true;
                }
                else  respuesta=false;
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
                utilitario.MostrarMensaje("Se produjo un error al obtener el listado de clientes", ConsultaClientes.this);
            }
            else
            {
                if(listaClientes.size()>0) {

                    // Add header view to the ListView
                    lstvClientes.addHeaderView(headerView);
                    adaptador = new ListaCliente(ConsultaClientes.this, listaClientes);
                    lstvClientes.setAdapter(adaptador);
                }
                else {
                    utilitario.MostrarMensaje("No Existen Clientes que Coincidan con la Busqueda ",ConsultaClientes.this);
                }
            }
        }
    }

}
