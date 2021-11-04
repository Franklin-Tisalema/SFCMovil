package com.chibuleo.sfcmovil;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistroNumeroTelefono extends AppCompatActivity
{

    private EditText txtNumeroTelefono;
    private Button btnRegistrar;

    Utilitarios utilitario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_numero_telefono);

        this.txtNumeroTelefono = (EditText) findViewById(R.id.txtNumeroTelefono);
        this.btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegistroNumero())
                {
                    utilitario.MostrarMensaje("Numero Registrado con Exito", RegistroNumeroTelefono.this);
                    RegistroNumeroTelefono.this.finish();
                }
                else {
                    utilitario.MostrarMensaje("Error al Registrar Numero de Celular", RegistroNumeroTelefono.this);
                }
            }
        });
    }

    public Boolean RegistroNumero() {
        Boolean respuesta = false;
        try {

            if (!utilitario.EstaVacio(txtNumeroTelefono)) {

                if (txtNumeroTelefono.length() == 10) {

                    String elNumero = txtNumeroTelefono.getText().toString();

                    BaseDatos baseDatos = new BaseDatos(RegistroNumeroTelefono.this, BaseDatos.DB_NAME, null, BaseDatos.v_db);
                    SQLiteDatabase db = baseDatos.getWritableDatabase();

                    db.execSQL("delete from Telefono");

                    String elSql = "insert into Telefono(numeroTelefono) values ('" + elNumero + "')";
                    db.execSQL(elSql);
                    db.close();

                    txtNumeroTelefono.setText("");

                    respuesta = true;

                } else {
                    utilitario.MostrarMensaje("Número de Teléfono debe contener 10 dígitos", RegistroNumeroTelefono.this);
                }
            } else {
                utilitario.MostrarMensaje("Ingrese un Número de Teléfono con 10 dígitos", RegistroNumeroTelefono.this);
            }

        } catch (Exception error) {
            utilitario.MostrarMensaje(error.getMessage(), RegistroNumeroTelefono.this);
        }
        return respuesta;
    }
}
