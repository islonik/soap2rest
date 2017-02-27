package org.javaee.soap2rest.soap.impl.dao;

import org.javaee.soap2rest.soap.impl.model.sql.AuthUser;
import org.javaee.soap2rest.utils.sql.DaoSupport;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by nikilipa on 8/13/16.
 */
@Dependent
public class AuthUserDao extends DaoSupport<AuthUser> {

    @PersistenceContext(unitName = "org.javaee.soap2rest.soap")
    private EntityManager em;

    @Override
    public EntityManager em() {
        return em;
    }

    public List<AuthUser> findAllUsers() {
        return em.createNamedQuery("findAllUsers", AuthUser.class)
                .getResultList();
    }

}
