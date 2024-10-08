package com.project.habitasse.domain.propertyDemand.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.habitasse.domain.address.entities.Address;
import com.project.habitasse.domain.common.SuperclassEntity;
import com.project.habitasse.domain.demand.entities.Demand;
import com.project.habitasse.domain.enums.*;
import com.project.habitasse.domain.propertyDemand.entities.request.PropertyDemandRequest;
import com.project.habitasse.security.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_property_demand")
@SequenceGenerator(name = "default_gen", sequenceName = "role_seq", allocationSize = 1)
public class PropertyDemand extends SuperclassEntity implements Serializable {

    @Column(name = "contract_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractTypeEnum contractType;

    @Column(name = "property_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyTypeEnum propertyType;

    @Column(name = "bedrooms_number")
    @Enumerated(EnumType.STRING)
    private BedroomsNumberEnum bedroomsNumber;

    @Column(name = "furnished")
    @Enumerated(EnumType.STRING)
    private BooleanEnum furnished;

    @Column(name = "pet_friendly")
    @Enumerated(EnumType.STRING)
    private BooleanEnum petFriendly;

    @Column(name = "suggested_value_for_rent")
    @Enumerated(EnumType.STRING)
    private SuggestedValueForRentEnum suggestedValueForRent;

    @Column(name = "suggested_value_for_sale")
    @Enumerated(EnumType.STRING)
    private SuggestedValueForSaleEnum suggestedValueForSale;

    @Column(name = "suggested_value_for_seasonal")
    @Enumerated(EnumType.STRING)
    private SuggestedValueForSeasonalEnum suggestedValueForSeasonal;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "demand_id", referencedColumnName = "id")
    private Demand demand;

    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "deleted")
    private boolean isDeleted = false;

    public static PropertyDemand createPropertyDemand(PropertyDemandRequest propertyDemandRequest) {
        return PropertyDemand.builder()
                .contractType(ContractTypeEnum.getByDescription(propertyDemandRequest.getContractType()))
                .propertyType(PropertyTypeEnum.getByDescription(propertyDemandRequest.getPropertyType()))
                .bedroomsNumber(StringUtils.isEmpty(propertyDemandRequest.getBedroomsNumber())
                        ? null
                        : BedroomsNumberEnum.getByDescription(propertyDemandRequest.getBedroomsNumber()))
                .furnished(StringUtils.isEmpty(propertyDemandRequest.getFurnished())
                        ? null
                        : BooleanEnum.getByDescription(propertyDemandRequest.getFurnished()))
                .petFriendly(StringUtils.isEmpty(propertyDemandRequest.getPetFriendly())
                        ? null
                        : BooleanEnum.getByDescription(propertyDemandRequest.getPetFriendly()))
                .suggestedValueForRent(StringUtils.isEmpty(propertyDemandRequest.getSuggestedValueForRent())
                        ? null
                        : SuggestedValueForRentEnum.getByDescription(propertyDemandRequest.getSuggestedValueForRent()))
                .suggestedValueForSale(StringUtils.isEmpty(propertyDemandRequest.getSuggestedValueForSale())
                        ? null
                        : SuggestedValueForSaleEnum.getByDescription(propertyDemandRequest.getSuggestedValueForSale()))
                .suggestedValueForSeasonal(StringUtils.isEmpty(propertyDemandRequest.getSuggestedValueForSeasonal())
                        ? null
                        : SuggestedValueForSeasonalEnum.getByDescription(propertyDemandRequest.getSuggestedValueForSeasonal()))
                .build();
    }

    public static PropertyDemand delete(PropertyDemand propertyDemand) {
        propertyDemand.setDeleted(true);
        return propertyDemand;
    }

    public static PropertyDemand updateDemand(PropertyDemand propertyDemand, PropertyDemandRequest propertyDemandRequest) {
        propertyDemand.setContractType(ContractTypeEnum.getByDescription(propertyDemandRequest.getContractType()));
        propertyDemand.setPropertyType(PropertyTypeEnum.getByDescription(propertyDemandRequest.getPropertyType()));
        propertyDemand.setBedroomsNumber(StringUtils.isEmpty(propertyDemandRequest.getBedroomsNumber())
                ? null
                : BedroomsNumberEnum.getByDescription(propertyDemandRequest.getBedroomsNumber()));
        propertyDemand.setFurnished(StringUtils.isEmpty(propertyDemandRequest.getFurnished())
                ? null
                : BooleanEnum.getByDescription(propertyDemandRequest.getFurnished()));
        propertyDemand.setPetFriendly(StringUtils.isEmpty(propertyDemandRequest.getPetFriendly())
                ? null
                : BooleanEnum.getByDescription(propertyDemandRequest.getPetFriendly()));
        propertyDemand.setSuggestedValueForRent(StringUtils.isEmpty(propertyDemandRequest.getSuggestedValueForRent())
                ? null
                : SuggestedValueForRentEnum.getByDescription(propertyDemandRequest.getSuggestedValueForRent()));
        propertyDemand.setSuggestedValueForSale(StringUtils.isEmpty(propertyDemandRequest.getSuggestedValueForSale())
                ? null
                : SuggestedValueForSaleEnum.getByDescription(propertyDemandRequest.getSuggestedValueForSale()));
        propertyDemand.setSuggestedValueForSeasonal(StringUtils.isEmpty(propertyDemandRequest.getSuggestedValueForSeasonal())
                ? null
                : SuggestedValueForSeasonalEnum.getByDescription(propertyDemandRequest.getSuggestedValueForSeasonal()));
        propertyDemand.demand.setAnnotation(propertyDemandRequest.getAnnotation());
        propertyDemand.address.setState(propertyDemandRequest.getState());
        propertyDemand.address.setCity(propertyDemandRequest.getCity());
        return propertyDemand;
    }
}
