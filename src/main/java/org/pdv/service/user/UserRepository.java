package org.pdv.service.user;

import org.pdv.domain.user.User;

public interface UserRepository {
    public String save(User user);

    public void delete(String id);

    public User findById(String id);
}
