package com.rev.test.transfers.dao;

import com.rev.test.transfers.model.User;

public interface UserDao {
    User create(User user);
    User read(String userName);
}
