package com.ead.payment.services;

import com.ead.payment.models.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserModel save(final UserModel userModel);

    void delete(final UUID userId);

    Optional<UserModel> findById(final UUID userId);
}
