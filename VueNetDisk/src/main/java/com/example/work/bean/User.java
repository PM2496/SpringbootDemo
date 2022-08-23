package com.example.work.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @AutoConfigureOrder
    private Integer id;
    private String name;
    private String password;
    private String email;
}
