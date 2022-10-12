package com.sangeng.controller;


import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.vo.GetMenuByIdVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.MenuService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;


    @GetMapping("/list")
    public ResponseResult listMenu(@RequestParam(required = false) String status, @RequestParam(required = false) String menuName) {
        return menuService.listMenu(status, menuName);

    }


    @PostMapping
    public ResponseResult addMenu(@RequestBody Menu menu) {
        menuService.save(menu);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult getMenuById(@PathVariable("id") Long id) {
        Menu menu = menuService.getById(id);
        if (Objects.isNull(menu)) {
            throw new SystemException(AppHttpCodeEnum.MENU_NOT_EXIST);
        }

        GetMenuByIdVo getMenuByIdVo = BeanCopyUtils.copyBean(menu, GetMenuByIdVo.class);

        return ResponseResult.okResult(getMenuByIdVo);
    }

    @PutMapping()
    public ResponseResult updateMenu(@RequestBody Menu menu) {

        if (menu.getParentId().equals(menu.getId())) {
            String errorMessage = "修改菜单" + "\'" + menu.getMenuName() + "\'" + "失败，上级菜单不能选择自己";
            return ResponseResult.errorResult(500, errorMessage);
        }

        menuService.updateById(menu);

        return ResponseResult.okResult();


    }

    @DeleteMapping("/{menuId}")
    public ResponseResult deleteMenu(@PathVariable("menuId") Long menuId) {
        //如果menu下面有子menu，则不允许删除

        boolean hasChildren = menuService.hasChildren(menuId);
        if (hasChildren) {
            String errMessage = "存在子菜单不允许删除";
            return ResponseResult.errorResult(500, errMessage);
        }

        //否则删除
        menuService.removeById(menuId);
        return ResponseResult.okResult();
    }

    @GetMapping("/treeselect")
    public ResponseResult getTreeSelect() {
        return menuService.getTreeSelect();
    }

    /**
     * 加载对应角色菜单列表树接口
     *
     * @return
     */
    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult getRoleMenuTreeSelect(@PathVariable("id") Long id) {
        return menuService.getRoleMenuTreeSelect(id);
    }
}
