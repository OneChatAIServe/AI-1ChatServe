package chat.aikf.ops.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatKfRuleInitVo {

    //当前客服状态 0:离开；1:正常

    private Integer currentKfState;

    //规则组名称
    private String ruleName;


   //对外昵称
    private String nickName;

    //上限
    private Integer upperLimit;


    //当前接待数(对话中的数量)
    private Integer currentRecepNum;


    //排队数
    private Integer lineUpNum;


    //客服组规则id
    private Long kfRuleId;


    //客服样式id
    private String webStyleId;


    //头像
    private String avatar;



    //员工账号
    private String userAccount;

    //接待语
    private String receiveMsg;

   //结束提示语
    private String endMsg;

}
