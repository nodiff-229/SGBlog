package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2022-09-14 18:02:23
 */
public interface UserMapper extends BaseMapper<User> {

    void insertUserRole(@Param("id") Long id, @Param("roleIds") List<String> roleIds);

    List<String> getRoleByUserId(Long id);

    void deleteUserRole(Long id);
}


