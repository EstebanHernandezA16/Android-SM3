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

public class ClientesActivity extends AppCompatActivity {

    ClsOpenHelper admin=new ClsOpenHelper(this,"Concesionario.db",null,1);
    EditText jetidentificacion,jetnombre,jetcorreo;
    CheckBox jcbactivo;
    String identificacion,nombre,correo;
    long respuesta;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        getSupportActionBar().hide();
        jetidentificacion=findViewById(R.id.etidentificacion);
        jetnombre=findViewById(R.id.etnombre);
        jetcorreo=findViewById(R.id.etcorreo);
        jcbactivo=findViewById(R.id.cbactivo);
        jetidentificacion.requestFocus();
        sw=0;
    }

    public void Guardar(View view){
        identificacion=jetidentificacion.getText().toString();
        nombre=jetnombre.getText().toString();
        correo=jetcorreo.getText().toString();
        if (identificacion.isEmpty() || nombre.isEmpty() || correo.isEmpty()){
            Toast.makeText(this, "Los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        }else{
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("Identificacion",identificacion);
            registro.put("nombre",nombre);
            registro.put("correo",correo);
            if (sw == 0)
                respuesta=db.insert("TblCliente",null,registro);
            else {
                respuesta = db.update("TblCliente", registro, "identificacion='" + identificacion + "'", null);
                sw=0;
            }
            if (respuesta > 0){
                Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            }else{
                Toast.makeText(this, "Error guardando registro", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }//Metodo guardar

    public void Consultar(View view)    {
        identificacion=jetidentificacion.getText().toString();
        if (identificacion.isEmpty()){
            Toast.makeText(this, "Identificacion requerida para consultar", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        }else{
            SQLiteDatabase db=admin.getReadableDatabase();
            Cursor fila=db.rawQuery("select * from TblCliente where identificacion='"+identificacion+"'",null);
            if (fila.moveToNext()){
                sw=1;
                jetnombre.setText(fila.getString(1));
                jetcorreo.setText(fila.getString(2));
                if (fila.getString(3).equals("Si"))
                    jcbactivo.setChecked(true);
                else
                    jcbactivo.setChecked(false);
            }else{
                Toast.makeText(this, "Registro no existe", Toast.LENGTH_SHORT).show();
            }


            db.close();
        }
    }//end consultar
//modificar mensaje que diga que está anulado y si lo está no lo muestre
public void Activar(View view){
        //mismo proceso que anular salvo que alterando el switch
        if(sw==0){
            sw=1;
        SQLiteDatabase db = admin.getWritableDatabase();//abrir conexion en modo escritura
            ContentValues registro = new ContentValues();//objeto que sirve de contenedor, recordar que array para el mismo tipo, objeto para diferente tipo de dato
            registro.put("Activo","Si");
            respuesta= db.update("TblCliente",registro,"identificacion='"+identificacion+"'", null);
            //ahora verificar lo arrojado por respuesta 0=no, el resto si
            if(respuesta!=0){
                Toast.makeText(this, "Activado Exitoso", Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            }else{
                Toast.makeText(this, "No se pudo activar", Toast.LENGTH_SHORT).show();
            }
            db.close();

    }
}
    public void Anular(View view) {
        //la diferencia entre anular y eliminar es que en bd relacionales si se elimina un registro de usuario se eliminan tambien las facturas,ventas etc, relacionadas con el, mientras que en anular simplemente se coloca inactivo conservando
        if (sw == 1){
            sw=0;
         SQLiteDatabase db=admin.getWritableDatabase();//conexion en modo escritura con el writable, mayor info repasar proyecto
            ContentValues registro= new ContentValues();
            registro.put("activo","No");
            respuesta= db.update("TblCliente",registro,"identificacion='"+identificacion+"'", null);
            if(respuesta>0){//verificar reemplazar !=0
                Toast.makeText(this,"Anulado Exitoso",Toast.LENGTH_SHORT).show();//Toast parece el mensaje que se le puede enviar desde el back
                Limpiar_campos();
            }else{
                Toast.makeText(this,"No se pudo anular",Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else{
            Toast.makeText(this,"Debe primero consultar para anular",Toast.LENGTH_SHORT);
        }
    }


    public void cancelar(View view){
        //llamar a metodos de limpiar campos limpiar_campos();
        Limpiar_campos();
    }
    public void Regresar(View view){//view view es para conectar con la vista
        Intent intmain = new Intent(this, MainActivity.class);
        startActivity(intmain);
    }
    private void Limpiar_campos(){
        jetcorreo.setText("");
        jetidentificacion.setText("");
        jetnombre.setText("");
        jcbactivo.setChecked(false);
        jetidentificacion.requestFocus();
        sw=0;
    }


}


