package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddUserDto;
import com.sangeng.domain.dto.UpdateUserDto;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.vo.UpdateUserVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.RoleMapper;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sangeng.domain.entity.User;
import com.sangeng.mapper.UserMapper;
import com.sangeng.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2022-09-14 18:02:25
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;




    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();

        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);


        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断
        if (!StringUtils.hasText(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getNickName())) {
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }


        //对数据进行是否重复的判断
        if (userNameExist(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);



        //存入数据库
        save(user);





        return ResponseResult.okResult();


    }

    @Override
    public ResponseResult listUser(Long pageNum, Long pageSize, String userName, String phonenumber, String status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(userName), User::getUserName, userName);
        lambdaQueryWrapper.like(StringUtils.hasText(phonenumber), User::getPhonenumber, phonenumber);
        lambdaQueryWrapper.like(StringUtils.hasText(status), User::getStatus, status);
        Page<User> userPage = page(page, lambdaQueryWrapper);
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("rows", userPage.getRecords());
        hashMap.put("total", userPage.getTotal());

        return ResponseResult.okResult(hashMap);

    }


    @Override
    public ResponseResult addUser(AddUserDto addUserDto) {
        //对数据进行非空判断
        if (!StringUtils.hasText(addUserDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }

        //用户名必须未存在
        if (userNameExist(addUserDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        //邮箱未存在
        if (emailExist(addUserDto.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        //手机号未存在
        if (phoneNumberExist(addUserDto.getPhonenumber())) {
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }

        User user = BeanCopyUtils.copyBean(addUserDto, User.class);

        //对密码进行加密
        String encodePassword = passwordEncoder.encode(addUserDto.getPassword());
        user.setPassword(encodePassword);

        save(user);

        //设置用户角色
        getBaseMapper().insertUserRole(user.getId(), addUserDto.getRoleIds());

        return ResponseResult.okResult();

    }

    /**
     * 根据id查询用户信息回显接口
     * @param id
     * @return
     */
    @Override
    public ResponseResult getUserById(Long id) {
        User user = getById(id);
        List<String> roleIds = getBaseMapper().getRoleByUserId(id);
        UpdateUserVo updateUserVo = BeanCopyUtils.copyBean(user, UpdateUserVo.class);
        List<Role> roles = roleIds.stream().map(roleId -> roleService.getById(roleId)).collect(Collectors.toList());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("roleIds", roleIds);
        hashMap.put("roles", roles);
        hashMap.put("user", updateUserVo);
        return ResponseResult.okResult(hashMap);

    }

    @Override
    public ResponseResult updateUser(UpdateUserDto updateUserDto) {
        User user = getById(updateUserDto.getId());
        user.setEmail(updateUserDto.getEmail());
        user.setNickName(updateUserDto.getNickName());
        user.setPhonenumber(updateUserDto.getPhonenumber());
        user.setStatus(updateUserDto.getStatus());
        user.setSex(updateUserDto.getSex());
        updateById(user);
        getBaseMapper().deleteUserRole(updateUserDto.getId());
        getBaseMapper().insertUserRole(updateUserDto.getId(), updateUserDto.getRoleIds());
        return ResponseResult.okResult();
    }

    private boolean emailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        int count = count(queryWrapper);
        return count > 0;
    }

    private boolean phoneNumberExist(String phonenumber) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhonenumber, phonenumber);
        int count = count(queryWrapper);
        return count > 0;
    }


    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        int count = count(queryWrapper);
        return count > 0;
    }


}


