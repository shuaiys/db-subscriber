package com.galen.subscriber.client;

import com.galen.subscriber.client.annotation.Subscriber;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 扫描注解 {@link Subscriber},并向容器中注册代理对象{@link SubscriberFactoryBean}bean定义
 * @date 2020-05-18 22:28
 */
public class SubscriberRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerSubscriber(metadata, registry);
    }

    private void registerSubscriber(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);

//        Map<String, Object> attrs = metadata
//                .getAnnotationAttributes(EnableSubscriber.class.getName());

        // 添加扫描器，扫描所有 @Subscriber注解
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                Subscriber.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        // 获取到所有需要扫描的包路径
        Set<String> basePackages = getBasePackages(metadata);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(!annotationMetadata.isInterface(),
                            "@Subscriber 注解不能标注接口");

                    Map<String, Object> attributes = annotationMetadata
                            .getAnnotationAttributes(
                                    Subscriber.class.getCanonicalName());
                    // 注册@Subscriber标注类的代理类的bean definition
                    registerSubscriber(registry, annotationMetadata, attributes);
                }
            }
        }
    }

    private void registerSubscriber(BeanDefinitionRegistry registry,
                                     AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        // 注册一个代理对象
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(SubscriberFactoryBean.class);
        String name = getSubscriberName(attributes);
        definition.addPropertyValue("name", name);
        String contextId = getContextId(attributes);
        definition.addPropertyValue("contextId", contextId);
        definition.addPropertyValue("type", className);
        String db = getDB(attributes);
        definition.addPropertyValue("db", db);
        String table = getTable(attributes);
        definition.addPropertyValue("table", table);

        /**
         * TODO 处理columns/eventType属性，2.0做
         */

        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        String alias = name + contextId + "SubscriberClient";
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        boolean primary = (Boolean) attributes.get("primary"); // has a default, won't be null

        beanDefinition.setPrimary(primary);

        String qualifier = getQualifier(attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[] { alias });
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private String getQualifier(Map<String, Object> attributes) {
        if (attributes == null) {
            return null;
        }
        String qualifier = (String) attributes.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    private String getTable(Map<String, Object> attributes) {
        return (String) attributes.get("table");
    }

    private String getDB(Map<String, Object> attributes) {
        return (String) attributes.get("db");
    }

    private String getContextId(Map<String, Object> attributes) {
        return (String) attributes.get("contextId");
    }

    private String getSubscriberName(Map<String, Object> attributes) {
        if (attributes != null) {
            String value = (String) attributes.get("name");
            String contextId = (String) attributes.get("contextId");
            return contextId + "_" + value;
        }

        throw new IllegalStateException("'contextId' 和 'name' 必须提供在注解 @"
                + Subscriber.class.getSimpleName());
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableSubscriber.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();

        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
