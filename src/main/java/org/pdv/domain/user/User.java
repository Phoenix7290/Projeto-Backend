package org.pdv.domain.user;

import org.pdv.domain.utils.IdUtils;

public class User {
    private final String id;
    private String name;
    private String document;
    private String email;

    private User(
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

    public static User with(
            String id,
            String name,
            String document,
            String email
    ) {
        return new User(id, name, document, email);
    }

    public static User with(
            String name,
            String document,
            String email
    ) {
        return new User(IdUtils.uuid(), name, document, email);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocument() {
        return document;
    }

    public String getEmail() {
        return email;
    }
}
