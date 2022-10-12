package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-09-21 18:17:45
 */
public interface RoleMapper extends BaseMapper<Role> {


    List<String> selectRoleKeyByUserId(Long id);

    void insertRoleMenu(@Param("id") Long id, @Param("menuIds") List menuIds);

    void deleteMenuById(Long id);
}


