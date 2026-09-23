package com.example.demo.Modelos.DAO;

import com.example.demo.Modelos.Entity.Usuario;

public interface InterfaceUsuarioDAO {
    public Usuario findByUsuario(String usuario);    
}