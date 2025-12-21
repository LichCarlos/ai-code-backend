package com.carlos.aicodebackend.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.carlos.aicodebackend.model.dto.AppAdminQueryRequest;
import com.carlos.aicodebackend.model.dto.AppQueryRequest;
import com.carlos.aicodebackend.model.entity.App;
import com.carlos.aicodebackend.model.entity.User;
import com.carlos.aicodebackend.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用视图对象
     *
     * @param app 应用实体
     * @return 应用视图对象
     */
    AppVO getAppVO(App app);

    /**
     * 获取应用视图对象列表
     *
     * @param appList 应用实体列表
     * @return 应用视图对象列表
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 获取用户查询条件
     *
     * @param appQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取精选应用查询条件
     *
     * @param appQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getFeaturedQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取管理员查询条件
     *
     * @param appAdminQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getAdminQueryWrapper(AppAdminQueryRequest appAdminQueryRequest);

    /**
     * 应用聊天生成代码
     *
     * @param appId    应用 ID
     * @param message  用户消息
     * @param loginUser 登录用户
     * @return 生成结果流
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 部署应用
     *
     * @param appId     应用 ID
     * @param loginUser 登录用户
     * @return 部署 URL
     */
    String deployApp(Long appId, User loginUser);
}
