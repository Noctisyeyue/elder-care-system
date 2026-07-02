package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.Role;
import com.eldercare.system.entity.User;
import com.eldercare.system.vo.customer.CustomerNoCaregiverVO;

import java.util.List;

/** UserMapper */
public interface UserMapper extends BaseMapper<User> {
    Long selectIdByUsername(String username);

    List<CustomerNoCaregiverVO> listcustomerItems(Long caregiverId, int pageStart, int pageSize, String customerName);
    long countCustomerItems(Long caregiverId, String customerName);

    int addCustomer(Long caregiverId, Long customerId);

    int removeCustomer(Long caregiverId, Long customerId);

    int deleteUser(String userName);

    int updateByEmail(String email, String password);

    void updateAvatar(String upload, Long userId);

    String selectAvatar(Long userId);

    void updateUserId(Long oldUserId);

    String selectEmail(Long userId);

    Long getTotalCareCount(Long userId);

    /**
     * 查询累计已执行护理次数
     *
     * @param userId 护工用户ID
     * @return 已执行次数总和
     */
    Long getSumExecutedTimes(Long userId);
}
