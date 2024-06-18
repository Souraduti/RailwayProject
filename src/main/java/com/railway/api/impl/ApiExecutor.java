package com.railway.api.impl;

import javax.ws.rs.core.Response;
import java.util.Map;

public interface ApiExecutor {
    Response execute(Map<String, String> parameters) throws Exception;
    Response validate(Map<String, String> parameters) throws Exception;
}
