package org.javaee.soap2rest.rest.impl.services;

import org.javaee.soap2rest.rest.impl.dao.MessageDao;
import org.javaee.soap2rest.rest.impl.model.Message;
import org.javaee.soap2rest.utils.liquibase.annotations.Liquibase;
import org.javaee.soap2rest.utils.liquibase.annotations.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.Random;

/**
 * Created by nikilipa on 3/28/17.
 */
@ApplicationScoped
@Default
@Liquibase
@Schema(name = "s2r_rest_message", resource = "/liquibase/MessageChangelog.xml")
public class MessageServices {

    private final static Logger log = LoggerFactory.getLogger(MessageServices.class);

    @Inject
    private MessageDao messageDao;

    private int totalNumber = 0;
    private final Random random = new Random();

    public void updateTotalNumber() {
        this.totalNumber = messageDao.findAll().size() - 1;
    }

    public Message getRandomMessage() {
        if (totalNumber <= 0) {
            updateTotalNumber();
        }
        int messageId = random.nextInt(totalNumber) + 1;

        log.info("MessageId = " + messageId);
        return messageDao.findById(Long.valueOf(messageId));
    }


}
