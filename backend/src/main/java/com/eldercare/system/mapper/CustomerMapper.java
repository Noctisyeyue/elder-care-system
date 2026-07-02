package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.Customer;
import com.eldercare.system.entity.NursingItemRecord;
import com.eldercare.system.vo.caregiver.PurchasedItemsVO;
import com.eldercare.system.vo.customer.CustomerCheckOutVO;
import com.eldercare.system.vo.customer.CustomerVO;
import com.eldercare.system.vo.customer.CustomerNoCaregiverVO;
import com.eldercare.system.vo.customer.CustomerOutingVO;
import com.eldercare.system.vo.customer.NursingLevelDistributionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** CustomerMapper */
public interface CustomerMapper extends BaseMapper<Customer> {
    List<CustomerVO> listCustomerItems(@Param("customerName") String customerName,
                                         @Param("customerType") String customerType,
                                         @Param("pageStart") int pageStart,
                                         @Param("pageSize") int pageSize);
    List<CustomerCheckOutVO> listCustomerCheckOutItems(@Param("customerName") String customerName,
                                                         @Param("pageStart") int pageStart,
                                                         @Param("pageSize") int pageSize);

    long countCustomerItems(@Param("customerName") String customerName,
                            @Param("customerType") String customerType);
    long countCustomerCheckOutItems(@Param("customerName") String customerName);

    void updateDelFlag(Long id, int i);

    List<CustomerOutingVO> listCustomerOutingItems(@Param("customerName") String customerName,
                                                     @Param("pageStart") int pageStart,
                                                      @Param("pageSize") int pageSize
                                                     );
    long countCustomerOutingItems(@Param("customerName") String customerName);

    List<CustomerNoCaregiverVO> listNoCaregiverItems(@Param("customerName") String customerName,
                                                       @Param("pageStart") int pageStart,
                                                       @Param("pageSize") int pageSize);

    List<PurchasedItemsVO> listPurchasedItems(@Param("customerId")  Long customerId,
                                                  @Param("pageStart")  int pageStart,
                                                  @Param("pageSize")  int pageSize,
                                                  @Param("itemName")  String itemName);
    int countPurchasedItems(@Param("customerId")  Long customerId, String itemName);

    int isPurchased(@Param("customerId") Long customerId, @Param("itemId")  Long itemId);

    void buyNursingItems(List<NursingItemRecord> requests);

    Map<String, Object> getNursingItemById(Long itemId);

    Long getIdByIdNum(String idNumberHash);

    Customer selectByIdNum(String idNumberHash);

    void removeAllCustomers(int caregiverId);

    void updateNursingLevelById(Customer customer);

    /**
     * 查询客户护理级别分布
     *
     * @return 护理级别分布列表
     */
    List<NursingLevelDistributionVO> selectNursingLevelDistribution();
}
