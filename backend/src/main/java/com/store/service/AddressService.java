package com.store.service;

import com.store.dao.AddressDAO;
import com.store.dao.UserDAO;
import com.store.dto.address.AddressRequest;
import com.store.dto.address.AddressResponse;
import com.store.entity.Address;
import com.store.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressDAO addressDAO;
    private final UserDAO userDAO;

    public AddressService(
            AddressDAO addressDAO,
            UserDAO userDAO
    ) {
        this.addressDAO = addressDAO;
        this.userDAO = userDAO;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(String email) {

        User user = getUser(email);

        return addressDAO.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressResponse createAddress(
            String email,
            AddressRequest request
    ) {

        User user = getUser(email);

        Address address = new Address();

        address.setUser(user);
        address.setFullName(request.fullName());
        address.setPhone(request.phone());
        address.setAddressLine(request.addressLine());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());

        return toResponse(addressDAO.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(
            String email,
            Long addressId,
            AddressRequest request
    ) {

        User user = getUser(email);

        Address address = addressDAO
                .findByIdAndUser(addressId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Address not found"
                        )
                );

        address.setFullName(request.fullName());
        address.setPhone(request.phone());
        address.setAddressLine(request.addressLine());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());

        return toResponse(addressDAO.save(address));
    }

    @Transactional
    public void deleteAddress(
            String email,
            Long addressId
    ) {

        User user = getUser(email);

        Address address = addressDAO
                .findByIdAndUser(addressId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Address not found"
                        )
                );

        addressDAO.delete(address);
    }

    private User getUser(String email) {

        return userDAO.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private AddressResponse toResponse(Address address) {

        return new AddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getAddressLine(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }
}