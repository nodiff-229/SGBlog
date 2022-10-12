package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.vo.LinkVo;
import com.sangeng.domain.vo.ListLinkVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.service.LinkService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import com.sangeng.domain.entity.Link;
import com.sangeng.mapper.LinkMapper;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2022-09-14 17:42:23
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        //查询所有审核通过的
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);

        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);

        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult listLink(Long pageNum, Long pageSize, String name, String status) {
        LambdaQueryWrapper<Link> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(name), Link::getName, name);
        lambdaQueryWrapper.eq(StringUtils.hasText(status), Link::getStatus, status);
        Page<Link> page = new Page<>(pageNum, pageSize);
        page(page, lambdaQueryWrapper);
        List<Link> records = page.getRecords();
        List<ListLinkVo> listLinkVos = BeanCopyUtils.copyBeanList(records, ListLinkVo.class);
        return ResponseResult.okResult(new PageVo(listLinkVos, page.getTotal()));
    }

}


