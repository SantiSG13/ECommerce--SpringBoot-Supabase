package com.example.demo.Modelos.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

//Encargado de bodega y conexion con la DB
@Repository
public class RepositoryUsuarioDAO implements InterfaceUsuarioDAO{

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public Usuario findByUsuario(String usuario) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.usuario = :usuario", Usuario.class)
                    .setParameter("usuario", usuario)
                    .getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;            
        }   
    }

    
    
}