package com.frank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
*
* SpringBoot 對r2dbc的自動配置
* 1、R2dbcAutoConfiguration: 主要設定連接工廠、連接池
*
* 2、R2dbcDataAutoConfiguration： 主要提供給使用者了 R2dbcEntityTemplate 可以進行CRUD操作
* 		R2dbcEntityTemplate: 操作資料庫的響應式客戶端；提供CruD api ; RedisTemplate XxxTemplate
* 		資料類型對應關係、轉換器、自訂R2dbcCustomConversions 轉換器元件
* 		資料型別轉換：int，Integer； varchar，String； datetime，Instant
*
*
*
* 3、R2dbcRepositoriesAutoConfiguration： 開啟Spring Data聲明式介面方式的CRUD；
* 	mybatis-plus： 提供了 BaseMapper，IService；自帶了CRUD功能；
* 	Spring Data： 提供了基礎的CRUD接口，不用寫任何實作的情況下，可以直接具有CRUD功能；
*
*
* 4、R2dbcTransactionManagerAutoConfiguration： 事務管理
*
*/

@SpringBootApplication
public class R2DBCMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(R2DBCMainApplication.class,args);
    }
}
