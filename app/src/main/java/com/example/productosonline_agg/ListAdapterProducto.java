package com.example.productosonline_agg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListAdapterProducto extends RecyclerView.Adapter<ListAdapterProducto.ProductoViewHolder>{

    private List<Producto> productoList;
    private Context contextActivity;

    private String fileUrl = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/images/productId_";


    /**
     * Constructor del adaptador en el que seteamos el conjunto de productos y el contexto (Activity) sobre
     * el que representaremos.
     *
     * @param listaProductos
     * @param contextActivity
     */
    public ListAdapterProducto(List<Producto> listaProductos, Context contextActivity){
        this.productoList =listaProductos;
        this.contextActivity = contextActivity;
    }

    /**
     * Este método devuelve el número de items que tiene la lista de productos
     */
    @Override
    public int getItemCount(){
        return productoList.size();
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
    public ListAdapterProducto.ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_adapter_producto, parent, false);
        return new ListAdapterProducto.ProductoViewHolder(view);
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
    public void onBindViewHolder(ListAdapterProducto.ProductoViewHolder holder, int position){
        Producto productoActual = productoList.get(position);
        holder.bindData(productoActual);
        if(InicioActivity.mPreferences.getBoolean("productosSeleccionable", false)){
            if(ListaProductosActivity.numeroProductos.contains(productoActual)){
                holder.esSeleccionado=true;
                holder.cardView.setBackgroundResource(R.color.green);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.esSeleccionado){
                        holder.cardView.setBackgroundResource(R.color.white);
                        holder.esSeleccionado=false;
                        ListaProductosActivity.numeroProductos.remove(productoActual);
                    }else{
                        holder.cardView.setBackgroundResource(R.color.green);
                        holder.esSeleccionado=true;
                        ListaProductosActivity.numeroProductos.add(productoActual);
                    }

                    if(!ListaProductosActivity.numeroProductos.isEmpty()){
                        ListaProductosActivity.botonPedido.setVisibility(View.VISIBLE);
                    }else{
                        ListaProductosActivity.botonPedido.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
    public class ProductoViewHolder extends RecyclerView.ViewHolder{
        TextView nombreProducto, nombreCategoria;
        ImageView fotoProducto;
        RelativeLayout cardView;

        boolean esSeleccionado;

        /**
         * En el constructor vamos a recoger los elementos de la vista y se lo vamos a asignar a las
         * variables establecidas en la clase
         *
         * @param itemView
         */
        ProductoViewHolder(View itemView){
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            nombreCategoria = itemView.findViewById(R.id.nombreCategoria);
            fotoProducto = itemView.findViewById(R.id.iconImageView);
            cardView = itemView.findViewById(R.id.contenidoProductoCv);
            esSeleccionado=false;
        }

        /**
         * Recibe el producto por parámetro y a través de los getters de la clase Alumno asignamos los valores que deben
         * de mostrar los elementos de la vista.
         *
         * @param item
         */
        void bindData(final Producto item){
            try{
                nombreProducto.setText(item.getNombreProducto());
                nombreCategoria.setText(item.getCategoria());
                Glide.with(contextActivity).load(Uri.decode(item.getFileUrl())).error(R.drawable.producto_default).into(fotoProducto);
            }catch(Exception e){
                Log.d("XXXX", e.toString());
            }
        }
    }
}