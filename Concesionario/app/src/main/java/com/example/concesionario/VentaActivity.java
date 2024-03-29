package com.example.concesionario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class VentaActivity extends AppCompatActivity {

    ClsOpenHelper admin = new ClsOpenHelper(this, "Concesionario.db", null, 1);

    EditText jetmarca, jetplaca, jetnombre, jetidentificacion, jetfecha, jetcodigo;

    String marca, placa, nombre, identificacion, fecha, codigo;

    long respuesta;
    byte ticIdent, ticPlaca;
    CheckBox jcbActivo_venta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        getSupportActionBar().hide();


        jetmarca = findViewById(R.id.etmarca_venta);
        jetplaca = findViewById(R.id.etplaca_venta);
        jetnombre = findViewById(R.id.etnombre_venta);
        jetidentificacion = findViewById(R.id.etIdentificacion_venta);
        jetfecha = findViewById(R.id.etFecha_venta);
        jetcodigo = findViewById(R.id.etCodigo_venta);
        jcbActivo_venta = findViewById(R.id.cbactivo_venta);

    }


    public void BuscarCodigoVenta(View view) {
        //consulta inner join y verificar tics

        if(ticPlaca!=1 && ticIdent==1){
            Toast.makeText(this, "Te falta  Placa", Toast.LENGTH_SHORT).show();
        }
        if(ticIdent!=1 && ticPlaca ==1){
            Toast.makeText(this, "Te falta Identificacion", Toast.LENGTH_SHORT).show();
        }

        if (ticPlaca != 1 && ticIdent != 1) {
            Toast.makeText(this, "Favor consultar la Placa e Identificacion primero antes de consultar una venta", Toast.LENGTH_SHORT).show();



        } else {
            ticPlaca = 0;
            ticIdent = 0;
            codigo = jetcodigo.getText().toString();
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Favor ingresar el codigo", Toast.LENGTH_SHORT).show();
            } else {
                //ahora a armar la consulta con inner

                //armando consulta
                SQLiteDatabase db = admin.getReadableDatabase();
                Cursor fila = db.rawQuery("SELECT TblCliente.Identificacion, TblCliente.nombre," +
                        " TblVehiculo.Placa, TblVehiculo.marca, TblVenta.codigo, TblVenta.fecha,TblVenta.activo" +
                        " FROM TblCliente" +
                        " INNER JOIN TblVenta ON TblCliente.Identificacion = TblVenta.Identificacion " +
                        "INNER JOIN TblVehiculo ON TblVenta.Placa = TblVehiculo.Placa" +
                        " WHERE TblVenta.codigo='" + codigo + "' AND TblCliente.Identificacion='"+identificacion+"' AND TblVehiculo.Placa ='"+placa+"'", null);

                if (fila.moveToFirst()) {
                     if(fila.getString(6).equals("Si")){
                         jetfecha.setText(fila.getString(5));
                         jetidentificacion.setText(fila.getString(0));
                         jetnombre.setText(fila.getString(1));
                         jetplaca.setText(fila.getString(2));
                         jetmarca.setText(fila.getString(3));
                         jetcodigo.setText(fila.getString(4));

                         jcbActivo_venta.setChecked(true);

                         Toast.makeText(this, "Registro encontrado con exito", Toast.LENGTH_SHORT).show();


                 }else{
                         Toast.makeText(this, "Esa Venta no está activada, favor activarla para poder verla", Toast.LENGTH_SHORT).show();
                     }


            }else{
                    Toast.makeText(this, "Ese registro no existe", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }

       }


    }

    public void BuscarIdentVenta(View view) {
        ticIdent = 0;
        identificacion = jetidentificacion.getText().toString();
        if (identificacion.isEmpty()) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        } else {
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select * from TblCliente where identificacion='"+identificacion+"'", null);
            if (fila.moveToNext()) {
                ticIdent = 1;
                if (fila.getString(3).equals("Si")) {
                    jetidentificacion.setText(fila.getString(0));
                    jetnombre.setText(fila.getString(1));
                    nombre = fila.getString(1);
                    //el tercero solo lo necesito para la comparacion
                    Toast.makeText(this, "Hola "+nombre+"", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ese Cliente Está inactivo, favor activarlo", Toast.LENGTH_SHORT).show();

                }


            }else{
                Toast.makeText(this, "Ese cliente no existe", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }


    public void BuscarPlacaVenta(View view) {
        ticPlaca = 0;
        placa = jetplaca.getText().toString();
        if (placa.isEmpty()) {
            Toast.makeText(this, "Favor ingresar la placa del vehiculo", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        } else {

            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select * from TblVehiculo where Placa='"+placa+"'",null);
            if (fila.moveToNext()) {
                ticPlaca = 1;
                if (fila.getString(3).equals("Si")) {
                    jetplaca.setText(fila.getString(0));
                    jetmarca.setText(fila.getString(1));
                    marca=fila.getString(1);
                    Toast.makeText(this, "Placa "+placa+" encontrada vehiculo marca "+marca+"", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "La placa está anulada, favor activarla", Toast.LENGTH_SHORT).show();

                }
            }else{
                Toast.makeText(this, "Esa placa no existe", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }

    }


    public void GuardarVenta(View view) {
        codigo = jetcodigo.getText().toString();
        fecha = jetfecha.getText().toString();
        identificacion = jetidentificacion.getText().toString();
        placa = jetplaca.getText().toString();
        //para poder guardar se debe verificar la consulta
        if (codigo.isEmpty() || fecha.isEmpty() || identificacion.isEmpty() || placa.isEmpty()) {
            Toast.makeText(this, "Favor llenar los campos requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
            jetfecha.requestFocus();
            jetidentificacion.requestFocus();
            jetplaca.requestFocus();
        } else {
            //armar consultas
            SQLiteDatabase dbRead = admin.getReadableDatabase();
            Cursor filaCliente = dbRead.rawQuery("select * from TblCliente where Identificacion='" + identificacion + "'", null);
            Cursor filaVehiculo = dbRead.rawQuery("select * from TblVehiculo where Placa='" + placa + "'", null);
            if (filaCliente.moveToNext() && filaVehiculo.moveToNext()) {
                SQLiteDatabase dbWrite = admin.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("codigo", codigo);
                registro.put("fecha", fecha);
                registro.put("Identificacion", identificacion);
                registro.put("Placa", placa);
                respuesta = dbWrite.insert("TblVenta", null,registro);


                if(respuesta>0){
                    Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();

                }else{
                    Toast.makeText(this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
                }


            }//no encontró los registros
            Toast.makeText(this, "No consultó", Toast.LENGTH_SHORT).show();
            dbRead.close();
        }
    }

    public void ActivarVenta(View view) {

        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("Activo", "Si");
        respuesta = db.update("TblVenta", registro, "codigo='" + codigo + "'", null);
        if (respuesta != 0) {
            Toast.makeText(this, "Activado Exitoso de la venta con codigo " + codigo, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No se logró activar el registro", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }




    public void AnularVenta(View view) {

        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("Activo", "No");
        respuesta = db.update("TblVenta", registro, "codigo='" + codigo + "'", null);
        if (respuesta != 0) {
            Toast.makeText(this, "Anulado Exitoso de la venta con codigo " + codigo, Toast.LENGTH_SHORT).show();
            jcbActivo_venta.setChecked(false);
        } else {
            Toast.makeText(this, "No se logró anular el registro", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }



    public void RegresarVenta(View view){//view view es para conectar con la vista
        Intent intmain = new Intent(this, MainActivity.class);
        startActivity(intmain);
    }
    public void CancelarVenta(View view){
        Limpiar_campos();
    }

    public void Limpiar_campos(){
        jetmarca.setText("");
        jetplaca .setText("");
        jetnombre.setText("");
        jetidentificacion.setText("");
        jetfecha.setText("");
        jetcodigo.setText("");
        jcbActivo_venta.setChecked(false);

        jetmarca.requestFocus();
    }





}