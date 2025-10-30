package lln.spring.service.impl;

import lln.spring.entity.Statistic;
import lln.spring.mapper.StatisticMapper;
import lln.spring.service.IStatisticService;
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


}
