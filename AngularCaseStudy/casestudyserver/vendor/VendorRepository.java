package com.info5059.casestudy.vendor;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

@Repository
public interface VendorRepository extends CrudRepository<Vendor, Long> {
    // extend so we can return the number of rows deleted
    @Modifying
    @Transactional
    @Query("delete from Vendor where id = ?1")
    @DeleteMapping("/vendors/{id}")
    int deleteOne(Long vendorid);
}
