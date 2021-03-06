package dev.chenjr.attendance.service;

import dev.chenjr.attendance.service.dto.OrganizationDTO;
import dev.chenjr.attendance.service.dto.PageSort;
import dev.chenjr.attendance.service.dto.PageWrapper;

import java.util.List;

public interface IOrganizationService extends IService {
    
    
    /**
     * 获取某个节点的信息, 返回一级子节点
     *
     * @param orgId 节点id
     * @return 节点信息
     */
    OrganizationDTO fetch(long orgId);
    
    /**
     * 获取某个节点的信息, 返回一级子节点
     *
     * @param orgId    节点id
     * @param pageSort 子节点的分页排序筛选信息
     * @return 子节点信息
     */
    OrganizationDTO listChildren(long orgId, PageSort pageSort);
    
    /**
     * 修改某个节点信息，不改变其孩子节点
     *
     * @param orgDTO 要修改的信息
     */
    OrganizationDTO modify(OrganizationDTO orgDTO);
    
    /**
     * 创建节点，会递归创建子类
     *
     * @param organizationDTO 待创建的节点
     * @return 创建成功的返回，和getOrg同样的返回
     */
    OrganizationDTO create(OrganizationDTO organizationDTO);
    
    /**
     * 删除节点，不会级联删除孩子节点
     *
     * @param orgId 要删除id
     */
    void delete(long orgId);
    
    PageWrapper<OrganizationDTO> listPage(String orgType, PageSort pageSort);
    
    /**
     * 不查找其孩子节点的fetch
     *
     * @param orgId 节点id
     * @return 节点信息
     */
    OrganizationDTO fetchItSelf(long orgId);
    
    /**
     * 批量删除
     *
     * @param orgIds 组织结构id
     */
    void delete(List<Long> orgIds);
}
