package com.migration.presentation;

import com.migration.presentation.http.HttpMethod;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Resource
public class UserResource {


    @Autowired
    private HttpMethod httpMethod;

    public Object getAll() throws IOException, InterruptedException {
        return this.httpMethod.get("http://localhost:8080/users");
    }
}
