package com.projects.covid19.serviceitem.config;

import org.apache.commons.lang3.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;


@Configuration
@EnableRedisRepositories
public class RedisConfig {
	
//	@Bean
//	public LettuceConnectionFactory redisConnectionFactory() {
//		return new LettuceConnectionFactory();
//	}
	
//	@Bean
//	public RedisTemplate<?, ?> redisTemplate() {
//		
//		RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
//		template.setConnectionFactory(redisConnectionFactory());
//		return template;
//		
//	}
	
	
	@Bean
	public LettuceConnectionFactory redisConnectionFactory( RedisPropertiesConfig config) {
		return new LettuceConnectionFactory(config.getRedisHost(), config.getRedisPort());
	}
	

	@Bean
	public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
		
		RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new JsonRedisSerializer());

		return template;
		
	}
	
	static class JsonRedisSerializer implements RedisSerializer<Object> {

		private final ObjectMapper om;

		public JsonRedisSerializer() {
			this.om = new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
		}

		@Override
		public byte[] serialize(Object t) throws SerializationException {
			try {
				return om.writeValueAsBytes(t);
			} catch (JsonProcessingException e) {
				throw new SerializationException(e.getMessage(), e);
			}
		}

		@Override
		public Object deserialize(byte[] bytes) throws SerializationException {
			
			if(bytes == null){
				return null;
			}
			
			try {
				return om.readValue(bytes, Object.class);
			} catch (Exception e) {
				throw new SerializationException(e.getMessage(), e);
			}
		}
}
		
	@Bean
	public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
		stringRedisTemplate.setEnableTransactionSupport(true);
		return stringRedisTemplate;
	}	

}
