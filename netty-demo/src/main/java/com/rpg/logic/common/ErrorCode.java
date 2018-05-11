package com.rpg.logic.common;

public enum ErrorCode {

	NULL(0, "正常"),
	
	SYSTEM_ERROR(1000, "系统异常"),
	PARAMS_ERROR(1001, "参数错误"),
	MATERIAL_NOT_ENOUGH(1002, "材料不足"),
	
	PLAYER_NOT_EXIST(1101, "角色不存在"),
	PLAYER_NO_MONEY(1102, "金币不足"),
	PLAYER_NO_GOLD(1103, "钻石不足"),
	PLAYER_NO_LEVEL(1104, "角色等级不足"),
	PLAYER_BACKAGE_NOT_ENOUGU(1105,"背包空间不足"),
	PLAYER_NOT_ONLINE(1106,"玩家不在线"),
	
	
	SOLIDER_NOT_EXIST(1201, "兵种不存在"),
	SOLIDER_NO_LEVEL(1202, "兵种等级不足"),
	SOLIDER_LEVEL_MAX(1203, "兵种已满级"),
	SOLIDER_AWAKEN_MAX(1204, "觉醒已满级"),
	SOLIDER_MARK_MAX(1205, "印记已满级"),
	SOLIDER_NO_SELL(1206, "该兵种不能出售"),
	SOLIDER_STAGE_MAX(1207, "兵种进阶等级已满"),
	SOLIDER_EXP_MAX(1208, "兵种经验已达上限"),
	
	

	ARENA_PROMOTION_ERROR(1301, "未获得晋级资格"),
	ARENA_BUYCOUNT_ERROR(1302, "还有剩余挑战次数，购买失败"),
	ARENA_COUNT_ERROR(1303, "挑战次数用完，挑战失败"),
	
	COPYMAP_NOT_EXIST(1401, "副本不存"),
	COPYMAP_NO_FINISH(1402, "副本未完成"),
	COPYMAP_BOX_REPEAT_OPEN(1403, "不能重复翻牌"),
	COPYMAP_NO_COUNT(1404, "挑战次数用完，挑战失败"),
	COPYMAP_NO_3S(1405, "副本评价为3S才能扫荡"),
	COPYMAP_DIFF_1(1406, "通关普通副本，才开放困难副本"),
	COPYMAP_DIFF_2(1407, "通关困难副本，才开放地狱副本"),
	
	
	RUNE_LEVEL_MAX(1501, "符文已满级"),
	RUNE_COMPOSE_COUNT_ERROR(1502, "合成符文数量不足"),
	
	SKILL_LEVEL_MAX(1601, "技能已满级"),
	SKILL_NO_UPGRADE(1602, "该技能不能升级"),
	SKILL_NO_ACTIVE(1603, "技能未激活"),
	
	MONSTER_IS_FIGHT(1701, "怪物战斗中"),
	MONSTER_NOT_EXISTS(1702, "怪物不存在了"),
	
	TASK_NO_ACCEPT(1801,"你还没有接该任务"),
	TASK_NO_ABANDON(1802,"该任务不能放弃"),

	EQUIP_NOT_FOUND(5001,"装备不存在"),
	EQUIP_ZHI_YE(5002,"职业不符"),
	EQUIP_ZHONG_ZU(5003,"种族不符"),
	EQUIP_MODEL_NOT_FOUND(5004,"装备配置表不存在"),
	EQUIP_UPDATE_MAX_LEVEL(5005,"已到达最高等级"),
	EQUIP_POSITION_ERROR(5006,"位置错误"),
	EQUIP_GEM_TYPE_ERROR(5007,"宝石类型错误"),
	EQUIP_NOT_GEM(5008,"没有可以拆卸的宝石"),
	EQUIP_CLING_TYPE_ERROR(5009,"不是附魔石"),
	EQUIP_GEM_ERROR(5010,"宝石不足"),
	EQUIP_WEAR_NOT_RESOLVE(5011,"已穿戴装备不能分解"),
	EQUIP_ITEM_NOT_FOUND(5012,"道具表不存在此装备"),
	EQUIP_GEM_NOT_ENOUGH(5013,"宝石数量不足"),
	SOCIETY_LEVEL(5014,"等级不足"),
	SOCIETY_NAME_NOT_NULL(5015,"帮会名字不能为空"),
	SOCIETY_LENGTH_ERROR(5016,"帮会名字长度不符"),
	SOCIETY_NOT_EMPTY(5017,"已存在该名字帮会"),
	SOCIETY_IS_IN(5018,"已加入帮会,不能再创建帮会"),
	SOCIETY_NO(5019,"不存在该帮会"),
	SOCIETY_IN(5020,"您已加入帮会"),
	SOCIETY_IS_APPLY(5021,"已申请加入帮会"),
	SOCIETY_IS_FULL(5022,"帮会已满员"),
	SOCIETY_NO_QUANXIAN(5023,"没有权限"),
	SOCIETY_NO_APPLYER(5024,"不存在该申请者"),
	SOCIETY_LEADER_NO_EXIT(5025,"帮主不能退出帮会"),
	SOCIETY_DONATE_MAX(5026,"今天捐献铜币已超过上限"),
	SOCIETY_MODEL_SKILL_ERROR(5027,"配置表技能错误"),
	SOCIETY_DONATE_NOT_ENOUGH(5028,"公会点数不足"),
	SOCIETY_NOT_ITEM(5029,"不存在该礼包"),
	SOCIETY_NOT_IN(5030,"不是该帮会成员"),
	SOCIETY_MAN_COUNT_MAX(5031,"已到达人数上限"),
	SOCIETY_NO_SELL(5032,"不能任命自己"),
	SOCIETY_NOT_LEADER(5033,"不是帮主,不能禅让"),
	SOCIETY_NOT_DELETE_SELF(5034,"不能删除自己"),
	SOCIETY_DESC_TOO_LONG(5035,"公告内容不能大于50个字符"),
	BACKAGE_GRID_COUNT_ERROR(5036,"格子数错误"),
	BACKAGE_ITEM_NOT_ENOUGH(5037,"物品数量不足"),
	BACKAGE_ITEM_MODEL_ERROR(5038,"道具表不存在此物品"),
	BACKAGE_ITEM_GIFT_MODEL_ERROR(5039,"礼包配置表不存在此物品"),
	BACKAGE_GRID_ERROR(5040,"格子错误"),
	BACKAGE_NOT_SELL(5041,"不允许出售"),
	BACKAGE_NOT_IN_WAREHOUSE(5042,"此物品不允许放入仓库"),
	BACKAGE_EQUIP_ADD_ERROR(5043,"装备添加失败"),
	BACKAGE_WAREHOUSE_ERROR(5044,"仓库错误"),
	FRIEND_NAME_NOT_NULL(5045,"玩家名字不能为空"),
	FRIEND_NOT_FOUNG(5046,"不存在该玩家"),
	FRIEND_NOT_ADD_SELF(5047,"不能添加自己为好友"),
	FRIEND_IS_ADD(5048,"已申请添加该好友"),
	FRIEND_COUNT_MAX(5049,"已到达好友上限"),
	FRIEND_APPLY_LIST_IS_EMPTY(5050,"申请列表为空"),
	MALL_LEVEL(5051,"玩家等级不足"),
	MALL_NO_SELL(5052,"此物品不允许交易"),
	MALL_LIMIT_COST(5053,"此物品不能超出限价"),
	MALL_NO_ITEM(5054,"不存在该物品"),
	MALL_NO_BUY(5055,"不能购买自己的商品"),
	MALL_NO_BUY_OUT(5056,"不能购买已下架商品"),
	MALL_NO_OUT(5057,"不能下架非自己的商品"),
	MALL_IS_OUT(5058,"商品已下架"),
	
	PRODUCTION_IS_LEARN(5059,"已学习该技能"),
	PRODUCTION_MODEL_NOT_FOUND(5060,"配置表不存在该配方"),
	PRODUCTION_MODEL_TYPE_ERROR(5061,"配置表配方类型不符"),
	
	SOCIETY_MODEL_SKILL_NOT_IN(5062,"技能尚未解锁"),
	
	EQUIP_MATERIAL_NOT_ENOUGH(5063,"合成材料不足"),
	
	SOCIETY_ALREADY_IN(5064,"该玩家已有公会,无法邀请"),
	SOCIETY_PLAYER_NOT_ONLINE(5065,"很抱歉,您查找的玩家不在线或者不存在"),
	SOCIETY_NOT_JOIN(5066,"您还没加入任何帮会,无法邀请"),
	;

	private short code;
	private String desc;

	private ErrorCode(int code, String desc) {
		this.code = (short) code;
		this.desc = desc;
	}

	public short getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static ErrorCode getErrorCode(short code) {
		for (ErrorCode error : ErrorCode.values()) {
			if (error.getCode() == code)
				return error;
		}
		return null;
	}
}
