package com.bluescript.demo.jpa;

import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import com.bluescript.demo.entity.CustomerEntity;
import com.bluescript.demo.entity.CustomerEntity;
import com.bluescript.demo.entity.CustomerEntity;

public interface IUpdateCustomerJpa extends JpaRepository<CustomerEntity, String> {
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE CUSTOMER SET FIRSTNAME = :caFirstName , LASTNAME = :caLastName , DATEOFBIRTH = :caDob , HOUSENAME = :caHouseName , HOUSENUMBER = :caHouseNum , POSTCODE = :caPostcode , PHONEMOBILE = :caPhoneMobile , PHONEHOME = :caPhoneHome , EMAILADDRESS = :caEmailAddress WHERE CUSTOMERNUMBER = :db2CustomernumInt", nativeQuery = true)
    void updateCustomerByCaFirstNameAndCaLastNameAndCaDob(@Param("caFirstName") String caFirstName,
            @Param("caLastName") String caLastName, @Param("caDob") String caDob,
            @Param("caHouseName") String caHouseName, @Param("caHouseNum") String caHouseNum,
            @Param("caPostcode") String caPostcode, @Param("caPhoneMobile") String caPhoneMobile,
            @Param("caPhoneHome") String caPhoneHome, @Param("caEmailAddress") String caEmailAddress,
            @Param("db2CustomernumInt") int db2CustomernumInt);
}
