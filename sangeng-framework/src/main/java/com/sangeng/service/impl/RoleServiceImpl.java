package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddRoleDto;
import com.sangeng.domain.dto.UpdateRoleDto;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.vo.DataListRoleVo;
import com.sangeng.domain.vo.ListRoleVo;
import com.sangeng.mapper.RoleMapper;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-09-21 18:17:45
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否为管理员，如果是，返回集合中只需要有admin
        if (id == 1L) {
            ArrayList<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }

        //否则查询用户所具有的角色信息

        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult listRoles(Long pageNum, Long pageSize, String roleName, String status) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName);
        lambdaQueryWrapper.like(StringUtils.hasText(status), Role::getStatus, status);
        Page<Role> pages = this.page(page, lambdaQueryWrapper);
        List<Role> roles = pages.getRecords();
        List<ListRoleVo> listRoleVos = BeanCopyUtils.copyBeanList(roles, ListRoleVo.class);
        DataListRoleVo dataListRoleVo = new DataListRoleVo(listRoleVos, pages.getTotal());

        return ResponseResult.okResult(dataListRoleVo);
    }

    @Override
    public ResponseResult addRole(AddRoleDto addRoleDto) {
        Role role = BeanCopyUtils.copyBean(addRoleDto, Role.class);
        save(role);
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Role::getRoleName, role.getRoleName());
        List<Role> list = list(lambdaQueryWrapper);
        Role thisRole = list.get(0);

        getBaseMapper().insertRoleMenu(thisRole.getId(), addRoleDto.getMenuIds());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateRole(UpdateRoleDto updateRoleDto) {
        //先把角色其他信息更新了
        Role role = BeanCopyUtils.copyBean(updateRoleDto, Role.class);
        updateById(role);
        //删除角色原来的菜单信息
        getBaseMapper().deleteMenuById(updateRoleDto.getId());

        //更新角色对应的菜单信息
        getBaseMapper().insertRoleMenu(updateRoleDto.getId(), updateRoleDto.getMenuIds());

        return null;
    }


}


