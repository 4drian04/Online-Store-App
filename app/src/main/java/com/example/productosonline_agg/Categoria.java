package com.example.productosonline_agg;

public class Categoria {

    private int categoryId;

    private String nombreCategoria;

    public Categoria(int categoryId, String nombreProducto) {
        this.categoryId = categoryId;
        this.nombreCategoria = nombreProducto;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
