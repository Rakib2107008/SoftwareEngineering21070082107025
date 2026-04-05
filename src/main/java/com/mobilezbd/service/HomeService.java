package com.mobilezbd.service;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    public Map<String, String> getHomeData() {
        return Map.of(
                "status", "UP",
                "message", "MobileZBD backend is running");
    }
}
