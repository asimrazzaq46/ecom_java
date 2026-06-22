package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a JOIN FETCH a.user au WHERE au.userId = ?1")
    List<Address> findAddressByUserId(Long userId);
}
