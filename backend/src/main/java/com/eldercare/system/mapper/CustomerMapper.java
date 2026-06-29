package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.Customer;
import com.eldercare.system.entity.NursingItemRecord;
import com.eldercare.system.po.caregiver.results.PurchasedItemsResult;
import com.eldercare.system.po.customer.customerresult.CustomerCheckOutItem;
import com.eldercare.system.po.customer.customerresult.CustomerItem;
import com.eldercare.system.po.customer.customerresult.CustomerNoCaregiverItem;
import com.eldercare.system.po.customer.customerresult.CustomerOutingItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** CustomerMapper */
public interface CustomerMapper extends BaseMapper<Customer> {
    List<CustomerItem> listCustomerItems(@Param("customerName") String customerName,
                                         @Param("customerType") String customerType,
                                         @Param("pageStart") int pageStart,
                                         @Param("pageSize") int pageSize);
    List<CustomerCheckOutItem> listCustomerCheckOutItems(@Param("customerName") String customerName,
                                                         @Param("pageStart") int pageStart,
                                                         @Param("pageSize") int pageSize);

    long countCustomerItems(@Param("customerName") String customerName,
                            @Param("customerType") String customerType);
    long countCustomerCheckOutItems(@Param("customerName") String customerName);

    void updateDelFlag(Long id, int i);

    List<CustomerOutingItem> listCustomerOutingItems(@Param("customerName") String customerName,
                                                     @Param("pageStart") int pageStart,
                                                      @Param("pageSize") int pageSize
                                                     );
    long countCustomerOutingItems(@Param("customerName") String customerName);

    List<CustomerNoCaregiverItem> listNoCaregiverItems(@Param("customerName") String customerName,
                                                       @Param("pageStart") int pageStart,
                                                       @Param("pageSize") int pageSize);

    List<PurchasedItemsResult> listPurchasedItems(@Param("customerId")  Long customerId,
                                                  @Param("pageStart")  int pageStart,
                                                  @Param("pageSize")  int pageSize,
                                                  @Param("itemName")  String itemName);
    int countPurchasedItems(@Param("customerId")  Long customerId, String itemName);

    int isPurchased(@Param("customerId") Long customerId, @Param("itemId")  Long itemId);

    void buyNursingItems(List<NursingItemRecord> requests);

    Map<String, Object> getNursingItemById(Long itemId);

    Long getIdByIdNum(String idNumber);

    Customer selectByIdNum(String idNumber);

    void removeAllCustomers(int caregiverId);

    void updateNursingLevelById(Customer customer);
}
