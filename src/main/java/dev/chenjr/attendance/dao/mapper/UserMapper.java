package dev.chenjr.attendance.dao.mapper;

import dev.chenjr.attendance.dao.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * <p>
 * 用户表，所有系统内的用户都在这个表里 Mapper 接口
 * </p>
 *
 * @author chenjr
 * @since 2021-04-11
 */
public interface UserMapper extends MyBaseMapper<User> {
    @Override
    @Select("SELECT 1 FROM user WHERE id=#{id} limit 1 ")
    Optional<Boolean> exists(@Param("id") long id);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User getByEmail(@Param("email") String email);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User getByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM user WHERE login_name = #{loginName}")
    User getByLoginName(@Param("loginName") String loginName);

    @Select("SELECT 1 FROM user WHERE phone = #{phone} limit 1 ")
    Boolean phoneExists(@Param("phone") String phone);

    @Select("SELECT 1 FROM user WHERE email = #{email} limit 1 ")
    Boolean emailExists(@Param("email") String email);

    @Select("SELECT 1 FROM user WHERE login_name = #{loginName} limit 1 ")
    Boolean loginNameExists(@Param("loginName") String loginName);

    @Select("SELECT real_name FROM user WHERE id=#{id} limit 1 ")
    String getRealNameById(Long id);

    @Select("SELECT id,real_name,academic_id FROM user WHERE id=#{uid} limit 1")
    User getStudent(Long uid);
}
