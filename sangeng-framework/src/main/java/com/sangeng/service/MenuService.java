package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2022-09-21 18:13:35
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long userId);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult listMenu(String status, String menuName);

    boolean hasChildren(Long menuId);

    ResponseResult getTreeSelect();

    ResponseResult getRoleMenuTreeSelect(Long id);
}


