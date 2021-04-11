package dev.chenjr.attendance.service.impl;

import dev.chenjr.attendance.dao.entity.Account;
import dev.chenjr.attendance.dao.entity.User;
import dev.chenjr.attendance.dao.mapper.AccountMapper;
import dev.chenjr.attendance.service.IAccountService;
import dev.chenjr.attendance.service.dto.LoginRequest;
import dev.chenjr.attendance.service.dto.MyUserDetail;
import dev.chenjr.attendance.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Authentication Service 认证服务，处理身份认证(登入登出，用户密码校验，Token校验，权限分配等)
 */
@Service
@Slf4j
public class AccountService extends BaseService implements IAccountService {


    @Autowired
    UserService userService;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 检验账号和密码是否匹配
     *
     * @param account     邮箱或者手机号
     * @param rawPassword 未加密的密码
     * @return 是否匹配
     */
    @Override
    public boolean checkPasswordAndAccount(String account, String rawPassword) {
        log.info("Checking: " + account + " with " + rawPassword);
        User user = userService.getUserByAccount(account);
        if (user == null) {
            log.info("User not found!");
            return false;
        }
        Account accountInfo;
        accountInfo = accountMapper.getByUid(user.getId());
        if (accountInfo == null) {
            log.info("localAuth not found!");

            return false;
        }
        String encoded = accountInfo.getToken();
        return checkPasswordHash(encoded, rawPassword);
    }

    @Override
    public boolean registerAccount(User user, String rawPassword) {
        return false;
    }

//    @Override
//    public void setUserRole(User user, String role) {
//        if (Roles.isValidRole(role)) {
//            Set<String> roles = StringUtils.commaDelimitedListToSet(user.getRoles());
//            roles.add(role);
//            user.setRoles(StringUtils.collectionToCommaDelimitedString(roles));
//        }
//        log.error("Not a valid role: " + role);
//    }
//
//    @Override
//    public void unsetUserRole(User user, String role) {
//        if (Roles.isValidRole(role)) {
//            Set<String> roles = StringUtils.commaDelimitedListToSet(user.getRoles());
//            roles.remove(role);
////            user.setRoles(StringUtils.collectionToCommaDelimitedString(roles));
//        }
//        log.error("Not a valid role: " + role);
//    }

    private String encodeHash(String password) {

        return passwordEncoder.encode(password);
    }

    @Override
    public Account getPhoneAccountInfo(long uid) {
        return this.accountMapper.getByUid(uid);
    }

    @Override
    public List<Account> getAllAccountInfo(long uid) {
        return null;
    }


    @Override
    public boolean setUserPassword(long uid, String password) {

        boolean exists = userService.userExists(uid);
        if (!exists) {
            log.info(String.format("setting password for %d to %s, uid not exists", uid, password));
            return false;
        }

        User user = userService.getUserById(uid);
        String passwordHash = encodeHash(password);
        // 先尝试查询已有的账号
        List<Account> accounts = this.getAllAccountInfo(uid);
        if (accounts == null || accounts.size() == 0) {
            // 新建登陆账号信息
            accounts = Arrays.asList(
                    Account.fromLoginName(uid, user.getLoginName(), passwordHash),
                    Account.fromPhone(uid, user.getPhone(), passwordHash),
                    Account.fromEmail(uid, user.getEmail(), passwordHash)
            );
            accounts.forEach(accountMapper::insert);
        } else {
            // 修改已有账号信息
            accounts.forEach(account -> {
                account.setToken(passwordHash);
                accountMapper.update(account, null);
            });

        }
        return true;
    }


    /**
     * 登录并创建Token 注册后也会调用这个过程
     *
     * @param loginRequest 登录请求
     * @return Token
     */
    @Override
    public String createUserToken(LoginRequest loginRequest) {
        Account accountInfo = accountMapper.getByAccount(loginRequest.getAccount());
        if (accountInfo == null) {
            throw new UsernameNotFoundException("can not found account");
        }
        User user = userService.getUserByAccount(loginRequest.getAccount());
        if (accountInfo.getLocked()) {
            throw new BadCredentialsException("User is forbidden to login");
        }
        if (!this.checkPasswordHash(loginRequest.getPassword(), accountInfo.getToken())) {
            throw new BadCredentialsException("The user name or password is not correct.");
        }

        // TODO 获取用户角色信息
        UserDetails details = new MyUserDetail(user, accountInfo.getToken(), AuthorityUtils.NO_AUTHORITIES);
        return jwtTokenUtil.generateToken(details);
    }

    @Override
    public boolean checkPasswordHash(String encodedPassword, String rawPassword) {

        log.info("encoded: " + encodedPassword + " raw: " + rawPassword);
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public Account getAccountInfo(long uid, String type) {
        return null;
    }
//    /**
//     * 登录认证换取JWT令牌
//     *
//     * @param account
//     * @param password
//     * @return
//     */
//    public String login(String account, String password) {
//        //用户验证
//        Authentication authentication = null;
//        try {
//            // 进行身份验证,
//            authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(account, password));
//        } catch (Exception e) {
//            throw new RuntimeException("用户名密码错误");
//        }
//
//        UserDetail loginUser = (UserDetail) authentication.getPrincipal();
//        // 生成token
//        return jwtTokenUtil.generateToken(loginUser);
//
//    }

    @Override
    public boolean accountExists(long accountId) {
        return this.accountMapper.exists(accountId) != null;
    }

    @Override
    public boolean accountExists(String account) {
        return this.accountMapper.existsByAccount(account) != null;
    }

    @Override
    public User currentUser() {

        throw new AuthenticationCredentialsNotFoundException("Cannot found current user!");

    }

    public static final class Roles {
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_TEACHER = "ROLE_TEACHER";
        public static final String ROLE_STUDENT = "ROLE_STUDENT";
        public static final HashSet<String> allRoles = new HashSet<>(Arrays.asList(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN));

        public static boolean isValidRole(String role) {
            return allRoles.contains(role);
        }

        public static boolean isValidRoles(String roles) {
            if (roles != null) {
                return allRoles.containsAll(Arrays.asList(roles.split(",")));
            } else {
                return false;
            }
        }
    }
}