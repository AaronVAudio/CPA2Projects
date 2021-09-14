package com.info5059.casestudy.purchaseorder;

import com.info5059.casestudy.product.Product;
import com.info5059.casestudy.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Component
public class PurchaseOrderDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Long create(PurchaseOrder clientorder) {
        PurchaseOrder realOrder = new PurchaseOrder();
        realOrder.setVendorid(clientorder.getVendorid());
        realOrder.setAmount(clientorder.getAmount());
        realOrder.setPodate(new Date());
        entityManager.persist(realOrder);

        for(PurchaseOrderLineitem item :clientorder.getItems()) {
            PurchaseOrderLineitem realItem = new PurchaseOrderLineitem();
            realItem.setPoid(realOrder.getId());
            realItem.setPrice(item.getPrice());
            realItem.setQty(item.getQty());
            realItem.setProductid(item.getProductid());
            Product product = productRepository.findById(item.getProductid()).get();
            product.setQoo(product.getQoo() + item.getQty());
            entityManager.persist(realItem);
        }
        return realOrder.getId();
    }

    public PurchaseOrder findOne(Long id) {
        PurchaseOrder PO = entityManager.find(PurchaseOrder.class, id);
        if (PO == null) {
            throw new EntityNotFoundException("Can't find purchase order for ID "
                    + id);
        }
        return PO;
    }

    public Iterable<PurchaseOrder> findByVendor(Long vendorId) {
        return entityManager.createQuery("select p from PurchaseOrder p where p.vendorid = :id")
                .setParameter("id", vendorId)
                .getResultList();
    }
}
