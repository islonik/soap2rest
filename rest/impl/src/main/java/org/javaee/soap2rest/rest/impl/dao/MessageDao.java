package org.javaee.soap2rest.rest.impl.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.javaee.soap2rest.rest.impl.model.Message;
import org.javaee.soap2rest.utils.sql.DaoSupport;

import java.util.List;

/**
 * Created by nikilipa on 3/31/17.
 */
public class MessageDao extends DaoSupport<Message> {

    @PersistenceContext(unitName = "org.javaee.soap2rest.rest")
    private EntityManager em;

    @Override
    public EntityManager em() {
        return em;
    }

    public Message findById(Long id) {
        return em.createNamedQuery("findById", Message.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Message> findAll() {
        return em.createNamedQuery("findAll", Message.class)
                .getResultList();
    }

}
