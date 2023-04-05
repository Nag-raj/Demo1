package com.olympus.oca.commerce.integrations.ping.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympus.oca.commerce.integrations.constants.OcaIntegrationConstants;
import com.olympus.oca.commerce.integrations.ping.service.PingIntegrationService;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.commercefacades.user.ping.data.*;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DefaultPingIntegrationService implements PingIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPingIntegrationService.class);


    private ConfigurationService configurationService;
    public static final String SLASH = "/";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "Basic ";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String URL_ENCODED = "urlencoded";
    public static final String APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_CREDENTIALS = "client_credentials";

    public static final String APPLICATION_PING= "application/vnd.pingidentity.user.import+json";
    public static final String APPLICATION_JSON ="application/json";

    public static final String BEARER = "Bearer ";

    @Override
    public Boolean executeActivation(String pingUserId) throws BusinessException {
        String accessToken =getAccessTokenForPing();
        configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_API_PATH);
        String envId = configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_ENV_ID);
        String apiPath= configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_API_PATH);
        String environments= configurationService.getConfiguration().getString(OcaIntegrationConstants.ENVIRONMENTS);
        String users= configurationService.getConfiguration().getString(OcaIntegrationConstants.USERS);
        String enabled= configurationService.getConfiguration().getString(OcaIntegrationConstants.ENABLED);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(apiPath).append(environments).append(envId).append(users).append(pingUserId).append(enabled);
        String activationUrl = urlBuilder.toString();
        Boolean result= null;
        result = executeAccountActivation(activationUrl,accessToken);
        return result;
    }
    protected String getAccessTokenForPing() throws BusinessException {
        String clientId = configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_CLIENT_ID);
        String clientSecret = configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_CLIENT_SECRET);
        String authPath = configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_AUTH_PATH);
        String envId = configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_ENV_ID);
        String asToken = configurationService.getConfiguration().getString(OcaIntegrationConstants.AS_TOKEN);
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(authPath).append(SLASH).append(envId).append(asToken);
        String url = urlBuilder.toString();
        String accessTokenForPing =fetchAccessToken(url,clientId,clientSecret);
        return accessTokenForPing;
    }

    protected String fetchAccessToken(String url, String clientId, String clientSecret) throws BusinessException {
        final RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = addAccessTokenHeaders(clientId,clientSecret);
        MultiValueMap<String, String> request= addAccessTokenRequest();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(),
                new HttpEntity<>(request, headers), String.class);
        return getAccessToken(response);
    }

    private String getAccessToken(ResponseEntity<String> response) throws BusinessException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
            return accessToken;
        } catch (JsonProcessingException e) {
            LOG.error("Exception occured while getting access token:",e);
            throw new BusinessException("Unable to activate customer account, Please try later");
        }

    }

    protected Boolean executeAccountActivation(String activationUrl, String accessToken) throws BusinessException {
        final RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= addActivationHeader(accessToken);
        Map<String, Boolean> requestBody =addActivationRequest();
        HttpEntity<String> entity = new HttpEntity<>(getRequestBodyString(requestBody), headers);
        ResponseEntity<String> response = restTemplate.exchange(activationUrl, HttpMethod.PUT, entity, String.class);
        return  isCustomerEnabled(response);
    }

    private String getRequestBodyString(Map<String, Boolean> requestBody){
        ObjectMapper mapper = new ObjectMapper();
        String requestBodyString = "";
        try {
            requestBodyString=mapper.writeValueAsString(requestBody);

            return requestBodyString;
        } catch (final JsonProcessingException e) {
            LOG.error("Exception occured while fetching activation request:",e);
        }
        return requestBodyString;
    }

    private Boolean isCustomerEnabled(ResponseEntity<String> response) throws BusinessException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response.getBody());
            String result = jsonNode.get("enabled").asText();
            return "true".equalsIgnoreCase(result);
        } catch (JsonProcessingException e) {
            LOG.error("Exception while fetching activation response:",e);
            throw new BusinessException("Unable to activate customer account, Please try later");
        }

    }

    private MultiValueMap<String, String> addAccessTokenRequest() {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add(URL_ENCODED, APPLICATION_FORM_URL_ENCODED);
        request.add(GRANT_TYPE, CLIENT_CREDENTIALS);
        return request;
    }

    private HttpHeaders addAccessTokenHeaders(String clientId, String clientSecret) {
        final String base64Credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BASIC + base64Credentials);
        headers.add(CONTENT_TYPE, APPLICATION_FORM_URL_ENCODED);
        return headers;
    }

    private HttpHeaders addActivationHeader(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION,BEARER + accessToken);
        headers.add(CONTENT_TYPE, APPLICATION_JSON);
        return headers;
    }

    private Map<String, Boolean> addActivationRequest(){
        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("enabled", true);
        return requestBody;
    }

    @Override
    public String executeCustomerCreation(CustomerActivationData customerActivationData) throws BusinessException {
        PingCustomerCreationData pingRequestData = new PingCustomerCreationData();
        populatePingRequestData(customerActivationData,pingRequestData);
        return sendCustomerCreationRequestToPing(pingRequestData);
    }

    private void populatePingRequestData(CustomerActivationData customerActivationData,PingCustomerCreationData pingRequestData){
        PingNameData pingNameReqData = new PingNameData();
        PingPopulationData pingPopulationReqData = new PingPopulationData();
        PingPasswordData pingPwdReqData = new PingPasswordData();
        pingNameReqData.setGiven(customerActivationData.getName()!=null?customerActivationData.getName().split(" ")[0]:"");
        pingNameReqData.setFamily(customerActivationData.getName()!=null && customerActivationData.getName().split(" ").length>1?customerActivationData.getName().split(" ")[1]:"");
        pingRequestData.setName(pingNameReqData);
        pingPopulationReqData.setId(configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_POP_ID));
        pingRequestData.setPopulation(pingPopulationReqData);
        pingPwdReqData.setForceChange(Boolean.FALSE);
        pingPwdReqData.setValue(customerActivationData.getPassword());
        pingRequestData.setPassword(pingPwdReqData);
        pingRequestData.setEmail(customerActivationData.getEmailId());
        pingRequestData.setUsername(customerActivationData.getEmailId());
        pingRequestData.setEnabled(Boolean.FALSE);
        pingRequestData.setMobilePhone(customerActivationData.getContactID());
    }

    private String sendCustomerCreationRequestToPing(PingCustomerCreationData pingRequestData) throws BusinessException {
        String accessToken = getAccessTokenForPing();
        String activationUrl = preparePingCustomerCreationUrl();
        return createCustomerInPing(activationUrl,accessToken,pingRequestData);
    }

    private String preparePingCustomerCreationUrl(){
        String envId = configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_ENV_ID);
        String apiPath= configurationService.getConfiguration().getString(OcaIntegrationConstants.PING_API_PATH);
        String env = configurationService.getConfiguration().getString(OcaIntegrationConstants.ENVIRONMENTS);
        String users= configurationService.getConfiguration().getString(OcaIntegrationConstants.USERS);
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(apiPath).append(env).append(envId).append(users);
        return urlBuilder.toString();
    }


    private String createCustomerInPing (String creationUrl, String accessToken,PingCustomerCreationData pingRequestData) throws BusinessException {
        final RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add(AUTHORIZATION,BEARER + accessToken);
        headers.add(CONTENT_TYPE, APPLICATION_PING);
        String requestBodyString = getAsJsonBody(pingRequestData);
        if(LOG.isDebugEnabled()){
            LOG.info("Ping Request details " +requestBodyString);
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(creationUrl);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(),
                    new HttpEntity<>(requestBodyString, headers), String.class);
            return getPingUserId(response);
        }catch(Exception ex) {
            LOG.info("Unable to create User in Ping :" + ex);
            throw new BusinessException("Customer cannot be created,Please try again later");
        }

    }

    protected String getPingUserId(final  ResponseEntity<String> response) throws BusinessException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("id").asText();
         }
            catch (final JsonProcessingException e)
            {
                LOG.error(" Json Processing Exception  : ", e);
                throw new BusinessException("Customer with cannot be created,Please try again later");
            }

        }
    protected String getAsJsonBody(final PingCustomerCreationData pingRequestData) throws BusinessException {
        final ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.writeValueAsString(pingRequestData);
        }
        catch (final JsonProcessingException e)
        {
            LOG.error(" Json Processing Exception  : ", e);
            throw new BusinessException("Customer with cannot be created,Please try again later");
        }
    }


    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
