package com.info5059.casestudy.purchaseorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderDAO orderDAO;

    @PostMapping("/api/pos")
    public ResponseEntity<Long> addOne(@RequestBody PurchaseOrder clientorder) { // use RequestBody here
        Long orderId = orderDAO.create(clientorder);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping("/api/pos/{id}")
    public ResponseEntity<Iterable<PurchaseOrder>> findbyVendorId(@PathVariable long id) {
        Iterable<PurchaseOrder> vendorOrders = orderDAO.findByVendor(id);
        return new ResponseEntity<Iterable<PurchaseOrder>>(vendorOrders, HttpStatus.OK);
    }
}
