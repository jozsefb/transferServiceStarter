package com.rev.test.transfers.dao.impl;

import com.rev.test.transfers.dao.UserDao;
import com.rev.test.transfers.exception.DuplicateAccountException;
import com.rev.test.transfers.exception.NotFoundException;
import com.rev.test.transfers.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserDaoImpl implements UserDao {
    private Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User create(User user) {
        if (users.get(user.getUserName()) != null) {
            throw new DuplicateAccountException(user.getUserName());
        }
        user.setUserId(UUID.randomUUID().toString());
        users.put(user.getUserName(), user);
        return user;
    }

    @Override
    public User read(String userName) {
        User user = users.get(userName);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    //UPDATE AND DELETE NOT SUPPORTED
}
