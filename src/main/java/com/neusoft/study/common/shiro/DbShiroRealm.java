package com.neusoft.study.common.shiro;

import com.neusoft.study.common.shiro.utils.JwtUtils;
import com.neusoft.study.user.entity.UserInfo;
import com.neusoft.study.user.service.IUserService;
import com.neusoft.study.user.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;


/**
 * <p>Title: com.neusoft.study.common.shiro</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/30 0030 9:41
 * Description: No Description
 */
@Slf4j
public class DbShiroRealm extends AuthorizingRealm {


    @Autowired
    @Qualifier("userServiceImpl")
    private IUserService userService;

    public DbShiroRealm() {
    }

    /**
     *  找它的原因是这个方法返回true
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     *  这一步我们根据token给的用户名，去数据库查出加密过用户密码，然后把加密后的密码和盐值一起发给shiro，让它做比对
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken userpasswordToken = (UsernamePasswordToken) token;
        String account = userpasswordToken.getUsername();
        UserInfo userInfo = userService.getUserInfo(account);
        return new SimpleAuthenticationInfo(userInfo, userInfo.getEncryptPwd(),
                ByteSource.Util.bytes(userInfo.getPasswrdSalt()), getName());
    }

    /**
     * 这里需要注意一下的就是Shiro默认不会缓存角色信息，所以这里调用service的方法获取角色强烈建议从缓存中获取
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();
        List<String> roles = userInfo.getRoles();
        if(roles == null) {
            roles = userService.getUserRoles(userInfo.getUserId());
            userInfo.setRoles(roles);
        }
        if (roles != null){
            simpleAuthorizationInfo.addRoles(roles);
        }
        return simpleAuthorizationInfo;
    }


    /**
     * 注册的时候，密码必须通过MD5加密并散列三次，存入数据库
     * @param args
     */
    public static void main(String[] args) {
        //生成盐（部分，需要存入数据库中）
        String random = JwtUtils.generateSalt();
        //将原始密码加盐（上面生成的盐），并且用md5算法加密三次，将最后结果存入数据库中
        String result = new Md5Hash("123456",random,3).toString();

        System.out.println(random);

        System.out.println(result);
    }
}
