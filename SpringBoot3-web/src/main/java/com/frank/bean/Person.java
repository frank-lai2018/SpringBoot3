package com.frank.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement  // 可以寫出為XML文檔
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private Long id;
    private String userName;
    private String email;
    private Integer age;
    private String role;
}
