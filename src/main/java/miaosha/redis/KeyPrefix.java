package miaosha.redis;

// 分割各个模块
public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();

}
