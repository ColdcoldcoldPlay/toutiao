package com.wangqifan.toutiao.Controller;


import com.wangqifan.toutiao.Models.EntityType;
import com.wangqifan.toutiao.Models.HostHolder;
import com.wangqifan.toutiao.Models.News;
import com.wangqifan.toutiao.Service.LikeService;
import com.wangqifan.toutiao.Service.NewsService;
import com.wangqifan.toutiao.async.EventModel;
import com.wangqifan.toutiao.async.EventProducer;
import com.wangqifan.toutiao.async.EventType;
import com.wangqifan.toutiao.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newId") int newsId) {
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setEntityOwnerId(news.getUserId())
                .setActorId(hostHolder.getUser().getId()).setEntityId(newsId));
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newId") int newsId) {
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        newsService.updateLikeCount(newsId, (int) likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
