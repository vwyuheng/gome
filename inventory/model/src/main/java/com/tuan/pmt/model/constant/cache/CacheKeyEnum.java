package com.tuan.pmt.model.constant.cache;

public enum CacheKeyEnum {
	CACHE_BATCH_KEY ("CACHE_BATCH_KEY_",	"批次在memcache中的key"),
	CACHE_CITY_KEY 	("CACHE_CITY_KEY_",  	"城市在memcache中的key"),
	CACHE_CAT_KEY	("CACHE_CAT_KEY_",		"分类在memcache中的key");
    
    private String name;
    private String description;
    
    private CacheKeyEnum(String name,String description){
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
