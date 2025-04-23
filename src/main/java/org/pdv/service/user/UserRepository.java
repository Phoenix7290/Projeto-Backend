package org.pdv.service.user;

import org.pdv.domain.user.User;

import java.util.List;

public interface UserRepository {
    public String save(User user);

    public void delete(String id);

    public List<User> findAll();

    public User findById(String id);

    public void update(User user);
}
