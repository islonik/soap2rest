package org.spring.soap2rest.soap.impl;

import com.google.common.util.concurrent.Uninterruptibles;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.logic.MulticastLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by nikilipa on 3/22/17.
 */
@Configuration
@PropertySource("classpath:impl.properties")
@EnableIntegration
public class ImplConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static Executor executor() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public static MessageChannel publishSubscribeChannel() {
        PublishSubscribeChannel publishSubscribeChannel = new PublishSubscribeChannel();
        publishSubscribeChannel.setApplySequence(true);
        return publishSubscribeChannel;
    }

    @Bean
    public IntegrationFlow fastFlow() {
        final Executor executor = executor();

        return IntegrationFlows
                .from("subscribeFast")
                .publishSubscribeChannel(executor, s -> s
                        .applySequence(true)
                        .subscribe(f -> f
                                .channel("fastChannel1"))
                        .subscribe(f -> f
                                .channel("fastChannel2"))
                        .subscribe(f -> f
                                .channel("fastChannel3"))
                )
                .get();

    }

    @Bean
    public IntegrationFlow mediumFlow() {
        final Executor executor = executor();

        return IntegrationFlows
                .from("subscribeMedium")
                .publishSubscribeChannel(executor, s -> s
                        .applySequence(true)
                        .subscribe(f -> f
                                .channel("mediumChannel1"))
                        .subscribe(f -> f
                                .channel("mediumChannel2"))
                        .subscribe(f -> f
                                .channel("mediumChannel3"))
                )
                .get();

    }

    @Bean
    public IntegrationFlow slowFlow() {
        final Executor executor = executor();

        return IntegrationFlows
                .from("subscribeSlow")
                .publishSubscribeChannel(executor, s -> s
                        .applySequence(true)
                        .subscribe(f -> f
                                .channel("slowChannel1"))
                        .subscribe(f -> f
                                .channel("slowChannel2"))
                        .subscribe(f -> f
                                .channel("slowChannel3"))
                )
                .get();

    }

    @Bean
    public IntegrationFlow aggregateFastFlow() {
        return IntegrationFlows
                .from("aggregatorChannel")
                .aggregate(a -> a
                        .outputProcessor(g -> {
                            return MulticastLogic.chooseBetweenEntities(
                                    g.getMessages()
                                            .stream()
                                            .map(m -> (ServiceOrderStatus) m.getPayload())
                                            .collect(Collectors.toList())
                            );
                        })
                )
                .get();
    }


}
