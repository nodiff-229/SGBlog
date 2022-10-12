package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddRoleDto;
import com.sangeng.domain.dto.UpdateRoleDto;
import com.sangeng.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-09-21 18:17:45
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);


    ResponseResult listRoles(Long pageNum, Long pageSize, String roleName, String status);

    ResponseResult addRole(AddRoleDto addRoleDto);

    ResponseResult updateRole(UpdateRoleDto updateRoleDto);
}


