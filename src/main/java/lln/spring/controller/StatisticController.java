package lln.spring.controller;

import lln.spring.service.IStatisticService;
import lln.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@RestController
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    private IStatisticService statisticService;

    @PostMapping("/getDashboardStatistics")
    public Result getDashboardStatistics() {
        Result result = new Result();
        try {
            result = statisticService.getDashboardStatistics();
        } catch (Exception e) {
            result.setErrorMessage("获取统计数据失败！");
            e.printStackTrace();
        }
        return result;
    }

}
