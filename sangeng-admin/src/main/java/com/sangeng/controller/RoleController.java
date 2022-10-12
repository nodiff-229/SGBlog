package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddRoleDto;
import com.sangeng.domain.dto.ChangeRoleStatusDto;
import com.sangeng.domain.dto.UpdateRoleDto;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.vo.UpdateRoleVo;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;


    @GetMapping("/list")
    public ResponseResult listRoles(Long pageNum, Long pageSize, @RequestParam(required = false) String roleName, @RequestParam(required = false) String status) {

        return roleService.listRoles(pageNum, pageSize, roleName, status);

    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeRoleStatusDto changeRoleStatusDto) {
        Role role = roleService.getById(changeRoleStatusDto.getRoleId());
        role.setStatus(changeRoleStatusDto.getStatus());
        roleService.updateById(role);
        return ResponseResult.okResult();
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody AddRoleDto addRoleDto) {

        return roleService.addRole(addRoleDto);

    }

    /**
     * 角色信息回显接口
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getRoleInformation(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);
        UpdateRoleVo updateRoleVo = BeanCopyUtils.copyBean(role, UpdateRoleVo.class);
        return ResponseResult.okResult(updateRoleVo);
    }

    @PutMapping()
    public ResponseResult updateRole(@RequestBody UpdateRoleDto updateRoleDto) {
        return roleService.updateRole(updateRoleDto);

    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteRole(@PathVariable("id")Long id) {
        roleService.removeById(id);
        return ResponseResult.okResult();
    }


    @GetMapping("/listAllRole")
    public ResponseResult listAllRole() {
        List<Role> roles = roleService.list();
        return ResponseResult.okResult(roles);
    }

}
