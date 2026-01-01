package lln.spring.service;

import lln.spring.entity.Statistic;
import com.baomidou.mybatisplus.extension.service.IService;
import lln.spring.tools.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
public interface IStatisticService extends IService<Statistic> {
    /**
     * 获取仪表盘统计数据
     * @return 包含文章总数、评论总数、访问总量的数据
     */
    Result getDashboardStatistics();
}
