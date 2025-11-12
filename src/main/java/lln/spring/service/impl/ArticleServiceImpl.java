package lln.spring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lln.spring.entity.Statistic;
import lln.spring.entity.vo.ArticleVO;
import lln.spring.mapper.ArticleMapper;
import lln.spring.entity.Article;
import lln.spring.mapper.CommentMapper;
import lln.spring.mapper.StatisticMapper;
import lln.spring.service.ArticleService;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import com.baomidou.mybatisplus.core.conditions.Wrapper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service // 确保该注解存在，让Spring扫描为Bean
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;



    // 修正方法名，添加@Override注解，与接口方法一致
//    @Override
//    public void publish(Article article) {
//        articleMapper.insert(article);
//    }

    public Result getIndexData(){
        Result result = new Result();
        List< Article> articles = articleMapper.getPage(0,5);
        result.getMap().put("articles",articles);

        PageParams pageParams = new PageParams();
        pageParams.setPage(1L);
        pageParams.setRows(10L);
        Result result1 = getAPageOfArticleVO(pageParams,"hits");
        result.getMap().put("articleVOs", result1.getMap().get("articleVOs"));

        return result;
    }


    @SneakyThrows
    @Override
    public String upload(MultipartFile file){
        final File folder = new File("D:\\Springboot\\img\\images");
        if(!folder.exists()){
            folder.mkdirs();
        }
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        final  String newName = UUID.randomUUID() + type;
        file.transferTo(new File(folder,newName));
        String url = "/api/images/"+newName;
        return url;
    }
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private StatisticMapper statisticMapper;

    public Result getAPageOfArticleVO(PageParams pageParams,String type) {
        QueryWrapper<ArticleVO> queryWrapper = new QueryWrapper<>();

        if("id".equals( type))
            queryWrapper.orderBy(true, false, "t_article.id");
        else
            queryWrapper.orderBy(true, false, "t_statistic.hits");

        queryWrapper.orderBy(true, false, "t_article.id");
        queryWrapper.apply("t_article.id = t_statistic.article_id");
        String s = queryWrapper.getCustomSqlSegment();
        Page<ArticleVO> page = new Page<>(pageParams.getPage(), pageParams.getRows());
        Wrapper wrapper = queryWrapper;
        IPage<ArticleVO> aPage = articleMapper.getAPageOfArticleVO(page, wrapper);
        Result result = new Result();
        pageParams.setTotal(aPage.getTotal());
        result.getMap().put("articleVOs", aPage.getRecords());
        result.getMap().put("pageParams", pageParams);
        return result;
    }

    public Result getArticleAndCommentByArticleId(Integer articleId) {
        Result result = new Result();
        result.getMap().put("article", articleMapper.selectById(articleId));
        result.getMap().put("comments", commentMapper.selectByArticleId(articleId));
        return result;
    }

    public Result getArticleAndFirstPageCommentByArticleId(Integer articleId, PageParams pageParams){
        Result result=new Result();
        result.getMap().put("article",articleMapper.selectById(articleId));
        // 对page和rows进行类型转换，将Long转为Integer
        Long offsetLong = (pageParams.getPage() - 1) * pageParams.getRows();
        Integer offset = offsetLong.intValue();
        Integer rows = pageParams.getRows().intValue();
        result.getMap().put("comments",
                commentMapper.getAPageCommentByArticleId(
                        articleId, offset, rows));

        // 文章的点击次数+1
        Statistic statistic=statisticMapper.selectByArticleId(articleId);
        statistic.setHits(statistic.getHits()+1);
        statisticMapper.updateById(statistic);

        return result;
    }

    //发布文章
    @Transactional
    public void publish(Article article){
        article.setCreated(new Date());
        articleMapper.insert(article);

        Statistic statistic=new Statistic();
        statistic.setArticleId(article.getId());
        statistic.setHits(0);
        statistic.setCommentsNum(0);
        statisticMapper.insert(statistic);
    }

    public void update(Article article){
        Article newArticle=articleMapper.selectById(article.getId());
        newArticle.setModified(new Date());
        newArticle.setTags(article.getTags());
        newArticle.setContent(article.getContent());
        newArticle.setTitle(article.getTitle());
        newArticle.setThumbnail(article.getThumbnail());
        articleMapper.updateById(newArticle);
    }

//    根据id查询
public Article selectById(Integer id){
    return articleMapper.selectById(id);
}
//根据id删除
public void deleteById(Integer id){
    articleMapper.deleteById(id);
}

    public Result getAPageOfArticle(PageParams pageParams) {
        QueryWrapper<ArticleVO> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, false, "t_article.id");

        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getRows());
        IPage<Article> aPage = articleMapper.getAPageOfArticle(page, wrapper);
        Result result = new Result();
        pageParams.setTotal(aPage.getTotal());
        result.getMap().put("articles", aPage.getRecords());
        result.getMap().put("pageParams", pageParams);
        return result;
    }
    public Result getIndexData(PageParams pageParams){
        //查文章分页
        Result result=getAPageOfArticle(pageParams);
        //查点击量排名前十文章
        PageParams pageParams1 = new PageParams();
        pageParams1.setPage(1L);
        pageParams1.setRows(10L);
        Result result1 = getAPageOfArticleVO(pageParams1, "hits");
        result.getMap().put("articleVOs", result1.getMap().get("articleVOs"));
        return result;
    }

}
