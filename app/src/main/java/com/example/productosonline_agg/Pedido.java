package com.example.productosonline_agg;

import java.util.ArrayList;

public class Pedido {

    private int pedidoId;

    private String tiendaAsignada;
    private String empleadoAsignado;

    private Integer tiendaAsignadaId;

    private ArrayList<Integer> listaProductos;

    public Pedido(int pedidoId, String tiendaAsignada, String empleadoAsignado, ArrayList<Integer> listaProductos, Integer tiendaAsignadaId) {
        this.pedidoId = pedidoId;
        this.tiendaAsignada = tiendaAsignada;
        this.empleadoAsignado = empleadoAsignado;
        this.listaProductos = listaProductos;
        this.tiendaAsignadaId=tiendaAsignadaId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public String getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    public ArrayList<Integer> getListaProductos() {
        return listaProductos;
    }

    public String getTiendaAsignada() {
        return tiendaAsignada;
    }

    public void setEmpleadoAsignado(String empleadoAsignado) {
        this.empleadoAsignado = empleadoAsignado;
    }

    public void setListaProductos(ArrayList<Integer> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public void setTiendaAsignada(String tiendaAsignada) {
        this.tiendaAsignada = tiendaAsignada;
    }

    public Integer getTiendaAsignadaId() {
        return tiendaAsignadaId;
    }

    public void setTiendaAsignadaId(Integer tiendaAsignadaId) {
        this.tiendaAsignadaId = tiendaAsignadaId;
    }
}
