package com.upic.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

import com.upic.condition.OperatorRoleCondition;
import com.upic.condition.ResourceCondition;
import com.upic.condition.RoleResourceCondition;
import com.upic.dto.OperatorInfo;
import com.upic.dto.OperatorRoleInfo;
import com.upic.dto.ResourceInfo;
import com.upic.dto.RoleResourceInfo;
import com.upic.dto.UserInfo;
import com.upic.enums.UserTypeEnum;
import com.upic.service.OperatorRoleService;
import com.upic.service.OperatorService;
import com.upic.service.ProjectCategoryService;
import com.upic.service.ResourceService;
import com.upic.service.RoleCheckStatusService;
import com.upic.service.RoleResourceService;
import com.upic.service.UserService;
import com.upic.social.user.SocialUsers;

@Component
public class MyUserDetailsService implements UserDetailsService, SocialUserDetailsService {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleCheckStatusService roleCheckStatusService;

    @Autowired
    private ProjectCategoryService projectCategoryService;

    // @Autowired
    // private RoleService roleService;

    @Autowired
    private OperatorRoleService operatorRoleService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private ResourceService resourceService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUserId(username);
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        UserInfo userInfo = userService.getUserByUserNum(userId);
        OperatorRoleCondition operatorRoleCondition = new OperatorRoleCondition();

        RoleResourceCondition roleResourceCondition = new RoleResourceCondition();

        ResourceCondition resourceCondition = new ResourceCondition();

        if (userInfo == null) {
            throw new UsernameNotFoundException("用户名不存在，请联系管理员！");
        }

        operatorRoleCondition.setJobNum(userInfo.getUserNum());

        OperatorInfo o = operatorService.getByJobNum(userInfo.getUserNum());

        List<GrantedAuthority> createAuthorityList = null;

        // 加载所有菜单角色资源
        List<RoleResourceInfo> listAll = new ArrayList<RoleResourceInfo>();

        // 准备审批字段
        List<String> checkList = new ArrayList<String>();

        // 所有的菜單
        List<ResourceInfo> resourceList = new ArrayList<ResourceInfo>();

        // 获取所有项目类别
        List<String> categoryName = new ArrayList<String>();

        createAuthorityList = AuthorityUtils.createAuthorityList("/*");
        // 用户基本身份类别
        UserTypeEnum u = userInfo.getType().equals(UserTypeEnum.STUDENT) ? UserTypeEnum.STUDENT : UserTypeEnum.TEACHER;
        Page<OperatorRoleInfo> searchOperatorRole = operatorRoleService.searchOperatorRole(operatorRoleCondition,
                new PageRequest(1, 150));
        initData(searchOperatorRole, listAll, roleResourceCondition, resourceCondition, resourceList, checkList,
                categoryName, o, u);
        return new SocialUsers(userId, "", createAuthorityList, userInfo.getUsername(), userInfo.getCollege(),
                userInfo.getMajor(), checkList, categoryName, resourceList);
    }

    private void initData(Page<OperatorRoleInfo> searchOperatorRole, List<RoleResourceInfo> listAll,
                          RoleResourceCondition roleResourceCondition, ResourceCondition resourceCondition,
                          List<ResourceInfo> resourceList, List<String> checkList, List<String> categoryName, OperatorInfo o,
                          UserTypeEnum u) {
        // 根据角色ID找出所有菜单 foreach
        searchOperatorRole.getContent().stream().parallel().forEach(x -> {
            // 根据角色id找出资源
            roleResourceCondition.setRoleId(x.getRoleId());
            listAll.addAll(roleResourceService.findAll(roleResourceCondition));
            if (u.equals(UserTypeEnum.TEACHER)) {
                // 所有审批状态
                checkList.addAll(roleCheckStatusService.getCheckStatusEnumName(x.getRoleId()));
            }
        });
        listAll.stream().parallel().forEach(x -> {
            resourceCondition.setResourceNum(x.getResourceNum());
            resourceList.addAll(resourceService.listResource(resourceCondition));
        });
        if (u.equals(UserTypeEnum.TEACHER)) {
            // 获取所有项目类别
            categoryName = projectCategoryService.getCategoryNameBySubordinateSectorOtherName(o.getCollegeOtherName());
        }
    }
}
