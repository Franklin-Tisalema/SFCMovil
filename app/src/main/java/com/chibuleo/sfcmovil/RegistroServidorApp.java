package com.chibuleo.sfcmovil;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistroServidorApp extends AppCompatActivity
{

    private EditText txtServidorAppInterno;
    private EditText txtServidorAppExterno;
    private Button btnRegistrar;

    Utilitarios utilitario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_servidor_app);

        this.txtServidorAppInterno = (EditText) findViewById(R.id.txtServidorAppLocal);
        this.txtServidorAppExterno = (EditText) findViewById(R.id.txtServidorAppExterno);
        this.btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegistroServidor())
                {
                    utilitario.MostrarMensaje("Servidor Registrado con Exito", RegistroServidorApp.this);
                    RegistroServidorApp.this.finish();
                }
                else {
                    utilitario.MostrarMensaje("Error al Registrar Servidor", RegistroServidorApp.this);
                }
            }
        });
    }

    public Boolean RegistroServidor() {
        Boolean respuesta = false;
        try {

            if (!utilitario.EstaVacio(txtServidorAppInterno)&& !utilitario.EstaVacio(txtServidorAppExterno)) {

                if (txtServidorAppInterno.length() >= 8 && txtServidorAppExterno.length() >= 8) {

                    String elServidorInterno = txtServidorAppInterno.getText().toString();
                    String elServidorExterno = txtServidorAppExterno.getText().toString();

                    BaseDatos baseDatos = new BaseDatos(RegistroServidorApp.this, BaseDatos.DB_NAME, null, BaseDatos.v_db);
                    SQLiteDatabase db = baseDatos.getWritableDatabase();

                    db.execSQL("delete from ServidorApp");

                    String elSql = "insert into ServidorApp(servidorInterno, servidorExterno) values ('" + elServidorInterno + "','" + elServidorExterno + "')";
                    db.execSQL(elSql);
                    db.close();

                    txtServidorAppInterno.setText("");
                    txtServidorAppExterno.setText("");

                    respuesta = true;

                } else {
                    utilitario.MostrarMensaje("URL de Servidor Incorrecta", RegistroServidorApp.this);
                }
            } else {
                utilitario.MostrarMensaje("URL de Servidor Incorrecta", RegistroServidorApp.this);
            }

        } catch (Exception error) {
            utilitario.MostrarMensaje(error.getMessage(), RegistroServidorApp.this);
        }
        return respuesta;
    }
}
