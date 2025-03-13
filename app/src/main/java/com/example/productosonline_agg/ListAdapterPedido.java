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
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListAdapterPedido extends RecyclerView.Adapter<ListAdapterPedido.PedidoViewHolder> {


    private List<Pedido> pedidoList;
    private Context contextActivity;



    /**
     * Constructor del adaptador en el que seteamos el conjunto de pedidos y el contexto (Activity) sobre
     * el que representaremos.
     *
     * @param listaPedidos
     * @param contextActivity
     */
    public ListAdapterPedido(List<Pedido> listaPedidos, Context contextActivity){
        this.pedidoList =listaPedidos;
        this.contextActivity = contextActivity;
    }

    /**
     * Este método devuelve el número de items que tiene la lista de pedidos
     */
    @Override
    public int getItemCount(){
        return pedidoList.size();
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
    public ListAdapterPedido.PedidoViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_adapter_pedido, parent, false);
        return new ListAdapterPedido.PedidoViewHolder(view);
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
    public void onBindViewHolder(ListAdapterPedido.PedidoViewHolder holder, int position){
        int posicion = position;
        Pedido pedidoActual = pedidoList.get(position);
        holder.bindData(pedidoActual);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idPedido = pedidoActual.getPedidoId();
                Intent cambiarAct = new Intent (contextActivity, DetallePedidoActivity.class );
                cambiarAct.putExtra("productId", posicion);
                cambiarAct.putExtra("activityPedidos", true);
                contextActivity.startActivity(cambiarAct);
            }
        });
    }



    public class PedidoViewHolder extends RecyclerView.ViewHolder{
        TextView pedidoId, nombreTienda, estadoPedido, numeroProductos;
        ImageView pedidoIcon;

        /**
         * En el constructor vamos a recoger los elementos de la vista y se lo vamos a asignar a las
         * variables establecidas en la clase
         *
         * @param itemView
         */
        PedidoViewHolder(View itemView){
            super(itemView);
            pedidoId = itemView.findViewById(R.id.pedidoIdTextView);
            nombreTienda = itemView.findViewById(R.id.nombreTiendaTextView);
            estadoPedido = itemView.findViewById(R.id.estadoPedidoTextView);
            numeroProductos = itemView.findViewById(R.id.numeroProductosTextView);
            pedidoIcon = itemView.findViewById(R.id.iconoPedido);
        }

        /**
         * Recibe el pedido por parámetro y a través de los getters de la clase Alumno asignamos los valores que deben
         * de mostrar los elementos de la vista.
         *
         * @param item
         */
        void bindData(final Pedido item){
            pedidoId.setText("Pedido nº" + String.valueOf(item.getPedidoId()));
            nombreTienda.setText(item.getTiendaAsignada());
            estadoPedido.setText(item.getEmpleadoAsignado());
            numeroProductos.setText("Número de productos: " + String.valueOf(item.getListaProductos().size()));
            Glide.with(contextActivity).load(R.drawable.pedido_default).into(pedidoIcon);
        }
    }
}