package org.javaee.soap2rest.utils.sql;

import javax.persistence.EntityManager;

/**
 * Created by nikilipa on 2/27/17.
 */
public abstract class DaoSupport<ENTITY> {

    public ENTITY save(ENTITY e) {
        return em().merge(e);
    }

    public void delete(ENTITY e) {
        em().remove(e);
    }

    public abstract EntityManager em();

}
