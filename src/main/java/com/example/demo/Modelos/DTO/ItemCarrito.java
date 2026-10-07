package com.example.demo.Modelos.DTO;

import com.example.demo.Modelos.Entity.Producto;

// Representa un elemento dentro del carrito de compras temporal en la sesión
public class ItemCarrito {

    private Producto producto;
    private Integer cantidad;

    public ItemCarrito() {
    }

    public ItemCarrito(Producto producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        if (producto != null && producto.getPrecio() != null && cantidad != null) {
            return producto.getPrecio() * cantidad;
        }
        return 0.0;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
