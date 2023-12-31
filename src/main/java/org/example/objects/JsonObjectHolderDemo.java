package org.example.objects;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.utils.RedissonUtil;
import org.redisson.Redisson;
import org.redisson.api.RJsonBucket;
import org.redisson.api.RSearch;
import org.redisson.api.RedissonClient;
import org.redisson.api.search.index.FieldIndex;
import org.redisson.api.search.index.IndexOptions;
import org.redisson.api.search.index.IndexType;
import org.redisson.api.search.query.Document;
import org.redisson.api.search.query.QueryOptions;
import org.redisson.api.search.query.ReturnAttribute;
import org.redisson.api.search.query.SearchResult;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JacksonCodec;

import java.util.Arrays;
import java.util.List;

/**
 * @author newgaoxin
 * @version 1.0
 * @date 2023/6/28 10:38
 * @description json object holder demo
 */
public class JsonObjectHolderDemo {

    public static void main(String[] args) {
        RedissonClient redissonClient = RedissonUtil.getRedissonStackClient();

        RJsonBucket<Human> bucket = redissonClient.getJsonBucket(JsonObjectHolderDemo.class.getSimpleName(), new JacksonCodec<>(Human.class));

        bucket.set(new Human("张三"));
        Human human = bucket.get();
        System.out.println("human = " + human);

        System.out.println("------- 华丽的分割线 -------");
        bucket.trySet(new Human("李四"));
        human = bucket.get();
        System.out.println("human = " + human);

        System.out.println("------- 比较并设置 华丽的分割线 -------");
        bucket.compareAndSet(new Human("王五"), new Human("赵六"));
        human = bucket.get();
        System.out.println("human = " + human);

        System.out.println("------- 获取并设置 华丽的分割线 -------");
        bucket.getAndSet(new Human("田七"));
        human = bucket.get();
        System.out.println("human = " + human);

        RSearch s = redissonClient.getSearch(StringCodec.INSTANCE);

        // 创建索引
        s.createIndex("idx", IndexOptions.defaults()
                        .on(IndexType.JSON)
                        .prefix(Arrays.asList(JsonObjectHolderDemo.class.getSimpleName())),
                FieldIndex.text("$..name").as("name"));

        System.out.println("------- 搜索 华丽的分割线 -------");
        SearchResult r = s.search("idx", "*", QueryOptions.defaults()
                .returnAttributes(new ReturnAttribute("name")));
        for (Document document : r.getDocuments()) {
            System.out.println(document.getId());
            System.out.println(document.getAttributes());
            System.out.println(document.getScore());
            System.out.println(document.getPayload());
        }


        System.exit(0);
    }

    /**
     * 人
     */
    public static class Human {
        private String name;

        public Human() {
        }

        public Human(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Human{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
