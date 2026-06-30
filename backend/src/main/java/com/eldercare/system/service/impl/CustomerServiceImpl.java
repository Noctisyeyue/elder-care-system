package com.eldercare.system.service.impl;
import com.eldercare.system.service.RedisService;
import com.eldercare.system.service.CustomerService;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eldercare.system.entity.*;
import com.eldercare.system.mapper.*;
import com.eldercare.system.dto.bed.BedSwapRequest;
import com.eldercare.system.dto.caregiver.CustomersByCareIdRequest;
import com.eldercare.system.dto.caregiver.PurchasedItemsRequest;
import com.eldercare.system.vo.caregiver.PurchasedItemsListVO;
import com.eldercare.system.vo.caregiver.PurchasedItemsVO;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.dto.customer.*;
import com.eldercare.system.vo.customer.*;
import com.eldercare.system.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
/**
 * 客户管理业务实现。
 */
public class CustomerServiceImpl implements CustomerService{
    /** 客户数据访问对象。 */
    @Autowired
    private CustomerMapper customerMapper;
    /** 床位数据访问对象。 */
    @Autowired
    private BedMapper bedMapper;
    /** 床位记录数据访问对象。 */
    @Autowired
    private BedRecordMapper bedRecordMapper;
    /** Redis 缓存服务。 */
    @Autowired
    private RedisService redisService;
    /** 用户数据访问对象。 */
    @Autowired
    private UserMapper  userMapper;
    /** 房间业务服务。 */
    @Autowired
    private RoomServiceImpl roomService;
    /** 房间数据访问对象。 */
    @Autowired
    private RoomMapper roomMapper;
    /** 床位业务服务。 */
    @Autowired
    BedServiceImpl bedService ;
    /** 健康管家业务服务。 */
    @Autowired
    private CaregiverServiceImpl caregiverServiceImpl;
    /** 外出记录数据访问对象。 */
    @Autowired
    private OutingRecordMapper outingRecordMapper;
    /** 退住记录数据访问对象。 */
    @Autowired
    private CheckOutRecordMapper checkOutRecordMapper;
    /** 退住审批数据访问对象。 */
    @Autowired
    private CheckOutRecordMapper checkoutMapper;
    /** 护理记录数据访问对象。 */
    @Autowired
    private NursingRecordMapper nursingRecordMapper;
    /** 退住记录数据访问对象。 */
    @Autowired
    private CheckOutRecordMapper checkoutRecordMapper;
    /** 客户套餐关系数据访问对象。 */
    @Autowired
    private SetMealCustomerMappingMapper setMealCustomerMappingMapper;

    /**
     * 分页查询客户列表。
     *
     * @param request 客户列表查询参数
     * @return 客户分页列表响应
     */
    @Override
    public ApiResult<CustomerListVO> list(CustomerListRequest request) {
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();
        if(Objects.equals(request.getCustomerType(), "self-care"))
            request.setCustomerType("0");
        if(Objects.equals(request.getCustomerType(), "nursing-care"))
            request.setCustomerType("1");
        List<CustomerVO> items = customerMapper.listCustomerItems(
                request.getCustomerName(),
                request.getCustomerType(),
                pageStart,
                request.getPageSize()
        );
        long total = customerMapper.countCustomerItems(
                request.getCustomerName(),
                request.getCustomerType()
        );
        CustomerListVO response = new CustomerListVO();
        response.setRecords(items);
        response.setTotal(total);
        ApiResult<CustomerListVO> result = new ApiResult<>();
        result.setData(response);
        if(!items.isEmpty()){
            result.setCode(200);
            result.setMessage("查询成功");
        }else {
            result.setCode(200);
            result.setMessage("数据为空");
        }
        return result;
    }

    /**
     * 登记入住客户。
     *
     * @param param 客户登记参数
     * @return 登记处理结果
     */
    @Override
    public ApiResult register(CustomerRegisterRequest param) {
        //创建一个返回值
        ApiResult result = new ApiResult<>();
        //创建一个Customer对象
        Customer customer = new Customer();
        customer.setCustomerName(param.getCustomerName());
        //根据出生日期param.getDateOfBirth()获取年龄
        if (param.getDateOfBirth() != null && !param.getDateOfBirth().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(param.getDateOfBirth(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")); // 根据实际格式调整
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                customer.setAge(age);
            } catch (Exception e) {
                // 可记录日志或返回错误码
                result.setMessage("出生日期格式不正确");
                return result ;
            }
        }
        //swtichgender male变成男
        switch (param.getGender()) {
            case "male": customer.setGender("男");
            break;
            case "female": customer.setGender("女");
            break;
            case "男": customer.setGender("男");
            break;
            case "女": customer.setGender("女");
            break;
        }
        customer.setBloodType(param.getBloodType());
        customer.setFamilyMember(param.getFamilyMember());
        customer.setTel(param.getTel());
        customer.setFamilyMemberTel(param.getFamilyMemberTel());
        customer.setNation(param.getNation());
        customer.setIdNumber(param.getIdNumber());
        //根据床号和房间号获取床id
        Map<String, Object> newBedParams = new HashMap<>();
        newBedParams.put("bedNo", param.getBedNumber());
        newBedParams.put("roomNo", param.getRoomNumber());
        newBedParams.put("building", param.getBuildingNumber());
        Long newBedId = bedMapper.selectBedByBedDetails(newBedParams).getBedId();

        try {
            customer.setBedId(newBedId);
            //根据id_number查询customer,如果有则返回错误
            if (customerMapper.selectByIdNum(param.getIdNumber()) != null) {
                result.setCode(500);
                result.setMessage("添加失败，用户已存在");
                return result;
            }
        }catch (Exception e){
            result.setCode(500);
            throw e;
        }
        customer.setDateOfBirth(param.getDateOfBirth());
        customer.setCheckInDate(param.getCheckInDate());
        customer.setContractEndDate(param.getContractEndDate());
        customer.setCustomerType(param.getCustomerType());
        customer.setPhysicalMentalStatus(param.getPhysicalMentalStatus());
        //插入客户
        try {
            //插入客户
            customerMapper.insert(customer);
            //修改床的使用状态
            bedMapper.updateBedStatusUsed(newBedId);
            //增加床的使用记录
            BedRecord bedRecord = new BedRecord();
            bedRecord.setBedId(newBedId);
            bedRecord.setBedNo(param.getBedNumber());
            bedRecord.setCustomerId(customerMapper.getIdByIdNum(param.getIdNumber()));
            bedRecord.setUsageStartDate(param.getCheckInDate());
            bedRecord.setUsageEndDate(param.getContractEndDate());
            bedRecord.setHistory("1");
            bedRecordMapper.insert(bedRecord);
            result.setCode(200);
            result.setMessage("登记成功");
        }catch (Exception e){
            result.setMessage("登记失败，数据库错误");
            result.setCode(500);
            throw e;
        }

        return result;
    }

    /**
     * 更新客户入住信息。
     *
     * @param id 客户ID
     * @param param 客户更新参数
     * @return 更新处理结果
     */
    @Override
    public ApiResult update(Long id, CustomerRegisterRequest param) {
        // 创建返回结果对象
        ApiResult result = new ApiResult<>();
        // 1. 根据ID查询现有客户信息
        Customer existingCustomer = customerMapper.selectById(id);
        if (existingCustomer == null) {
            result.setCode(404);
            result.setMessage("客户不存在");
            return result;
        }else {
            BedSwapRequest params = new BedSwapRequest();
            //根据床id和history=1查找床位记录的id
            Long bedRecordId = bedRecordMapper.selectBedRecordIdByBedIdAndHistory(existingCustomer.getBedId(), "1");
            //根据existingCustomer.getBedId()查床的房间号和床号
            Bed bed = bedMapper.selectById(existingCustomer.getBedId());
            Room oldRoom = roomMapper.selectById(bed.getRoomId());
            params.setOldBedId(bedRecordId);
            params.setOldBedEndDate(existingCustomer.getContractEndDate());
            params.setNewBedStartDate(param.getCheckInDate());
            params.setNewBedEndDate(param.getContractEndDate());
            if (!Objects.equals(oldRoom.getBuilding(), param.getBuildingNumber())
                    || !Objects.equals(oldRoom.getRoomNo(), param.getRoomNumber())
                    || !Objects.equals(bed.getBedNo(), param.getBedNumber())) {
                params.setNewBuildingNumber(param.getBuildingNumber());
                params.setNewRoomNumber(param.getRoomNumber());
                params.setNewBedNumber(param.getBedNumber());
                bedService.swap(params);
            }
        }

        // 2. 设置基础信息
        existingCustomer.setCustomerName(param.getCustomerName());

        // 3. 计算年龄
        if (param.getDateOfBirth() != null && !param.getDateOfBirth().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(param.getDateOfBirth(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                existingCustomer.setAge(age);
            } catch (Exception e) {
                result.setMessage("出生日期格式不正确");
                result.setCode(400);
                return result;
            }
        }

        existingCustomer.setGender(param.getGender());
        existingCustomer.setBloodType(param.getBloodType());
        existingCustomer.setFamilyMember(param.getFamilyMember());
        existingCustomer.setTel(param.getTel());
        existingCustomer.setFamilyMemberTel(param.getFamilyMemberTel());
        existingCustomer.setNation(param.getNation());
        existingCustomer.setIdNumber(param.getIdNumber());


        // 4. 获取新的床号 ID（如果提供了 roomNumber 和 bedNumber）
        if (param.getRoomNumber() != null && !param.getRoomNumber().isEmpty()
                && param.getBedNumber() != null && !param.getBedNumber().isEmpty()) {
            Map<String, Object> bedParams = new HashMap<>();
            //去掉bedNumber中的“号床”字
            param.setBedNumber(param.getBedNumber().replace("号床", ""));
            bedParams.put("bedNo", param.getBedNumber());
            bedParams.put("roomNo", param.getRoomNumber());
            bedParams.put("building", param.getBuildingNumber());
            try {
                Long newBedId = bedMapper.selectBedByBedDetails(bedParams).getBedId();
                Long oldBedId = existingCustomer.getBedId();
                BedRecord bedRecord = bedRecordMapper.selectByBedId(oldBedId);
                existingCustomer.setBedId(newBedId);
            } catch (Exception e) {
                result.setMessage("床位信息无效");
                result.setCode(500);
                return result;
            }
        }

        // 5. 设置其他字段
        existingCustomer.setDateOfBirth(param.getDateOfBirth());
        existingCustomer.setCheckInDate(param.getCheckInDate());
        existingCustomer.setContractEndDate(param.getContractEndDate());
        existingCustomer.setPhysicalMentalStatus(param.getPhysicalMentalStatus());
        existingCustomer.setCustomerType(param.getCustomerType());
        // 6. 执行更新
        try {
            customerMapper.updateById(existingCustomer);
            result.setCode(200);
            result.setMessage("更新成功");
        } catch (Exception e) {
            result.setMessage("更新失败，数据库错误");
            result.setCode(500);
            // 可以选择打印日志：log.error("更新客户失败", e);
        }

        return result;
    }

    /**
     * 删除客户并释放关联床位。
     *
     * @param id 客户ID
     * @return 删除处理结果
     */
    @Override
    public ApiResult delete(Long id) {
        ApiResult result = new ApiResult();
        if (id == null) {
            result.setCode(500);
        }
        //获取当前日期
        String date = LocalDate.now().toString();
        try{
            Customer customer = customerMapper.selectById(id);
            customerMapper.updateDelFlag(id,1);
            //根据床id将床的状态设置为free
            bedMapper.updateBedStatus(customer.getBedId());
            //将客户表的del_flag改为1
            customerMapper.updateDelFlag(id, 1);
            //修改床位记录表，截止时间usage_end_date改为当前日期
            //是否为历史记录标识符history改为0
            bedRecordMapper.updateBedRecordUsageEndDate(id, date, "0");
            // set_meal_customer_mapping del_flag改为1
            UpdateWrapper<SetMealCustomerMapping> setMealCustomerMappingUpdateWrapper = new UpdateWrapper<>();
            setMealCustomerMappingUpdateWrapper.eq("customer_id", id);
            setMealCustomerMappingUpdateWrapper.set("del_flag", "0");
            setMealCustomerMappingMapper.update(null, setMealCustomerMappingUpdateWrapper);
            // customer对应的nursing_record del_flag改为1
            nursingRecordMapper.updateNursingRecordDelFlag(id);
            // customer对应的outing_record和check_record del_flag改为1
            outingRecordMapper.updateOutingRecordDelFlag(id);
            checkoutRecordMapper.updateCheckoutRecordDelFlag(id);
            result.setCode(200);
            result.setMessage("删除成功");
        }
        catch (Exception e){
            result.setCode(500);
            result.setMessage("删除失败，未找到对应用户");
            throw e;
        }
        return result;
    }

    /**
     * 删除客户床位使用记录。
     *
     * @param id 客户ID
     * @param bedNumber 床位号
     * @return 删除处理结果
     */
    @Override
    public ApiResult deleteBed(Long id, Long bedNumber) {
        ApiResult result = new ApiResult();
        // 根据用户id和床位号删除床位记录
        try {
            bedRecordMapper.deleteBedRecord(id, bedNumber);
            result.setCode(200);
            result.setMessage("删除成功");
        }
        catch (Exception e){
            result.setCode(500);
            result.setMessage("删除失败");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 分页查询退住申请列表。
     *
     * @param request 退住列表查询参数
     * @return 退住申请分页列表响应
     */
    @Override
    public ApiResult<CustomerCheckOutListVO> checkoutList(CustomerListRequest request) {
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();

        List<CustomerCheckOutVO> items = customerMapper.listCustomerCheckOutItems(
                request.getCustomerName(),
                pageStart,
                request.getPageSize()
        );

        long total = customerMapper.countCustomerCheckOutItems(
                request.getCustomerName()
        );

        CustomerCheckOutListVO response = new CustomerCheckOutListVO();
        response.setRecords(items);
        response.setTotal(total);

        ApiResult<CustomerCheckOutListVO> result = new ApiResult<>();
        result.setData(response);

        if(!items.isEmpty()){
            result.setCode(200);
            result.setMessage("查询成功");
        } else {
            result.setCode(200);
            result.setMessage("数据为空");
        }

        return result;
    }

    /**
     * 审批客户退住申请。
     *
     * @param id 退住申请ID
     * @param params 审批参数
     * @param token 当前登录用户令牌
     * @return 审批处理结果
     */
    @Override
    public ApiResult approveCheckout(Long id, Map<String, Object> params, String token) {
        ApiResult result = new ApiResult();
        //根据Token获取用户ID，将审批人id添加到记录表
        Long userId;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户ID不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            return result;
        }
        //获取当前日期
        String date = LocalDate.now().toString();
        //获取退住类型,用汉字映射字符型的数字，退住类型0正常退住1死亡退住2保留床位
        String type =
            switch (params.get("checkOutType").toString()) {
                case "正常退住" -> "0";
                case "死亡退住" -> "1";
                case "保留床位" -> "2";
                default -> "3";
            };
        //获取通过状态,0通过1不通过2已提交3未提交
        String status =
                switch (params.get("approvalStatus").toString()) {
                    case "通过" -> "0";
                    case "不通过" -> "1";
                    case "已提交" -> "2";
                    case "未提交" -> "3";
                    default -> "4";
                };
        String building = params.get("buildingNumber").toString();
        Long roomNo = Long.parseLong(params.get("roomNumber").toString());
        Long bedNo = Long.parseLong(params.get("bedNumber").toString());
        //更新退住记录表
        //如果不通过则状态改为不通过，更新时间改为当前日期
        try {
            int rowsAffected = bedRecordMapper.updateCheckOutRecord(id,userId, status, date, type);
            if (rowsAffected == 0) {
                result.setCode(404);
                result.setMessage("未找到对应退住记录");
                return result;
            }
            if(!type.equals("2")&& status.equals("0")){
                //根据床号将床的状态设置为free
                roomService.updateBedStatus(building, roomNo, bedNo);
                //将客户表的del_flag改为1
                customerMapper.updateDelFlag(id, 1);
                //修改床位记录表，截止时间usage_end_date改为当前日期
                //是否为历史记录标识符history改为0
                bedRecordMapper.updateBedRecordUsageEndDate(id, date, "0");
                // set_meal_customer_mapping del_flag改为1
                UpdateWrapper<SetMealCustomerMapping> setMealCustomerMappingUpdateWrapper = new UpdateWrapper<>();
                setMealCustomerMappingUpdateWrapper.eq("customer_id", id);
                setMealCustomerMappingUpdateWrapper.set("del_flag", "0");
                setMealCustomerMappingMapper.update(null, setMealCustomerMappingUpdateWrapper);
                // customer对应的nursing_record del_flag改为1
                nursingRecordMapper.updateNursingRecordDelFlag(id);
                // customer对应的outing_record和check_record del_flag改为1
                outingRecordMapper.updateOutingRecordDelFlag(id);
                checkoutRecordMapper.updateCheckoutRecordDelFlag(id);
            }
            result.setCode(200);
            result.setMessage("更新成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("更新失败，数据库错误");
            throw e;
        }

        return result;
    }

    /**
     * 分页查询外出申请列表。
     *
     * @param request 外出列表查询参数
     * @return 外出申请分页列表响应
     */
    @Override
    public ApiResult<CustomerOutingListVO> outingList(CustomerListRequest request) {
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();

        List<CustomerOutingVO> items = customerMapper.listCustomerOutingItems(
                request.getCustomerName(),
                pageStart,
                request.getPageSize()
        );

        long total = customerMapper.countCustomerOutingItems(
                request.getCustomerName()
        );

        CustomerOutingListVO response = new CustomerOutingListVO();
        response.setRecords(items);
        response.setTotal(total);

        ApiResult<CustomerOutingListVO> result = new ApiResult<>();
        result.setData(response);

        if (!items.isEmpty()) {
            result.setCode(200);
            result.setMessage("查询成功");
        } else {
            result.setCode(200);
            result.setMessage("数据为空");
        }

        return result;
    }

    /**
     * 审批客户外出申请。
     *
     * @param id 外出申请ID
     * @param params 审批参数
     * @param token 当前登录用户令牌
     * @return 审批处理结果
     */
    @Override
    public ApiResult approveOuting(Long id, Map<String, Object> params, String token) {
        ApiResult result = new ApiResult();
        //根据Token获取用户ID，将审批人id添加到记录表
        Long userId;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户ID不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            return result;
        }
        //获取当前日期
        String date = LocalDate.now().toString();
        //获取通过状态,0通过1不通过2已提交3未提交
        String status =
                switch (params.get("approvalStatus").toString()) {
                    case "通过" -> "0";
                    case "不通过" -> "1";
                    case "已提交" -> "2";
                    case "未提交" -> "3";
                    default -> "4";
                };
        String building = params.get("buildingNumber").toString();
        Long roomNo = Long.parseLong(params.get("roomNumber").toString());
        Long bedNo = Long.parseLong(params.get("bedNumber").toString());
        Map<String, Object> newBedParams = new HashMap<>();
        newBedParams.put("roomNo", roomNo);
        newBedParams.put("bedNo", bedNo);
        newBedParams.put("building", building);
        Long bedId;
        try {
            bedId = bedMapper.selectBedByBedDetails(newBedParams).getBedId();
            result.setCode(200);
            result.setMessage("获取床位成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("获取床Id失败，数据库错误");
            return result;
        }
        //更新外出记录表
        try {
            int rowsAffected = bedRecordMapper.updateOutingRecord(id,userId, status, date);
            if (rowsAffected == 0) {
                result.setCode(404);
                result.setMessage("未找到对应外出记录");
                return result;
            }
            if(status.equals("0")){
                //根据床号将床的状态设置为out
                bedMapper.updateBedStatusOut(bedId);
            }
            result.setCode(200);
            result.setMessage("更新成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("更新失败，数据库错误");
            log.error("e: ", e);
        }

        return result;
    }

    /**
     * 分页查询未分配健康管家的客户。
     *
     * @param request 客户列表查询参数
     * @return 未分配健康管家的客户分页列表响应
     */
    @Override
    public ApiResult<CustomerNoCaregiverListVO> listnoCaregiver(CustomerListRequest request) {
        ApiResult<CustomerNoCaregiverListVO> result = new ApiResult<>();
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();
        List<CustomerNoCaregiverVO> items = customerMapper.listNoCaregiverItems(
                request.getCustomerName(),
                pageStart,
                request.getPageSize()
        );
        if(items.isEmpty()){
            result.setCode(200);
            result.setMessage("数据为空");
            List<CustomerNoCaregiverVO> list = new ArrayList<>();
            CustomerNoCaregiverListVO response = new CustomerNoCaregiverListVO();
            response.setList(list);
            response.setTotal(0L);
            result.setData(response);
            return result;
        }
        long total = customerMapper.countCustomerItems(
                request.getCustomerName(),
                request.getCustomerType());

        CustomerNoCaregiverListVO response = new CustomerNoCaregiverListVO();
        response.setList(items);
        response.setTotal(total);
        result.setData(response);
        result.setMessage("查询成功");
        result.setCode(200);
        return result;
    }

    /**
     * 分页查询客户已购买的护理项目。
     *
     * @param request 已购护理项目查询参数
     * @return 已购护理项目分页列表响应
     */
    @Override
    public ApiResult<PurchasedItemsListVO> purchasedItems(PurchasedItemsRequest request) {
        ApiResult<PurchasedItemsListVO> result = new ApiResult<>();
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();
        List<PurchasedItemsVO> items = customerMapper.listPurchasedItems(
                request.getCustomerId(),
                pageStart,
                request.getPageSize(),
                request.getItemName()
        );
        if(items.isEmpty()){
            result.setCode(200);
            result.setData(new PurchasedItemsListVO(new ArrayList<>(), 0));
            result.setMessage("数据为空");
            return result;
        }
        try {
            int total = customerMapper.countPurchasedItems(request.getCustomerId(),request.getItemName());
            PurchasedItemsListVO response = new PurchasedItemsListVO(items, total);

            result.setData(response);
            result.setCode(200);
            result.setMessage("查询成功");
            return result;
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("查询失败，数据库错误");
            log.error("e: ", e);
            return result;
        }

    }

    /**
     * 判断客户是否已购买指定护理项目。
     *
     * @param customerId 客户ID
     * @param itemId 护理项目ID
     * @return 是否已购买的查询结果
     */
    @Override
    public ApiResult isPurchased(Long customerId, Long itemId) {
        ApiResult result = new ApiResult();
        try {
            int res = customerMapper.isPurchased(customerId, itemId);
            if (res == 1) {
                result.setCode(200);
                result.setMessage("success");
                result.setData(true);
                return result;
            } else {
                result.setCode(200);
                result.setMessage("false");
                result.setData(false);
                return result;
            }
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询失败，数据库错误");
            log.error("e: ", e);
            return result;
        }
    }

    /**
     * 批量购买客户护理项目。
     *
     * @param requests 护理项目购买请求列表
     * @return 购买处理结果
     */
    @Override
    public ApiResult<Map<String, Boolean>> buyItems(List<BuyItemRequest> requests) {
        ApiResult<Map<String, Boolean>> result = new ApiResult<>();
        //新建一个NursingItemRecord对象列表，用于存储要购买的项目信息
        List<NursingItemRecord> records = new ArrayList<>();
        for (BuyItemRequest req : requests) {
            Map<String, Object> itemInfo = customerMapper.getNursingItemById(req.getItemId());
            NursingItemRecord record = new NursingItemRecord();
            record.setCustomerId(req.getCustomerId());
            record.setNursingItemName(itemInfo.get("name").toString());
            record.setPrice((Float) itemInfo.get("price"));
            record.setExecutionCycle(itemInfo.get("executionCycle").toString());
            record.setExecutionTimes( (Integer) itemInfo.get("executionTimes"));
            record.setExecutedTimes(0);
            record.setNursingItemCode(itemInfo.get("code").toString());
            record.setNursingItemId(req.getItemId());
            record.setPurchasingDate(req.getBuyDate());
            record.setPurchasingTimes(req.getBuyCount());
            record.setExpirationDate(req.getExpireDate());
            record.setStatus("0");
            records.add(record);
        }
        try {
            customerMapper.buyNursingItems(records);
            result.setCode(200);
            result.setMessage("购买成功");
            return result;
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("购买失败，数据库错误");
            log.error("e: ", e);
            return result;
        }
    }

    /**
     * 分页查询当前健康管家的服务客户。
     *
     * @param request 客户列表查询参数
     * @param token 当前登录用户令牌
     * @return 当前健康管家的服务客户分页列表响应
     */
    @Override
    public ApiResult<CustomerNoCaregiverListVO> listMyCustomers(CustomerListRequest request, String token) {
        ApiResult<CustomerNoCaregiverListVO> result = new ApiResult<>();
        //从token中获取当前健康管家的 id
        Long userId;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户ID不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            throw e;
        }
        CustomersByCareIdRequest request2 = new CustomersByCareIdRequest();
        request2.setCustomerName(request.getCustomerName());
        request2.setCaregiverId(userId);
        request2.setPageNum(request.getPageNum());
        request2.setPageSize(request.getPageSize());
        result = caregiverServiceImpl.listCustomers(request2);
        return result;
    }

    /**
     * 提交客户外出申请。
     *
     * @param param 外出申请参数
     * @param token 当前登录用户令牌
     * @return 外出申请处理结果
     */
    @Override
    public ApiResult outingApply(OutingRequest param, String token) {
        //健康管家为客户提出外出申请请求。后端将approvalStatus(外出申请状态)默认置为“已提交”
        ApiResult result = new ApiResult();
        OutingRecord outingRecord = new OutingRecord();
        Long userId;
        //获取token中的用户ID
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户ID不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            throw e;
        }
        // 验证客户id是否存在
        if (customerMapper.selectById(param.getCustomerId()) == null) {

            result.setCode(400);
            result.setMessage("客户id不存在");
            return result;
        }
        outingRecord.setUserId(userId);
        outingRecord.setCustomerId(param.getCustomerId());
        outingRecord.setOutingDate(param.getOutingDate());
        outingRecord.setReason(param.getOutingReason());
        outingRecord.setExpectedReturnDate(param.getReturnDate());
        outingRecord.setAccompany(param.getCompanion());
        outingRecord.setRelationship(param.getRelationship());
        outingRecord.setAccompanyTel(param.getCompanionPhone());
        outingRecord.setStatus("2");
        //向outing_record表插入数据
        try{
            outingRecordMapper.insert(outingRecord);
            result.setCode(200);
            result.setMessage("申请成功");
        }catch (Exception e){
            throw e;
        }
        return result;
    }

    /**
     * 分页查询当前健康管家的外出申请。
     *
     * @param request 外出申请查询参数
     * @param token 当前登录用户令牌
     * @return 外出申请分页列表响应
     */
    @Override
    public ApiResult<MyApplicationListVO> listmyApplications(CustomerListRequest request, String token) {
        ApiResult<MyApplicationListVO> result = new ApiResult<>();
        //获取token中的用户ID
        Long userId;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户ID不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            throw e;
        }

        String customerName = request.getCustomerName();
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();

        //根据user_id获取用户申请列表
        try {
            List<MyApplicationVO> records = outingRecordMapper.selectMyApplications(userId, pageStart, request.getPageSize(), customerName);
            int total = outingRecordMapper.countMyApplications(userId, customerName);
            result.setData(new MyApplicationListVO(records, total));
            result.setCode(200);
            result.setMessage("查询成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
            result.setMessage("数据库错误");
        }
        return result;
    }

    /**
     * 登记客户实际返回时间。
     *
     * @param id 外出记录ID
     * @param actualReturnDate 实际返回日期
     * @return 返回登记处理结果
     */
    @Override
    public ApiResult returnOuting(Long id, String actualReturnDate) {
        ApiResult result = new ApiResult();
        //根据id更新actualReturnDate
        try {
            outingRecordMapper.returnOuting(id, actualReturnDate);
            //查询客户id，根据客户Id将床位状态改为空闲
            OutingRecord outingRecord = outingRecordMapper.selectById(id);
            Customer customer = customerMapper.selectById(outingRecord.getCustomerId());
            //根据customer.getBedId())将床位设为used
            bedMapper.updateBedStatusUsed(customer.getBedId());
            result.setCode(200);
            result.setMessage("成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
            result.setMessage("数据库错误");
        }
        return result;
    }

    /**
     * 撤销客户外出申请。
     *
     * @param id 外出申请ID
     * @return 撤销处理结果
     */
    @Override
    public ApiResult cancelOuting(Long id) {
        //将状态改为已撤销(approvalStatus:“已提交”->“已撤销”)
        //如果status是2改成3
        ApiResult result = new ApiResult();
        try {
            outingRecordMapper.cancelOuting(id);
            result.setCode(200);
            result.setMessage("撤销成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("撤销失败");
        }
        return result;
    }

    /**
     * 提交客户退住申请。
     *
     * @param param 退住申请参数
     * @param token 当前登录用户令牌
     * @return 退住申请处理结果
     */
    @Override
    public ApiResult checkApply(CheckoutRequest param, String token) {
        ApiResult result = new ApiResult();
        //获取token中的用户ID
        Long userId;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户ID不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            throw e;
        }
        CheckOutRecord checkoutRecord = new CheckOutRecord();
        checkoutRecord.setUserId(userId);
        checkoutRecord.setCustomerId(param.getCustomerId());
        checkoutRecord.setCheckOutDate(param.getCheckOutDate());
        checkoutRecord.setType(switch (param.getCheckOutType()) {
            case "正常退住" -> "0";
            case "死亡退住" -> "1";
            case "保留床位" -> "2";
            default -> "3";
        });
        //如果是正常退住或死亡退住，根据customerId调用删除用户方法
        if (param.getCheckOutType().equals("正常退住") || param.getCheckOutType().equals("死亡退住")) {
            userMapper.deleteUser(param.getCustomerName());
        }
        checkoutRecord.setStatus("2");
        checkoutRecord.setReason(param.getCheckOutReason());
        try {
            checkOutRecordMapper.insert(checkoutRecord);
            result.setCode(200);
            result.setMessage("添加成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("添加失败");
            throw e;
        }
        return result;
    }

    /**
     * 撤销客户退住申请。
     *
     * @param id 退住申请ID
     * @return 撤销处理结果
     */
    @Override
    public ApiResult cancelCheckout(Long id) {
        //将状态改为已撤销(approvalStatus:“已提交”->“已撤销”)
        //如果status是2改成3
        ApiResult result = new ApiResult();
        try {
            checkOutRecordMapper.cancelCheckout(id);
            result.setCode(200);
            result.setMessage("撤销成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("撤销失败");
        }
        return result;
    }

    /**
     * 分页查询当前健康管家的退住申请。
     *
     * @param request 退住申请查询参数
     * @param token 当前登录用户令牌
     * @return 退住申请分页列表响应
     */
    @Override
    public ApiResult<MyCheckoutApplicationListVO> listmyCheckoutApplications(CustomerListRequest request, String token) {
        ApiResult<MyCheckoutApplicationListVO> result = new ApiResult<>();
        //获取token中的用户ID
        Long userId;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户ID不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            throw e;
        }

        String customerName = request.getCustomerName();
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();

        //根据user_id获取用户申请列表
        try {
            List<MyCheckoutApplicationVO> records = checkoutRecordMapper.selectMyCheckoutApplications(userId, pageStart, request.getPageSize(), customerName);
            int total = checkoutRecordMapper.countMyCheckoutApplications(userId, customerName);
            result.setData(new MyCheckoutApplicationListVO(records, total));
            result.setCode(200);
            result.setMessage("查询成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
            result.setMessage("数据库错误");
        }
        return result;
    }

    /**
     * 统计当前在住客户数量。
     *
     * @return 当前在住客户数量
     */
    @Override
    public ApiResult<Long> count() {
        // 获取客户数量
        // 变量准备
        ApiResult<Long> result = new ApiResult<>();
        Long data;
        // 数据库查询
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", "0");
        queryWrapper.eq("history","1");
        try {
            data = customerMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询客户数量数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 统计指定月份新增客户数量。
     *
     * @param date 月份字符串
     * @return 指定月份新增客户数量
     */
    @Override
    public ApiResult<Long> monthCount(String date) {
        // 获取新增客户数量
        // 变量准备
        ApiResult<Long> result = new ApiResult<>();
        Long data;
        // 数据库查询
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", "0");
        queryWrapper.eq("history","1");
        queryWrapper.like("check_in_date", date+"%");
        try {
            data = customerMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询某月客户数量失败");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 统计指定年份每月新增客户数量。
     *
     * @param year 年份字符串
     * @return 每月新增客户数量列表
     */
    @Override
    public ApiResult<List<Long>> yearCount(String year) {
        // 获取新增客户数量
        // 变量准备
        ApiResult<List<Long>> result = new ApiResult<>();
        List<Long> data = new ArrayList<>();
        // 数据库查询
        for (int i = 1; i < 13; i++){
            Long monthData;
            QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", "0");
            queryWrapper.eq("history","1");
            queryWrapper.like("check_in_date", i<10?(year+"-"+"0"+i+"%"):(year+"-"+i+"%"));
            try {
                monthData = customerMapper.selectCount(queryWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("查询某月客户数量失败");
                throw e;
            }
            data.add(monthData);
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }
}
