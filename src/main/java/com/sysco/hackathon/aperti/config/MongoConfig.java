package com.sysco.hackathon.aperti.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${application.database.cluster}")
    private String cluster;

    @Value("${application.database.dbname}")
    private String database;

    @Value("${application.database.username}")
    private String username;

    @Value("${application.database.password}")
    private String password;

    @Override
    public @NonNull MongoClient mongoClient() {
        LOGGER.info("Connecting to Mongo DB: {}", getConnectionString());
        final ConnectionString connectionString = new ConnectionString(getConnectionString());
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    protected String getConnectionString() {
        StringBuilder mongoConnectionString = new StringBuilder();
        mongoConnectionString.append("mongodb+srv://").append(username).append(":").append(password);
        mongoConnectionString.append("@").append(cluster).append("/");
        mongoConnectionString.append(database).append("?retryWrites=true&w=majority");
        return mongoConnectionString.toString();
    }

    @Override
    protected @NonNull String getDatabaseName() {
        return database;
    }

}
