package com.interfaces.iat.util;

import com.interfaces.iat.dto.input.job.XxlJobInputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.Map;


@Component
public class XxlJobUtil {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    HttpSession httpSession;

    @Value("${spring.application.name:接口自动化测试平台}")
    String springApplicationName;
    @Value("${xxl.job.admin.address}")
    String xxlJobAdminAddress;
    @Value("${xxl.job.admin.username}")
    String xxlJobAdminUserName;
    @Value("${xxl.job.admin.password}")
    String xxlJobAdminPassword;
    @Value("${xxl.job.admin.callback.url.prefix}")
    String xxlJobAdminCallbackUrlPrefix;

    /**
     * 往XXL-Job添加定时执行
     * @param xxlJobInputDto
     * @return
     */
    public Map<String,Object> addJob(XxlJobInputDto xxlJobInputDto){
        //xxl-job-admin添加任务api
        String url = xxlJobAdminAddress + "/jobinfo/add";

        HttpHeaders headers = new HttpHeaders();
        //获取登录成功的票据cookie
        String loginInfo = null;
        if (httpSession.getAttribute("loginInfo") ==null) {
            loginInfo = getCookie();
            httpSession.setAttribute("loginInfo",loginInfo);
        }else{
            loginInfo = httpSession.getAttribute("loginInfo").toString();
        }
        headers.set(HttpHeaders.COOKIE,loginInfo);
        //头部类型
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //创建任务接口参数
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("jobGroup", 1);
        paramMap.add("jobDesc", "【"+springApplicationName+"】"+ xxlJobInputDto.getName());
        paramMap.add("executorRouteStrategy", "FIRST");
        paramMap.add("scheduleType", "CRON");
        paramMap.add("scheduleConf", xxlJobInputDto.getCron());  //支持年份，如3 25 19 26 8 ? 2021
        paramMap.add("misfireStrategy", "DO_NOTHING");
        paramMap.add("glueType", "BEAN");
        paramMap.add("executorHandler", "httpGetJobHandler");  // 此处hander需提前在项目中定义
        paramMap.add("executorParam", xxlJobAdminCallbackUrlPrefix + xxlJobInputDto.getCallbackUrl());  //设计一个定时任务回调的接口
        paramMap.add("executorBlockStrategy", "SERIAL_EXECUTION");
        paramMap.add("executorTimeout", 0);
        paramMap.add("executorFailRetryCount", 1);
        paramMap.add("author", "admin");
        paramMap.add("glueRemark", "GLUE代码初始化");
        paramMap.add("triggerStatus", 1);

        //构造请求实体对象
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);

        //发送请求
//        restTemplate.exchange(url,HttpMethod.POST,param,ReturnT.class);
        ResponseEntity responseEntity = restTemplate.postForEntity(url, httpEntity,Object.class);
        return (Map<String,Object>)responseEntity.getBody();
    }

    /**
     * 停止任务
     * @param id
     * @return
     */
    public Map<String,Object> stopJob(Integer id){
        //xxl-job-admin添加任务api
        String url = xxlJobAdminAddress + "/jobinfo/stop?id=" + id;

        HttpHeaders headers = new HttpHeaders();
        //获取登录成功的票据cookie
        String loginInfo = null;
        if (httpSession.getAttribute("loginInfo") ==null) {
            loginInfo = getCookie();
            httpSession.setAttribute("loginInfo",loginInfo);
        }else{
            loginInfo = httpSession.getAttribute("loginInfo").toString();
        }
        headers.set(HttpHeaders.COOKIE,loginInfo);

        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("id", id);

        //构造请求实体对象
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);



        //发送请求
//        restTemplate.exchange(url,HttpMethod.POST,param,ReturnT.class);
        ResponseEntity responseEntity = restTemplate.postForEntity(url, httpEntity,Object.class);
        return (Map<String,Object>)responseEntity.getBody();
    }

    /**
     * 登录xxl-job-admin，获取登录票据cookie
     * @return
     */
    public String getCookie() {
        String url = xxlJobAdminAddress + "/login";

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        //接口参数
        map.add("userName",xxlJobAdminUserName);
        map.add("password",xxlJobAdminPassword);
        //头部类型
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //构造实体对象
        HttpEntity<MultiValueMap<String, Object>> param = new HttpEntity<>(map, headers);
        //发起请求,服务地址，请求参数，返回消息体的数据类型
        ResponseEntity responseEntity = restTemplate.postForEntity(url, param,Object.class);
        headers = responseEntity.getHeaders();
        String cookies = headers.getFirst(HttpHeaders.SET_COOKIE);
        return cookies;
    }
}