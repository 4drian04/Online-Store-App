package com.example.productosonline_agg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListAdapterCategoria extends RecyclerView.Adapter<ListAdapterCategoria.CategoriaViewHolder> {

    private List<Categoria> categoriaList;
    private Context contextActivity;


    /**
     * Constructor del adaptador en el que seteamos el conjunto de categoria y el contexto (Activity) sobre
     * el que representaremos.
     *
     * @param listaCategoria
     * @param contextActivity
     */
    public ListAdapterCategoria(List<Categoria> listaCategoria, Context contextActivity){
        this.categoriaList =listaCategoria;
        this.contextActivity = contextActivity;
    }

    /**
     * Este método devuelve el número de items que tiene la lista de categorias
     */
    @Override
    public int getItemCount(){
        return categoriaList.size();
    }

    /**
     * Indicamos que la vista será el xml creado para ello
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @Override
    public ListAdapterCategoria.CategoriaViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_adapter_categoria, parent, false);
        return new ListAdapterCategoria.CategoriaViewHolder(view);
    }

    /**
     * Enlazará los datos de cada uno de los elementos de la lista y asignará el listener de
     * evento
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ListAdapterCategoria.CategoriaViewHolder holder, int position){
        Categoria categoriaActual = categoriaList.get(position);
        holder.bindData(categoriaActual);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idCategoria = categoriaActual.getCategoryId();
                Intent cambiarAct = new Intent (contextActivity, ListaProductosActivity.class );
                SharedPreferences.Editor preferencesEditor =
                        InicioActivity.mPreferences.edit();
                preferencesEditor.putBoolean("productosSeleccionable", true);
                preferencesEditor.apply();
                cambiarAct.putExtra("categoriaId", idCategoria);
                cambiarAct.putExtra("productosSeleccionable", true);
                contextActivity.startActivity(cambiarAct);
            }
        });
    }
    public class CategoriaViewHolder extends RecyclerView.ViewHolder{
        TextView nombreCategoria;

        /**
         * En el constructor vamos a recoger los elementos de la vista y se lo vamos a asignar a las
         * variables establecidas en la clase
         *
         * @param itemView
         */
        CategoriaViewHolder(View itemView){
            super(itemView);
            nombreCategoria = itemView.findViewById(R.id.nombreCategoriaAdapter);
        }

        /**
         * Recibe la categoria por parámetro y a través de los getters de la clase Categoria asignamos los valores que deben
         * de mostrar los elementos de la vista.
         *
         * @param item
         */
        void bindData(final Categoria item){
            nombreCategoria.setText(item.getNombreCategoria());
        }
    }
}