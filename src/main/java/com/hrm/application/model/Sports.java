package com.hrm.application.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * 体育常量
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public interface Sports {
  //采集源维护
  String Maintenance = "goHome";
  //登录信息被挤下失效
  String Expired = "doubleLogin";
  //成功
  String SUCCESS = "success";
  //失败
  String FAIL = "fail";

  String SPORT_DELAY = "赛事延赛";

  String Default_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";

  String Default_Host = "hg0088.com";

  String Match_Redis_Prefix = "match_type_";

  enum API {
    GetDomain("获取可用域名", "service_mainget"),
    VersionInfo("获取网站版本", "get_version"),
    Login("登录", "chk_login"),
    CheckDomain("检查是否切换域名", "check_login_domain", new SimpleEntry<>("code", 663)),
    GetLeagueList("获取联赛信息", "get_league_list_All"),
    GetLeagueCount("获取联赛玩法信息", "get_league_count"),
    GetGameList("获取投注玩法信息", "get_game_list"),
    GetGameMore("获取单赛事所有玩法信息", "get_game_more"),
    GetGameModel("取得外層玩法樣板", "gameModel"),
    GetGameMoreFT("取得足球內層玩法樣板", "game_more_FT"),
    GetGameMoreBK("取得籃球內層玩法樣板", "game_more_BK"),
    ;
    String desc;
    Map<String, Object> baseParams;

    @SafeVarargs
    API(String desc, String cmd, SimpleEntry<String, Object>... extInitParams) {
      this.desc = desc;
      this.baseParams = new HashMap<>();
      baseParams.put("p", cmd);
      baseParams.put("langx", "zh-cn");
      if (extInitParams != null && extInitParams.length > 0) {
        Arrays.asList(extInitParams).forEach(e -> baseParams.put(e.getKey(), e.getValue()));
      }
    }

    public Map<String, Object> initParams() {
      return new HashMap<>(baseParams);
    }

    public String getDesc() {
      return desc;
    }
  }

  enum Kind {
    FT("FT", "足球"),
    BK("BK", "篮球"),
    VB("VB", "排球"),
    TN("TN", "网球"),
    BS("BS", "棒球"),
    TT("TT", "乒乓球"),
    BM("BM", "羽毛球"),
    SK("SK", "斯诺克"),
    OP("OP", "其他"),
    ;
    String name;
    String id;

    Kind(String id, String name) {
      this.name = name;
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public String getId() {
      return id;
    }
  }

  /**
   * 采集账号分类
   */
  enum AccountType {
    Common(0, "普通采集"),
    RB(1, "滚球采集"),
    RB2(2, "新滚球采集");
    int type;
    String desc;

    AccountType(int type, String desc) {
      this.type = type;
      this.desc = desc;
    }

    public static AccountType getByValue(int value) {
      return Arrays.stream(AccountType.values())
          .filter(accountType -> accountType.getType() == value).findFirst().orElse(null);
    }

    public int getType() {
      return type;
    }

    public String getDesc() {
      return desc;
    }
  }

  /**
   * 体育采集类型： 普通（1）、滚球（2）、早盘（4）、赛果（8），为2的N次方
   */
  enum SportType {
    Today(1, "今日"),
    RB(2, "滚球"),
    FU(4, "早盘"),
    Result(8, "赛果");
    int type;
    String desc;

    SportType(int type, String desc) {
      this.type = type;
      this.desc = desc;
    }

    public boolean yes(int type) {
      return this.type == type;
    }

    public int getType() {
      return type;
    }

    public String getDesc() {
      return desc;
    }
  }

  /**
   * status，启用状态：禁用<0>、启用<1>
   */
  enum Status {
    Enable(1, "启用"),
    Disable(0, "禁用");
    int value;
    String desc;

    Status(int value, String desc) {
      this.value = value;
      this.desc = desc;
    }

    public int getValue() {
      return value;
    }

    public String getDesc() {
      return desc;
    }
  }

  enum SysWarnGroup {
    ADMIN("管理后台", "admin"),
    OTHER("其它", "other"),
    COLLECT("体育采集", "sportCollect"),
    WEB("前端", "web"),
    WORK("任务", "work"),
    ;
    String name;
    String value;

    SysWarnGroup(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }
  }

  enum MatchType {
    Live("滚球", 0, "tail", new HashSet<>(Arrays.asList(
        FT_RE, BK_RE // 独赢/让球/大小/单双
    ))),
    Today("今日", 1, "t", new HashSet<>(Arrays.asList(
        FT_R, FT_P3, FT_T, FT_PD, FT_HPD, FT_F, BK_P3, BK_R // 足球：独赢/让球/大小/单双|波胆|总入球|半场/全场|综合过关  篮球：独赢/让球/大小/单双|综合过关
    ))),
    Early("早盘", 2, "u", new HashSet<>(Arrays.asList(
        FT_FU_T, FT_FU_R, FT_FU_PD, FT_FU_HPD, FT_FU_F, FT_FU_P3, BK_FU_R, BK_FU_P3 //足球：独赢/让球/大小/单双|波胆|总入球|半场/全场|综合过关 篮球：独赢/让球/大小/单双|综合过关
    )));

    private final String name, label;
    private final int value;
    private final Set<String> subTypes;

    MatchType(String name, int value, String label, Set<String> subTypes) {
      this.name = name;
      this.value = value;
      this.label = label;
      this.subTypes = subTypes;
    }

    public static MatchType getByLabel(String label) {
      return Arrays.stream(MatchType.values()).filter(m -> m.getLabel().equals(label)).findFirst()
          .get();
    }

    public static MatchType getBySubType(String subType) {
      return Arrays.stream(MatchType.values()).filter(type -> type.getSubTypes().contains(subType))
          .findFirst().get();
    }

    public String getName() {
      return name;
    }

    public int getValue() {
      return value;
    }

    public Set<String> getSubTypes() {
      return subTypes;
    }

    public String getLabel() {
      return label;
    }
  }

  /**
   * 账户配置状态
   */
  enum AccountState {
    DISABLED(0), ENABLED(1);
    Integer value;

    AccountState(Integer value) {
      this.value = value;
    }

    public static boolean invalid(Integer value) {
      return Arrays.stream(AccountState.values())
          .noneMatch(state -> Objects.equals(state.getValue(), value));
    }

    public Integer getValue() {
      return value;
    }
  }

  /**
   * 站点配置状态
   */
  enum WebsiteState {
    DISABLED(0), ENABLED(1);
    Integer value;

    WebsiteState(Integer value) {
      this.value = value;
    }

    public static boolean invalid(Integer value) {
      return Arrays.stream(WebsiteState.values())
          .noneMatch(state -> Objects.equals(state.getValue(), value));
    }

    public Integer getValue() {
      return value;
    }
  }

  enum SysWarnType {
    COLLECT("collect", "采集"),
    LOTTERY("lottery", "开奖结果"),
    OPEN_TURN("open_turn", "期号"),
    SPORT_SETTLE("sport_settle", "体育结算");

    String code;
    String name;

    SysWarnType(String code, String name) {
      this.code = code;
      this.name = name;
    }

    public String getCode() {
      return code;
    }

    public String getName() {
      return name;
    }
  }
  /**
   * 今日 足球 独赢
   */
  String FT_R = "ft_r";

  /**
   * 今日 足球 波胆
   */
  String FT_PD = "ft_pd";

  /**
   * 今日 足球 上半场波胆
   */
  String FT_HPD = "ft_hpd";

  /**
   * 今日 足球 总入球
   */
  String FT_T = "ft_t";

  /**
   * 今日 足球 半全场
   */
  String FT_F = "ft_f";

  /**
   * 今日 足球 综合过关
   */
  String FT_P3 = "ft_p3";

  /**
   * 足球滚球
   */
  String FT_RE = "ft_re";

  /**
   * 今日 篮球 赛事
   */
  String BK_R = "bk_r";

  /**
   * 早盘 篮球 赛事
   */
  String BK_FU_R = "bk_fu_r";

  /**
   * 早盘篮球综合过关
   */
  String BK_FU_P3 = "bk_fu_p3";

  /**
   * 今日 篮球 综合过关
   */
  String BK_P3 = "bk_p3";
  /**
   * 滚球 篮球
   */
  String BK_RE = "bk_re";

  /**
   * 早盘足球独赢
   */
  String FT_FU_R = "ft_fu_r";
  /**
   * 早盘足球波胆
   */
  String FT_FU_PD = "ft_fu_pd";
  /**
   * 早盘足球总入球
   */
  String FT_FU_T = "ft_fu_t";
  /**
   * 早盘足球半全场
   */
  String FT_FU_F = "ft_fu_f";
  /**
   * 早盘足球综合过关
   */
  String FT_FU_P3 = "ft_fu_p3";
  /**
   * 早盘足球上半场波胆
   */
  String FT_FU_HPD = "ft_fu_hpd";

  /**
   * 足球赛果
   */
  String R_FT = "r_ft";

  /**
   * 篮球赛果
   */
  String R_BK = "r_bk";


  String TODAY_FT_MORE = "today_ft_more";
  String EARLY_FT_MORE = "early_ft_more";
  String TODAY_FT_P3_MORE = "today_ft_p3_more";
  String EARLY_FT_P3_MORE = "early_ft_p3_more";
  String FT_RE_TEMP = "ft_re_temp";


  String TODAY_BK_MORE = "today_bk_more";
  String EARLY_BK_MORE = "early_bk_more";
  String TODAY_BK_P3_MORE = "today_bk_p3_more";
  String EARLY_BK_P3_MORE = "early_bk_p3_more";
  String BK_RE_TEMP = "bk_re_temp";


}
