package com.example.securitydemo.config;

import com.example.securitydemo.service.AuthorityService;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 动态权限数据源，用于获取动态权限规则
 *
 */
@Component
public class AppFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Resource
    private AuthorityService                       authorityService;

    private static Map<String, Set<ConfigAttribute>> configAttributeMap = null;

    @PostConstruct
    public void loadDataSource() {
        configAttributeMap = authorityService.loadDataSource();
    }


    public void clearDataSource() {
        configAttributeMap.clear();
        configAttributeMap = null;
    }


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (configAttributeMap == null) this.loadDataSource();

        String requestUrl = ((FilterInvocation)object).getRequestUrl();
        PathMatcher pathMatcher = new AntPathMatcher();

        for (Map.Entry<String, Set<ConfigAttribute>> entry : configAttributeMap.entrySet()) {
            if (pathMatcher.match(entry.getKey(),requestUrl)) {
                return configAttributeMap.get(entry.getKey());
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }



}
