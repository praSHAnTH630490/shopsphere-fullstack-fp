package com.store.resource;

import com.store.dto.ApiResponse;
import com.store.dto.contact.CreateContactRequest;
import com.store.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactResource {

    private final ContactService contactService;

    public ContactResource(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createMessage(
            Authentication authentication,
            @Valid @RequestBody CreateContactRequest request
    ) {

        String email = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : null;

        contactService.createMessage(
                email,
                request
        );

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Contact message submitted successfully"
                )
        );
    }
}
