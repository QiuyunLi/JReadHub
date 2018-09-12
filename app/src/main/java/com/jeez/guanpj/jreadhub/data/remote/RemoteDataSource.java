package com.jeez.guanpj.jreadhub.data.remote;


import com.jeez.guanpj.jreadhub.bean.DataListBean;
import com.jeez.guanpj.jreadhub.bean.InstantReadBean;
import com.jeez.guanpj.jreadhub.bean.NewTopicCountBean;
import com.jeez.guanpj.jreadhub.bean.NewsBean;
import com.jeez.guanpj.jreadhub.bean.RelevantTopicBean;
import com.jeez.guanpj.jreadhub.bean.TopicBean;
import com.jeez.guanpj.jreadhub.bean.TopicDetailBean;
import com.jeez.guanpj.jreadhub.util.Constants;

import java.util.List;

import io.reactivex.Observable;

public interface RemoteDataSource {
    Observable<DataListBean<TopicBean>> getTopicList(Long lastCursor, int pageSize);

    Observable<DataListBean<NewsBean>> getNewsList(@Constants.Type String type, Long lastCursor, int pageSize);

    Observable<InstantReadBean> getTopicInstantRead(String topicId);

    Observable<TopicDetailBean> getTopicDetail(String topicId);

    Observable<List<RelevantTopicBean>> getRelateTopic(String topicId, int eventType, long order, long timeStamp);

    Observable<NewTopicCountBean> getNewTopicCount(Long latestCursor);
}
