package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddLinkDto;
import com.sangeng.domain.entity.Link;
import com.sangeng.domain.vo.ListLinkVo;
import com.sangeng.service.LinkService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult listLink(Long pageNum, Long pageSize, @RequestParam(required = false) String name, @RequestParam(required = false) String status) {
        return linkService.listLink(pageNum, pageSize, name, status);
    }

    @PostMapping()
    public ResponseResult addLink(@RequestBody AddLinkDto addLinkDto) {

        Link link = BeanCopyUtils.copyBean(addLinkDto, Link.class);
        linkService.save(link);
        return ResponseResult.okResult();

    }

    @GetMapping("/{id}")
    public ResponseResult getLinkById(@PathVariable("id") Long id) {
        Link link = linkService.getById(id);
        ListLinkVo listLinkVo = BeanCopyUtils.copyBean(link, ListLinkVo.class);
        return ResponseResult.okResult(listLinkVo);

    }

    @PutMapping
    public ResponseResult updateLink(@RequestBody ListLinkVo listLinkVo) {
        Link link = linkService.getById(listLinkVo.getId());
        link.setAddress(listLinkVo.getAddress());
        link.setLogo(listLinkVo.getLogo());
        link.setName(listLinkVo.getName());
        link.setAddress(listLinkVo.getAddress());
        link.setDescription(listLinkVo.getDescription());
        link.setStatus(listLinkVo.getStatus());
        linkService.updateById(link);
        return ResponseResult.okResult();
    }
    @DeleteMapping("/{id}")
    public ResponseResult deleteLink(@PathVariable("id") Long id) {
        linkService.removeById(id);
        return ResponseResult.okResult();

    }
}
