package com.egao.common.core.shiro;

import com.egao.common.system.entity.User;
import com.egao.common.system.service.UserRoleService;
import com.egao.common.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * Shiro认证和授权
 */
public class UserRealm extends AuthorizingRealm {
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
            if (i==3) flag=true;
        }
        User user = userService.getByUsername(username);
        if (!flag) throw new UnknownAccountException(); // 账号不存在
        if (user.getState() != 0) throw new LockedAccountException();  // 账号被锁定
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}

}
