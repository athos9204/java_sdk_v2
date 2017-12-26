package com.gsma.mobileconnect.r2.web;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

@SpringBootConfiguration
public class MobileConnectConfiguration {

    @Value("${MC_CONFIG:./src/main/resources/config/defaultData.json}")
    private String configFilePath;

    @Value("${MC_CONFIG:./src/main/resources/config/defaultDataWD.json}")
    private String configFilePathWD;

    @Bean
    public MobileConnectConfig mobileConnectConfig() throws IOException, ParseException {
        JSONObject config = (JSONObject)new JSONParser().parse(new FileReader(configFilePath));
        return new MobileConnectConfig.Builder()
                .withClientId(config.get("clientID").toString())
                .withClientSecret(config.get("clientSecret").toString())
                .withDiscoveryUrl(URI.create(config.get("discoveryURL").toString()))
                .withRedirectUrl(URI.create(config.get("redirectURL").toString()))
                .withApiVersion(config.get("apiVersion").toString())
                .build();
    }

    @Bean
    public OperatorUrls operatorUrls() throws IOException, ParseException {
        JSONObject config = (JSONObject)new JSONParser().parse(new FileReader(configFilePathWD));
        return new OperatorUrls.Builder()
                .withProviderMetadataUri(config.get("metadataURL").toString())
                .withAuthorizationUrl(config.get("authorizationURL").toString())
                .withRequestTokenUrl(config.get("tokenURL").toString())
                .withUserInfoUrl(config.get("userInfoURL").toString())
                .build();
    }

    @Bean
    public MobileConnectWebInterface mobileConnectWebInterface(
            @Autowired final MobileConnectConfig config)
    {
        return MobileConnect.buildWebInterface(config, new DefaultEncodeDecoder());
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
            @Autowired final ObjectMapper objectMapper)
    {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
