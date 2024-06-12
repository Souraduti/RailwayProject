package com.railway.api.service;

import java.util.List;

public class ApiDetails {

    private String endpoint;
    private  String httpMethod;
    private String implementationClass;
    private  List<String> requiredParams;


    public ApiDetails(String endpoint, String httpMethod, List<String> requiredParams,String implementationClass) {
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.requiredParams = requiredParams;
        this.implementationClass = implementationClass;
    }

    public String getImplementationClass() {
        return implementationClass;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<String> getRequiredParams() {
        return requiredParams;
    }

    public void setRequiredParams(List<String> requiredParams) {
        this.requiredParams = requiredParams;
    }

}
