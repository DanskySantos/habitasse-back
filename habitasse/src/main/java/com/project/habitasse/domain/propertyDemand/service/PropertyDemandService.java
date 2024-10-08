package com.project.habitasse.domain.propertyDemand.service;

import com.project.habitasse.domain.address.entities.Address;
import com.project.habitasse.domain.address.repository.AddressRepository;
import com.project.habitasse.domain.demand.entities.Demand;
import com.project.habitasse.domain.demand.repository.DemandRepository;
import com.project.habitasse.domain.propertyDemand.entities.PropertyDemand;
import com.project.habitasse.domain.propertyDemand.entities.request.PropertyDemandRequest;
import com.project.habitasse.domain.propertyDemand.repository.PropertyDemandRepository;
import com.project.habitasse.security.service.JwtService;
import com.project.habitasse.security.user.entities.User;
import com.project.habitasse.security.user.entities.request.UserRequest;
import com.project.habitasse.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyDemandService {


    private final PropertyDemandRepository propertyDemandRepository;
    private final DemandRepository demandRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public PropertyDemand registerDemand(PropertyDemandRequest propertyDemandRequest, String token) {
        Address address = Address.createAddress(propertyDemandRequest);
        PropertyDemand newPropertyDemand = PropertyDemand.createPropertyDemand(propertyDemandRequest);

        newPropertyDemand.setAddress(address);
        newPropertyDemand.setUser(userRepository.findByEmailAndExcludedFalse(jwtService.getEmail(token)).orElseThrow());

        addressRepository.save(address);
        PropertyDemand propertyDemand = propertyDemandRepository.save(newPropertyDemand);

        propertyDemandRequest.setPropertyDemand(propertyDemand);
        Demand demand = Demand.createDemand(propertyDemandRequest);

        propertyDemand.setDemand(demandRepository.save(demand));
        propertyDemandRepository.save(propertyDemand);
        return newPropertyDemand;
    }

    public PropertyDemand registerNewDemand(PropertyDemandRequest propertyDemandRequest) {
        Address address = Address.createAddress(propertyDemandRequest);
        PropertyDemand newPropertyDemand = PropertyDemand.createPropertyDemand(propertyDemandRequest);

        newPropertyDemand.setAddress(address);
//        newPropertyDemand.setUser(userRepository.findByEmailAndExcludedFalse(jwtService.getEmail(token)).orElseThrow());

        addressRepository.save(address);
        PropertyDemand propertyDemand = propertyDemandRepository.save(newPropertyDemand);

        propertyDemandRequest.setPropertyDemand(propertyDemand);
        Demand demand = Demand.createDemand(propertyDemandRequest);

        propertyDemand.setDemand(demandRepository.save(demand));
        propertyDemandRepository.save(propertyDemand);
        return newPropertyDemand;
    }

//    public Optional<List<PropertyDemand>> findByEmail(String email) {
//        return propertyDemandRepository.findAllByEmail(email);
//    }

    public PropertyDemand updatePropertyDemand(Long id, PropertyDemandRequest propertyDemandRequest) {
        PropertyDemand propertyDemand = propertyDemandRepository.findById(id).get();

        return propertyDemandRepository.save(PropertyDemand.updateDemand(propertyDemand, propertyDemandRequest));
    }

    public void deleteById(Integer propertyId, Integer demandId) {
        PropertyDemand propertyDemand = new PropertyDemand();
        Demand demand = new Demand();

        if(propertyId != null)
            propertyDemand = propertyDemandRepository.findById(Long.valueOf(propertyId)).get();
        if(demandId != null)
            demand = demandRepository.findById(Long.valueOf(demandId)).get();

        Demand.delete(demand);
        PropertyDemand.delete(propertyDemand);
        propertyDemandRepository.save(propertyDemand);
        demandRepository.save(demand);
    }
}
