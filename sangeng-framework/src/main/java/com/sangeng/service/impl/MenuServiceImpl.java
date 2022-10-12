package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;

import com.sangeng.domain.vo.ListMenuVo;
import com.sangeng.domain.vo.MenuTreeSelectVo;
import com.sangeng.service.MenuService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import com.sangeng.mapper.MenuMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2022-09-21 18:13:35
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是管理员，返回所有的权限
        if (SecurityUtils.isAdmin()) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
            wrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());

            return perms;
        }
        //否则返回用户具有的权限信息
        return getBaseMapper().selectPermsByUserId(id);

    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是否是管理员
        if (SecurityUtils.isAdmin()) {
            //如果是，返回所有符合要求的Menu
             menus = menuMapper.selectAllRouterMenu();

        }else{
            //否则，返回当前用户所具有的menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }

        //构建tree
        //先找出第一层的菜单，然后去找他们的子菜单，设置到children属性中
        List<Menu> menuTree = buildMenuTree(menus,0L);

        return menuTree;
    }

    @Override
    public ResponseResult listMenu(String status, String menuName) {
        //根据状态和菜单名模糊查询
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(status), Menu::getStatus, status);
        lambdaQueryWrapper.like(StringUtils.hasText(menuName), Menu::getMenuName, menuName);

        //按照父菜单id和ordernum进行排序
        lambdaQueryWrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);

        List<Menu> menuList = list(lambdaQueryWrapper);
        List<ListMenuVo> listMenuVos = BeanCopyUtils.copyBeanList(menuList, ListMenuVo.class);
        return ResponseResult.okResult(listMenuVos);

    }

    /**
     * 判断是否存在子menu
     * @param menuId
     * @return
     */
    @Override
    public boolean hasChildren(Long menuId) {
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Menu::getParentId, menuId);
        int count = count(lambdaQueryWrapper);
        return count >= 1;
    }

    /**
     * 获得所有菜单树
     * @return
     */
    @Override
    public ResponseResult getTreeSelect() {
        //首先得到所有menu
        List<Menu> menus = list();
        //给menu填补children信息
        menus.stream().map(menu ->
                menu.setChildren(getChildren(menu, menus))).collect(Collectors.toList());
        //将menu集合转化为MenuTreeSelectVo集合
        List<MenuTreeSelectVo> menuTreeSelectVoChildren = getMenuTreeSelectVoChildren(menus).stream()
                //去重，只保留一级菜单，他们已经包含了子菜单的信息
                .filter(menuTreeSelectVo -> menuTreeSelectVo.getParentId().equals(0L)).collect(Collectors.toList());

        return ResponseResult.okResult(menuTreeSelectVoChildren);
    }

    @Override
    public ResponseResult getRoleMenuTreeSelect(Long id) {
        //首先得到菜单树
        ResponseResult treeSelect = getTreeSelect();


        //封装数据
        HashMap<String, Object> hashMap = new HashMap<>();


        hashMap.put("menus", treeSelect.getData());

        //得到角色关联的菜单
        List<String> checkedKeys = getBaseMapper().selectMenuByUserId(id);
        hashMap.put("checkedKeys", checkedKeys);
        return ResponseResult.okResult(hashMap);

    }

    private List<MenuTreeSelectVo> getMenuTreeSelectVoChildren(List<Menu> menus) {

        List<MenuTreeSelectVo> childrenList = menus.stream()
                .map(menu -> {
                    MenuTreeSelectVo menuTreeSelectVo = new MenuTreeSelectVo();
                    menuTreeSelectVo.setId(menu.getId());
                    menuTreeSelectVo.setLabel(menu.getMenuName());
                    menuTreeSelectVo.setParentId(menu.getParentId());
                    menuTreeSelectVo.setChildren(getMenuTreeSelectVoChildren(menu.getChildren()));
                    return menuTreeSelectVo;
                }).collect(Collectors.toList());

        return childrenList;
    }

    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());

        return menuTree;
    }

    /**
     * 获取传入参数的子menu集合
     *
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(m, menus)))
                .collect(Collectors.toList());
        return childrenList;
    }


}


