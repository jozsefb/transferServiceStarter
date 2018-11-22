package com.rev.test.transfers.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.rev.test.transfers.dao.AccountsDao;
import com.rev.test.transfers.dao.impl.AccountsDaoImpl;
import com.rev.test.transfers.dao.UserDao;
import com.rev.test.transfers.dao.impl.UserDaoImpl;
import com.rev.test.transfers.service.AccountService;
import com.rev.test.transfers.service.impl.AccountServiceImpl;
import com.rev.test.transfers.service.DepositService;
import com.rev.test.transfers.service.impl.DepositServiceImpl;
import com.rev.test.transfers.service.TransferService;
import com.rev.test.transfers.service.impl.TransferServiceImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Context;
import io.vertx.reactivex.core.Vertx;

import java.util.Properties;

public class GuiceModule extends AbstractModule {

    private final Context context;

    public GuiceModule(Vertx vertx) {
        this.context = vertx.getOrCreateContext();
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), extractToProperties(context.config()));
        //bind
        bind(AccountsDao.class).to(AccountsDaoImpl.class).in(Singleton.class);
        bind(UserDao.class).to(UserDaoImpl.class).in(Singleton.class);
        bind(AccountService.class).to(AccountServiceImpl.class).in(Singleton.class);
        bind(DepositService.class).to(DepositServiceImpl.class).in(Singleton.class);
        bind(TransferService.class).to(TransferServiceImpl.class).in(Singleton.class);
    }

    private Properties extractToProperties(JsonObject config) {
        Properties properties = new Properties();
        config.getMap().keySet().forEach((String key) -> properties.setProperty(key, "" + config.getValue(key)));
        return properties;
    }
}
