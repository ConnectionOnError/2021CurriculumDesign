package com.egao.common.core.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.egao.common.system.entity.Role;
import com.egao.common.system.entity.User;
import com.egao.common.system.service.UserRoleService;
import com.egao.common.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.sql.Wrapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shiro认证和授权
 * Created by wangfan on 2017-04-28 09:45
 */
public class AdminRealm extends AuthorizingRealm {
    @Lazy
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        Boolean flag=false;
        Integer[] roles=userRoleService.getRoleIds(userService.getByUsername(username).getUserId().toString());
        for (Integer i :roles){
            if (i==1||i==2) flag=true;
        }
        User user = userService.getByUsername(username);
        if (!flag) throw new UnknownAccountException(); // 账号不存在
        if (user.getState() != 0) throw new LockedAccountException();  // 账号被锁定
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        userService.selectRoleAndAuth(user);
        // 角色
        Set<String> roles = new HashSet<>();
        for (Role r : user.getRoles()) {
            if (r.getDeleted() == 0) roles.add(r.getRoleCode());
        }
        authorizationInfo.setRoles(roles);
        // 权限
        Set<String> permissions = new HashSet<>();
        for (String auth : user.getAuthorities()) {
            if (auth != null && !auth.trim().isEmpty()) permissions.add(auth);
        }
        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }

}