package com.sysco.hackathon.aperti.repository.impl;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.hackathon.aperti.dao.CustomerDetailsDAO;
import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.repository.PlaceRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceRepositoryImpl extends SimpleMongoRepository<CustomerDetailsDAO, String> implements PlaceRepository {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public PlaceRepositoryImpl(MongoOperations mongoOperations) {
        super(new MongoRepositoryFactory(mongoOperations).getEntityInformation(CustomerDetailsDAO.class), mongoOperations);
    }

    public boolean saveCustomerPlaceDetails(CustomerDetailsDTO customerDetails, String key) {
        try {
            CustomerDetailsDAO customer = MAPPER.convertValue(customerDetails, CustomerDetailsDAO.class);
            customer.setId(key);
            save(customer);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error while saving data into database : " + ex.getMessage());
        }
    }
}
