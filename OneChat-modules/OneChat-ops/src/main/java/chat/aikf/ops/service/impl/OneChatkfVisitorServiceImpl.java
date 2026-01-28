package chat.aikf.ops.service.impl;


import chat.aifk.common.datascope.annotation.DataScope;
import chat.aikf.common.core.constant.Constants;
import chat.aikf.common.core.utils.DefaultAvatarUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.core.web.domain.BaseEntity;
import chat.aikf.common.security.utils.SecurityUtils;
import chat.aikf.im.api.constant.OneChatMsgTypes;
import chat.aikf.ops.api.constant.OneChatReadMsgState;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.api.utils.RuleFfServingUtils;
import chat.aikf.ops.domain.OneChatKfCsStatsVO;
import chat.aikf.ops.domain.OneChatKfTrendVO;
import chat.aikf.ops.mapper.OneChatkfVisitorMapper;
import chat.aikf.ops.service.IOneChatKfVisitorMsgService;
import chat.aikf.ops.service.IOneChatkfVisitorService;
import chat.aikf.system.api.RemoteUserService;
import chat.aikf.system.api.domain.SysUser;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_ kf_visitor(客服访客)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatkfVisitorServiceImpl extends ServiceImpl<OneChatkfVisitorMapper, OneChatkfVisitor>
    implements IOneChatkfVisitorService {

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private IOneChatKfVisitorMsgService oneChatKfVisitorMsgService;



    @Override
    public List<OneChatkfVisitor> findList(OneChatkfVisitor oneChatkfVisitor) {
        oneChatkfVisitor.setUserAccount(SecurityUtils.getUsername());
        List<OneChatkfVisitor> chatkfVisitors = this.baseMapper.findList(oneChatkfVisitor);
        if(CollectionUtil.isNotEmpty(chatkfVisitors)){
            chatkfVisitors.stream().forEach(k->{

                OneChatKfVisitorMsg visitorMsg = oneChatKfVisitorMsgService.getOne(new LambdaQueryWrapper<OneChatKfVisitorMsg>()
                        .eq(OneChatKfVisitorMsg::getKfVisitorId, k.getId())
                        .orderByDesc(OneChatKfVisitorMsg::getSendTime)
                        .last("limit 1"));

                if(null != visitorMsg){
                    if(visitorMsg.getMsgType().equals(OneChatMsgTypes.MSG_TYPE_IMAGE)){
                        k.setCurrentMsg("[图片]");
                    }else{
                        k.setCurrentMsg(visitorMsg.getContent());
                    }
                    k.setCurrentMsgSendTime(visitorMsg.getSendTime());
                }


                k.setNotReadNumber(
                        oneChatKfVisitorMsgService.count(new LambdaQueryWrapper<OneChatKfVisitorMsg>()
                                .eq(OneChatKfVisitorMsg::getKfVisitorId, k.getId())
                                .eq(OneChatKfVisitorMsg::getReadReceipt, OneChatReadMsgState.noReadReceipt))
                );


            });

        }



        return chatkfVisitors;
    }

    @Override
    public OneChatkfVisitor getOneChatkfVisitorById(Long id) {
        List<OneChatkfVisitor> chatkfVisitors = this.baseMapper.findList(OneChatkfVisitor.builder().id(id).build());

        if(CollectionUtil.isNotEmpty(chatkfVisitors)){
            return chatkfVisitors.stream().findAny().get();
        }
        return new OneChatkfVisitor();
    }

    @Override
    @DataScope(deptAlias = "ockv", userAlias = "ockv")
    public OneChatKfCsStatsVO countOneChatKfCsStats(BaseEntity baseEntity) {
        OneChatKfCsStatsVO oneChatKfCsStatsVO = this.baseMapper.countOneChatKfCsStats();

        List<SysUser> sysUsers = remoteUserService.allList(new SysUser()).getData();
        if(CollectionUtil.isNotEmpty(sysUsers)){
            oneChatKfCsStatsVO.setOnlineAgents( sysUsers.stream()
                    .filter(user -> "0".equals(user.getKfStatus()))
                    .count());
        }else{
            oneChatKfCsStatsVO.setOnlineAgents(0L);
        }
        return oneChatKfCsStatsVO;
    }

    @Override
    public List<OneChatKfTrendVO> countOneChatKfTrendVo(BaseEntity baseEntity) {

        return this.baseMapper.countOneChatKfTrendVo(baseEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OneChatkfVisitor addOrUpdate(OneChatkfVisitor oneChatkfVisitor) {
        if(StringUtils.isNotEmpty(oneChatkfVisitor.getVisitorId()) && StringUtils.isNotEmpty(oneChatkfVisitor.getUserAccount())){
                OneChatkfVisitor oldOneChatkfVisitor = this.getOne(new LambdaQueryWrapper<OneChatkfVisitor>()
                        .eq(OneChatkfVisitor::getVisitorId, oneChatkfVisitor.getVisitorId())
                        .eq(OneChatkfVisitor::getKfRuleId,oneChatkfVisitor.getKfRuleId())
                        .eq(OneChatkfVisitor::getUserAccount,oneChatkfVisitor.getUserAccount())
                        .last("limit 1"));
                if(null != oldOneChatkfVisitor){//更新
                    oneChatkfVisitor.setId(oldOneChatkfVisitor.getId());
                    oneChatkfVisitor.setViewNumber(oldOneChatkfVisitor.getViewNumber()==null?0:oldOneChatkfVisitor.getViewNumber()+1);
                    oneChatkfVisitor.setCurrentViewTime(new Date());//当前到访时间
                }

                //设置头像
                if(StringUtils.isEmpty(oneChatkfVisitor.getAvatar())){
                    oneChatkfVisitor.setAvatar(
                            DefaultAvatarUtils.getRandomAvatarPath()
                    );
                }


                this.saveOrUpdate(oneChatkfVisitor);


            }




        return oneChatkfVisitor;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OneChatkfVisitor endChat(OneChatkfVisitor oneChatkfVisitor) {
        //1:修改访客状态,同时缓存中移除关系
        OneChatkfVisitor oldOneChatkfVisitor = this.getById(oneChatkfVisitor.getId());

        if(null != oldOneChatkfVisitor){
            //状态设置为结束
            oldOneChatkfVisitor.setCurrentState(OneChatVisitorSate.END_STATE);
            if(this.updateById(oldOneChatkfVisitor)){

                //获取最久远的一条排队中的访客
                OneChatkfVisitor chatkfVisitor = this.getOne(new LambdaQueryWrapper<OneChatkfVisitor>()
                        .eq(OneChatkfVisitor::getCurrentState, OneChatVisitorSate.IDLE_STATE)
                        .orderByAsc(OneChatkfVisitor::getCreateTime)
                        .last(" limit 1"));

                //调用tio相关接口通知客户端，并关闭链接


                //不为空,则更新状态
                if(null != chatkfVisitor){
                    chatkfVisitor.setCurrentState(OneChatVisitorSate.RECEIVE_STATE);
                }



                //缓存中接触关系
//                ruleFfServingUtils.unbindVisitorFromKf(oldOneChatkfVisitor.getKfRuleId().toString(),oldOneChatkfVisitor.getUserAccount(),oldOneChatkfVisitor.getVisitorId());


                //从排队的队列中选出最久的一条接入会话，接入语推送给访客

            }
        }



        return oldOneChatkfVisitor;
    }

    @Override
    public OneChatkfVisitor findAccessVisitor() {

        return this.getOne(new LambdaQueryWrapper<OneChatkfVisitor>()
                .eq(OneChatkfVisitor::getCurrentState, OneChatVisitorSate.IDLE_STATE)
                .orderByAsc(OneChatkfVisitor::getCreateTime)
                .last(" limit 1"));
    }

    @Override
    public OneChatkfVisitor findAppointVisitor(String kfRuleId, String visitorId, String userAccount) {
        return this.getOne(new LambdaQueryWrapper<OneChatkfVisitor>()
                .eq(OneChatkfVisitor::getCurrentState, OneChatVisitorSate.IDLE_STATE)
                .eq(OneChatkfVisitor::getKfRuleId,kfRuleId)
                .eq(OneChatkfVisitor::getVisitorId,visitorId)
                .eq(OneChatkfVisitor::getUserAccount,userAccount)
                .last(" limit 1")
        );
    }
}




