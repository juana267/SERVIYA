package com.serviya.msservicerequest.client;

import com.serviya.msservicerequest.config.FeignAuthConfig;
import com.serviya.msservicerequest.dto.user.UserClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-users", configuration = FeignAuthConfig.class)
public interface UsersClient {

    @GetMapping("/api/v1/users/{id}")
    UserClientResponse findById(@PathVariable("id") Long id);
}
