package com.sysco.hackathon.aperti.repository;

import com.sysco.hackathon.aperti.dao.CustomerDetailsDAO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaceRepository extends MongoRepository<CustomerDetailsDAO, String> {
}
