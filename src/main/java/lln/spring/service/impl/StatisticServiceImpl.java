package lln.spring.service.impl;

import lln.spring.entity.Statistic;
import lln.spring.mapper.ArticleMapper;
import lln.spring.mapper.CommentMapper;
import lln.spring.mapper.StatisticMapper;
import lln.spring.service.IStatisticService;
import lln.spring.tools.Result;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Service
@Transactional
public class StatisticServiceImpl extends ServiceImpl<StatisticMapper, Statistic> implements IStatisticService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private StatisticMapper statisticMapper;

    @Override
    public Result getDashboardStatistics() {
        Result result = new Result();
        try {
            // 获取文章总数
            long articleCount = articleMapper.selectCount(null);
            // 获取评论总数
            long commentCount = commentMapper.selectCount(null);
            // 获取访问总量
            Integer totalHits = statisticMapper.selectTotalHits();
            if (totalHits == null) {
                totalHits = 0;
            }

            result.getMap().put("articleCount", articleCount);
            result.getMap().put("commentCount", commentCount);
            result.getMap().put("totalHits", totalHits);
            result.setMsg("获取统计数据成功！");
        } catch (Exception e) {
            result.setErrorMessage("获取统计数据失败！");
            e.printStackTrace();
        }
        return result;
    }
}
