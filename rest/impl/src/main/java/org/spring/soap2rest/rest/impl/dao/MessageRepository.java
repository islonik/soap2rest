package org.spring.soap2rest.rest.impl.dao;

import org.spring.soap2rest.rest.impl.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nikilipa on 3/28/17.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findById(Long id);

}
