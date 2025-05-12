package org.pdv.repository.user.models;

import org.pdv.domain.user.User;

public class UserModel {
    public String id;
    public String name;
    public String document;
    public String email;

    public UserModel(
            String id,
            String name,
            String document,
            String email
    ) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.email = email;
    }

    public UserModel() {
    }

    public static UserModel fromDomain(User user) {
        return new UserModel(
                user.getId(),
                user.getName(),
                user.getDocument(),
                user.getEmail()
        );
    }

    public User toDomain() {
        return User.with(
                id,
                name,
                document,
                email
        );
    }
}
