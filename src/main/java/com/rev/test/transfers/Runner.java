package com.rev.test.transfers;

import com.rev.test.transfers.verticles.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Slf4j
public class Runner {

    public static void main(String[] args) {
        int verticlesNumber = 1;
        if (args != null && args.length > 0) {
            verticlesNumber = Integer.parseInt(args[0].split("=")[1]);
        }

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(verticlesNumber);
        if (deploymentOptions.getConfig() == null) {
            deploymentOptions.setConfig(new JsonObject());
        }
        File conf = new File("src/conf/config.json");
        deploymentOptions.getConfig().mergeIn(getConfiguration(conf));
        VerticlesRunner.run(MainVerticle.class, new VertxOptions()
                        .setClustered(false)
                        .setEventLoopPoolSize(200)
                        .setWorkerPoolSize(200)
                , deploymentOptions);
    }

    private static JsonObject getConfiguration(File config) {
        JsonObject conf = new JsonObject();
        if (config.isFile()) {
            log.debug("Reading config file: {}", config.getAbsolutePath());
            try (Scanner scanner = new Scanner(config).useDelimiter("\\A")) {
                String sconf = scanner.next();
                try {
                    conf = new JsonObject(sconf);
                } catch (DecodeException e) {
                    log.error("Configuration file {} does not contain a valid JSON object", sconf);
                }
            } catch (FileNotFoundException e) {
                // Ignore it.
            }
        } else {
            log.warn("Config file not found {}", config.getAbsolutePath());
        }
        return conf;
    }
}
