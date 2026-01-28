package chat.aikf.common.redis.configure;



import chat.aikf.common.core.utils.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson配置
 *
 *
 * @version 1.0.0
 * @date 2023/05/15 14:42
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String post;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database}")
    private Integer database;

    @Bean
    public RedissonClient singleServerConfig() {
        //使用单机模式
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setConnectionMinimumIdleSize(5); // 最小空闲连接数
        singleServerConfig.setConnectionPoolSize(30); // 连接池大小
        singleServerConfig.setAddress("redis://" + host + ":" + post);
        if (StringUtils.isNotBlank(password)) {
            singleServerConfig.setPassword(password);
        }
        if (database != null) {
            singleServerConfig.setDatabase(database);
        }
        config.setCodec(new JsonJacksonCodec());
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }
}

