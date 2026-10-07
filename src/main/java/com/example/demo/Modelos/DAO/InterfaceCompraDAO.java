package com.example.demo.Modelos.DAO;

import java.util.List;
import com.example.demo.Modelos.Entity.Compra;

// Interfaz para la gestión de compras en la base de datos
public interface InterfaceCompraDAO {

    public List<Compra> findAll();

    public void save(Compra compra);

    public Compra findOne(Long id);

    public List<Compra> findByClienteId(Long clienteId);

}
