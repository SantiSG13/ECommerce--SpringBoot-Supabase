package com.example.demo.Modelos.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class RepositoryUsuarioDAO implements InterfaceUsuarioDAO {

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
            return null;
        }
    }

    @Transactional
    @Override
    public void save(Usuario usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario);
        } else {
            em.merge(usuario);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findByHabilitado(boolean habilitado) {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.habilitado = :habilitado", Usuario.class)
                .setParameter("habilitado", habilitado)
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findById(Long id) {
        return em.find(Usuario.class, id);
    }
}