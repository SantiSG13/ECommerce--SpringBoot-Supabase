package com.example.demo.Modelos.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.Compra;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class RepositoryCompraDAO implements InterfaceCompraDAO {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public List<Compra> findAll() {
        return em.createQuery("from Compra c order by c.fecha desc").getResultList();
    }

    @Transactional
    @Override
    public void save(Compra compra) {
        if (compra.getId() != null && compra.getId() > 0) {
            em.merge(compra);
        } else {
            em.persist(compra);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Compra findOne(Long id) {
        return em.find(Compra.class, id);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public List<Compra> findByClienteId(Long clienteId) {
        return em.createQuery("from Compra c where c.cliente.id = :clienteId order by c.fecha desc")
                 .setParameter("clienteId", clienteId)
                 .getResultList();
    }
}
