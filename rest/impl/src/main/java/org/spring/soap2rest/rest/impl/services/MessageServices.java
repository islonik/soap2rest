package org.spring.soap2rest.rest.impl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.impl.dao.MessageRepository;
import org.spring.soap2rest.rest.impl.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * Created by nikilipa on 3/28/17.
 */
@Service
@Transactional
public class MessageServices {

    private final static Logger log = LoggerFactory.getLogger(MessageServices.class);

    @Autowired
    private MessageRepository messageRepository;

    private int totalNumber = 0;
    private final Random random = new Random();

    public void updateTotalNumber() {
        this.totalNumber = messageRepository.findAll().size() - 1;
    }

    public Message getRandomMessage() {
        if (totalNumber <= 0) {
            updateTotalNumber();
        }
        int messageId = random.nextInt(totalNumber) + 1;

        log.info("MessageId = " + messageId);
        return messageRepository.findById(Long.valueOf(messageId));
    }


}
