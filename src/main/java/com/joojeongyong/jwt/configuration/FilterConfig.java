package com.joojeongyong.jwt.configuration;

import com.joojeongyong.jwt.filter.MyFilter1;
import com.joojeongyong.jwt.filter.MyFilter2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//IOC 내가 만든 필터를 빈으로 등록함
@Configuration
public class FilterConfig {
//
//    @Bean
//    public FilterRegistrationBean<MyFilter1> filter1() {
//        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
//        bean.addUrlPatterns("/*");
//        bean.setOrder(0);
//        return bean;
//    }
//
//    @Bean
//    public FilterRegistrationBean<MyFilter2> filter2() {
//        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
//        bean.addUrlPatterns("/*");
//        bean.setOrder(1);
//        return bean;
//    }
}
