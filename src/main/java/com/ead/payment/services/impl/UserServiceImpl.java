package com.ead.payment.services.impl;

import com.ead.payment.models.UserModel;
import com.ead.payment.repositories.UserRepository;
import com.ead.payment.services.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserModel save(final UserModel userModel) {
        return this.userRepository.save(userModel);
    }

    @Override
    @Transactional
    public void delete(final UUID userId) {
        this.userRepository.deleteById(userId);
    }

    @Override
    public Optional<UserModel> findById(final UUID userId) {
        return this.userRepository.findById(userId);
    }

}
