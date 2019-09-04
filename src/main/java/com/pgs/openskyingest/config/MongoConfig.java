package com.pgs.openskyingest.config;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"com.pgs.openskyingest.repository"})
public class MongoConfig {
}