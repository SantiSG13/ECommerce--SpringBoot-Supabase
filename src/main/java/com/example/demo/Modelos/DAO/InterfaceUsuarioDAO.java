package com.example.demo.Modelos.DAO;

import java.util.List;
import com.example.demo.Modelos.Entity.Usuario;

public interface InterfaceUsuarioDAO {
    
    public Usuario findByUsuario(String usuario);

    public void save(Usuario usuario);

    public List<Usuario> findByHabilitado(boolean habilitado);

    public List<Usuario> findAll();

    public Usuario findById(Long id);
}