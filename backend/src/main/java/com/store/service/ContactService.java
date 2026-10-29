package com.store.service;

import com.store.dao.ContactMessageDAO;
import com.store.dao.UserDAO;
import com.store.dto.contact.CreateContactRequest;
import com.store.entity.ContactMessage;
import com.store.entity.User;
import com.store.entity.enums.ContactStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService {

    private final ContactMessageDAO contactMessageDAO;
    private final UserDAO userDAO;

    public ContactService(
            ContactMessageDAO contactMessageDAO,
            UserDAO userDAO
    ) {
        this.contactMessageDAO = contactMessageDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public void createMessage(
            String email,
            CreateContactRequest request
    ) {

        User user = null;
        if (email != null && !email.isBlank()) {
            user = userDAO.findByEmail(email).orElse(null);
        }

        ContactMessage contactMessage =
                new ContactMessage();

        contactMessage.setUser(user);
        contactMessage.setName(request.name().trim());
        contactMessage.setEmail(request.email().trim());
        contactMessage.setSubject(request.subject().trim());
        contactMessage.setMessage(request.message().trim());
        contactMessage.setStatus(ContactStatus.OPEN);

        contactMessageDAO.save(contactMessage);
    }
}
