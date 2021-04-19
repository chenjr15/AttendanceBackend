package dev.chenjr.attendance.controller;

import dev.chenjr.attendance.dao.entity.User;
import dev.chenjr.attendance.exception.RegisterException;
import dev.chenjr.attendance.service.dto.*;
import dev.chenjr.attendance.service.impl.AccountService;
import dev.chenjr.attendance.service.impl.SmsService;
import dev.chenjr.attendance.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "用户", description = "用户CRUD")
public class UserController {

    @Autowired
    AccountService accountService;
    @Autowired
    UserService userService;

    @Autowired
    SmsService smsService;

    @GetMapping("")
    @Operation(description = "获取用户列表")
    @ResponseBody
    public RestResponse<List<UserInfoResponse>> listUsers(
            @RequestParam(defaultValue = "1") long curPage,
            @RequestParam(defaultValue = "10") long pageSize) {
        List<UserInfoResponse> users = this.userService.getUsers(curPage, pageSize);
        return RestResponse.okWithData(users);
    }

    @PostMapping("")
    @Operation(description = "注册")
    @ResponseBody
    public RestResponse<TokenUidDTO> register(@RequestBody @Validated RegisterRequest request) {
        if (!smsService.codeValid(request.getPhone(), "register", request.getSmsCode())) {
            throw new RegisterException("sms code mismatch");
        }
        // 尝试创建Token，失败会报错
        String token;
        User user = userService.register(request);
        if (user == null) {
            throw new RegisterException("注册失败！");
        }
        token = accountService.createToken(user);
        return new RestResponse<>(HttpStatus.OK.value(), "注册成功！", new TokenUidDTO(token, user.getId()));
    }

    @GetMapping("/{uid}")
    @Operation(description = "获取指定用户的信息")
    @ResponseBody
    public RestResponse<?> getUser(@PathVariable Long uid) {
        User user = userService.getUserById(uid);
        return RestResponse.okWithData(user);
    }

    @DeleteMapping("/{uid}")
    @Operation(description = "注销帐户")
    @ResponseBody
    public RestResponse<?> deleteUser(@PathVariable Integer uid) {
        return RestResponse.notImplemented();
    }


    @PatchMapping("/{uid}")
    @Operation(description = "修改用户信息")
    @ResponseBody
    public RestResponse<?> modifyUser(@PathVariable Integer uid, @RequestBody InputModifyUserDTO modifyUserRequest) {
        return RestResponse.notImplemented();
    }
}
