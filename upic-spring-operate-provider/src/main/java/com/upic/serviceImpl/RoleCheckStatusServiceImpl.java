package com.upic.serviceImpl;

import com.upic.common.beans.utils.UpicBeanUtils;
import com.upic.common.support.spec.domain.AbstractDomain2InfoConverter;
import com.upic.common.support.spec.domain.converter.QueryResultConverter;
import com.upic.condition.RoleCheckStatusCondition;
import com.upic.dto.RoleCheckStatusInfo;
import com.upic.dto.RoleInfo;
import com.upic.po.RoleCheckStatus;
import com.upic.repository.RoleCheckStatusRepository;
import com.upic.repository.Spec.RoleCheckStatusSpec;
import com.upic.service.RoleCheckStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhubuqing on 2017/9/11.
 */
@Service("roleCheckStatusService")
public class RoleCheckStatusServiceImpl implements RoleCheckStatusService {
    @Autowired
    private RoleCheckStatusRepository roleCheckStatusRepository;

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleCheckStatusServiceImpl.class);

    @Override
    public RoleCheckStatusInfo addRoleCheckStatus(RoleCheckStatusInfo roleCheckStatusInfo) {
        try {
            RoleCheckStatus roleCheckStatus = new RoleCheckStatus();
            UpicBeanUtils.copyProperties(roleCheckStatusInfo, roleCheckStatus);
            roleCheckStatus = roleCheckStatusRepository.save(roleCheckStatus);
            UpicBeanUtils.copyProperties(roleCheckStatus, roleCheckStatusInfo);
            return roleCheckStatusInfo;
        } catch (Exception e) {
            LOGGER.info("addRoleCheckStatus。错误信息：" + e.getMessage());
            return null;
        }
    }

    @Override
    public RoleCheckStatusInfo updateRoleCheckStatus(RoleCheckStatusInfo roleCheckStatusInfo) {
        try {
            RoleCheckStatus roleCheckStatus = new RoleCheckStatus();
            UpicBeanUtils.copyProperties(roleCheckStatusInfo, roleCheckStatus);
            roleCheckStatus = roleCheckStatusRepository.saveAndFlush(roleCheckStatus);
            UpicBeanUtils.copyProperties(roleCheckStatus, roleCheckStatusInfo);
            return roleCheckStatusInfo;
        } catch (Exception e) {
            LOGGER.info("updateRoleCheckStatus。错误信息：" + e.getMessage());
            return null;
        }
    }

    @Override
    public Page<RoleCheckStatusInfo> searchRoleCheckStatus(RoleCheckStatusCondition roleCheckStatusCondition, Pageable pageable) {
        Page<RoleCheckStatus> roleCheckStatusPage = null;
        try {
            roleCheckStatusPage = roleCheckStatusRepository.findAll(new RoleCheckStatusSpec(roleCheckStatusCondition), pageable);
            return QueryResultConverter.convert(roleCheckStatusPage, pageable, new AbstractDomain2InfoConverter<RoleCheckStatus, RoleCheckStatusInfo>() {
                @Override
                protected void doConvert(RoleCheckStatus domain, RoleCheckStatusInfo info) throws Exception {
                    UpicBeanUtils.copyProperties(domain, info);
                }
            });
        } catch (Exception e) {
            LOGGER.info("searchRoleCheckStatus。错误信息：" + e.getMessage());
            return null;
        }
    }

    @Override
    public RoleCheckStatusInfo getRoleCheckStatusById(long roleCheckStatusId) {
        try {
            RoleCheckStatus roleCheckStatus = roleCheckStatusRepository.findOne(roleCheckStatusId);
            RoleCheckStatusInfo roleCheckStatusInfo = new RoleCheckStatusInfo();
            UpicBeanUtils.copyProperties(roleCheckStatus, roleCheckStatusInfo);
            return roleCheckStatusInfo;
        } catch (Exception e) {
            LOGGER.info("getRoleCheckStatusById。错误信息：" + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteRoleCheckStatus(long roleCheckStatusId) {
        try {
            roleCheckStatusRepository.delete(roleCheckStatusId);
        } catch (Exception e) {
            LOGGER.info("deleteRoleCheckStatus。错误信息：" + e.getMessage());
        }
    }

    /**
     * Important！！！重要！！！！获取审批状态列表
     *
     * @param roleId
     * @return
     */
    @Override
    public List<String> getCheckStatusEnumName(long roleId) {
        List<String> checkStatusList = new ArrayList<>();
        try {
            List<RoleCheckStatus> roleCheckStatusList = roleCheckStatusRepository.findByRoleId(roleId);
            for (RoleCheckStatus roleCheckStatus : roleCheckStatusList) {
                checkStatusList.add(roleCheckStatus.getEnumName());
            }
            return checkStatusList;
        } catch (Exception e) {
            LOGGER.info("getCheckStatusEnumName。错误信息：" + e.getMessage());
            return null;
        }
    }

    @Override
    public String addNewRoleCheckStatus(RoleInfo roleInfo) {
        try {
            List<String> checkStatuses = getCheckStatusByRank(roleInfo.getRank());
            for (String checkStatus : checkStatuses) {
                RoleCheckStatus roleCheckStatus = roleCheckStatusRepository.getByRoleIdAndEnumName(roleInfo.getId(), checkStatus);
                if (roleCheckStatus == null) {
                    roleCheckStatus = new RoleCheckStatus();
                    roleCheckStatus.setEnumName(checkStatus);
                    roleCheckStatus.setRoleId(roleInfo.getId());
                    roleCheckStatus.setCreatTime(new Date());
                    roleCheckStatus.setRoleName(roleInfo.getRoleName());
                    roleCheckStatusRepository.save(roleCheckStatus);
                }
            }
            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.info("addNewRoleCheckStatus。错误信息：" + e.getMessage());
            return null;
        }
    }

    private List<String> getCheckStatusByRank(int rank) {
        List<String> checkStatuses = new ArrayList<>();
        switch (rank) {
            case 1:
                checkStatuses.add("PENDING_AUDIT_FINAL");
                checkStatuses.add("IN_AUDIT_FINAL");
                checkStatuses.add("CHECKING_FINAL");
                break;
            case 2:
                checkStatuses.add("PENDING_AUDIT_AGAIN");
                checkStatuses.add("IN_AUDIT_AGAIN");
                checkStatuses.add("CHECKING_AGAIN");
                break;
            case 3:
                checkStatuses.add("PENDING_AUDIT");
                checkStatuses.add("IN_AUDIT");
                checkStatuses.add("CHECKING");
                checkStatuses.add("PENDING_AUDIT_BEFORE");
        }
        return checkStatuses;
    }
}