package com.nd.hilauncherdev.plugin.navigation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 卡片定义
 * 
 * @author chenzhihong_9101910
 * 
 */
public class Card {

	public static final int TYPE_SEARCH = 1;
	public static final int TYPE_AD = 2;
	public static final int SHOW_TYPE_SINGLE = 1;
	public static final int SHOW_TYPE_GROUP = 2;
	
	public static final String IS_FROM_SUB_ADD = "is_from_sub_add";
	public static final String IS_FROM_NEW_CARD_NOTIFY = "is_from_new_card_notify";

	private static final String ID = "id";
	private static final String TYPE = "type";
	private static final String NAME = "name";
	private static final String CAN_BE_DELETED = "can_be_deleted";
	private static final String IS_DEFAULT_OPEN = "is_default_open";
	private static final String CAN_MOVED = "can_moved";
	private static final String CAN_BE_REPEAT_ADDED = "can_be_repeat_added";
	private static final String DESC = "desc";
	private static final String BIG_IMG_URL = "bigImgUrl";
	private static final String SMALL_IMG_URL = "smallImgUrl";
	private static final String POSITION = "position";

	public int id;

	public int type;
	public int showType = SHOW_TYPE_SINGLE;
	public String name;

	//无调用
	public boolean isDefaultOpen;

	//卡片是否可以被删除
	public boolean canBeDeleted;

	//在卡片管理页面是否位置可调整
	public boolean canMoved;

	//无调用
	public boolean canBeRepeatAdded;

	public String desc;

	public int position;

	public String bigImgUrl;

	public String smallImgUrl;

	//是否已经添加
	public boolean isAdded = false;

	public void initCard(JSONObject cardJo) {
		try {
			this.id = cardJo.getInt(ID);
			this.type = cardJo.getInt(TYPE);
			this.name = cardJo.getString(NAME);
			this.isDefaultOpen = cardJo.getBoolean(IS_DEFAULT_OPEN);
			this.canBeDeleted = cardJo.getBoolean(CAN_BE_DELETED);
			this.canMoved = cardJo.getBoolean(CAN_MOVED);
			this.canBeRepeatAdded = cardJo.getBoolean(CAN_BE_REPEAT_ADDED);
			this.desc = cardJo.optString(DESC);
			this.bigImgUrl = cardJo.optString(BIG_IMG_URL);
			this.smallImgUrl = cardJo.optString(SMALL_IMG_URL);
			this.position = cardJo.getInt(POSITION);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(ID, id);
			jsonObject.put(TYPE, type);
			jsonObject.put(NAME, name);
			jsonObject.put(IS_DEFAULT_OPEN, isDefaultOpen);
			jsonObject.put(CAN_BE_DELETED, canBeDeleted);
			jsonObject.put(CAN_MOVED, canMoved);
			jsonObject.put(CAN_BE_REPEAT_ADDED, canBeRepeatAdded);
			jsonObject.put(DESC, desc);
			jsonObject.put(BIG_IMG_URL, bigImgUrl);
			jsonObject.put(SMALL_IMG_URL, smallImgUrl);
			jsonObject.put(POSITION, position);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

}
