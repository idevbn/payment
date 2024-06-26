package com.ead.payment.dtos;

import com.ead.payment.models.UserModel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@Data
public class UserEventDTO {

    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private String userStatus;
    private String userType;
    private String phoneNumber;
    private String cpf;
    private String imageUrl;
    private String actionType;

    public UserModel convertToUserModel(final UserModel userModel) {
        BeanUtils.copyProperties(this, userModel);
        return userModel;
    }

}
