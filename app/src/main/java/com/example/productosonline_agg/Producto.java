package com.example.productosonline_agg;

import java.util.Objects;

public class Producto {

    private int productoId;

    private String nombreProducto;

    private String categoria;

    private String fileUrl;

    public Producto(int productoId, String nombreProducto, String categoria, String fileUrl) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.fileUrl=fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return productoId == producto.productoId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productoId);
    }
}
