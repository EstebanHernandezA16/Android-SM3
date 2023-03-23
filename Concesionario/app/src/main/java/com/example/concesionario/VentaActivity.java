package com.example.concesionario;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class VentaActivity extends AppCompatActivity {

        ClsOpenHelper admin = new ClsOpenHelper(this,"Concesionario.db",null,1);

        EditText jetmarca, jetplaca, jetnombre,jetidentificacion,jetfecha,jetcodigo;

        String marca, placa, nombre, identificacion, fecha, codigo;

    long respuesta;
        byte sw;
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
        sw=0;
    }

    public void GuardarVenta(View view){
        codigo = jetcodigo.getText().toString();
        marca = jetmarca.getText().toString();
        placa = jetplaca.getText().toString();
        nombre = jetnombre.getText().toString();
        identificacion = jetidentificacion.getText().toString();
        fecha = jetfecha.getText().toString();


        if(marca.isEmpty() || placa.isEmpty() || nombre.isEmpty() || identificacion.isEmpty() || fecha.isEmpty() || codigo.isEmpty()){
            Toast.makeText(this, "Todos los campos de la venta son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();

        }else{
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("codigo",codigo);
            registro.put("fecha",fecha);
            registro.put("Identificacion",identificacion);
            registro.put("Placa",placa);

            if(sw==0){
                respuesta = db.insert("TblVenta",null,registro);
            }else{
                respuesta = db.update("TblVenta",registro,"codigo='"+codigo+"'", null);
            }
            db.close();

        }
    }

    public void ActivarVenta(View view){
        if(sw==0){
        sw=1;
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
            registro.put("Activo","Si");
        respuesta = db.update("TblVenta",registro,"codigo='"+codigo+"'",null);
            if(respuesta!=0){
                Toast.makeText(this, "Activado Exitoso de la venta con codigo "+codigo, Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "No se logró activar el registro", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else{
            Toast.makeText(this, "Debe consultar primero para anular", Toast.LENGTH_SHORT).show();
        }
    }

    public void AnularVenta(View view){
        if(sw==1){
            sw=0;
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("Activo","No");
            respuesta = db.update("TblVenta",registro,"codigo='"+codigo+"'",null);
            if(respuesta!=0){
                Toast.makeText(this, "Anulado Exitoso de la venta con codigo "+codigo, Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "No se logró anular el registro", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else{
            Toast.makeText(this, "Debe consultar primero para anular", Toast.LENGTH_SHORT).show();
        }
    }

    public void RegresarVenta(View view){//view view es para conectar con la vista
        Intent intmain = new Intent(this, MainActivity.class);
        startActivity(intmain);
    }
    public void Cancelar(View view){
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
        sw=0;
        jetmarca.requestFocus();
    }





}