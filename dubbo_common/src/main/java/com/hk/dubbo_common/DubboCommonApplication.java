package com.hk.dubbo_common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hk.dubbo_common.dao")
public class DubboCommonApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboCommonApplication.class, args);
	}

}

