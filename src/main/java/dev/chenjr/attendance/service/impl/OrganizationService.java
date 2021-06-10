package dev.chenjr.attendance.service.impl;

import dev.chenjr.attendance.service.IOrganizationService;
import dev.chenjr.attendance.service.dto.OrganizationDTO;
import dev.chenjr.attendance.service.dto.PageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrganizationService implements IOrganizationService {
    /**
     * 返回指定类型的组织结构
     *
     * @param orgType  类型英文名
     * @param curPage  当前页面
     * @param pageSize 页面大小
     * @return 分页的组织结构数据
     */
    @Override
    public PageWrapper<OrganizationDTO> listPage(String orgType, long curPage, long pageSize) {
        return null;
    }

    /**
     * 获取某个节点的信息
     *
     * @param orgId 节点id
     * @return 节点信息
     */
    @Override
    public OrganizationDTO fetch(long orgId) {
        return null;
    }

    /**
     * 修改某个节点信息，不改变其儿子节点
     *
     * @param orgDTO 要修改的信息
     */
    @Override
    public OrganizationDTO modify(OrganizationDTO orgDTO) {
        return null;
    }

    /**
     * 创建节点，会递归创建子类
     *
     * @param organizationDTO 待创建的节点
     * @return 创建成功的返回，和getOrg同样的返回
     */
    @Override
    public OrganizationDTO create(OrganizationDTO organizationDTO) {
        return null;
    }

    /**
     * 删除节点，不会级联删除儿子节点
     *
     * @param orgId 要删除id
     */
    @Override
    public void delete(long orgId) {

    }
}