package com.example.groupboardservice.data.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
public class UserDto {

    private String id;
    private String email;
    private String password;
    private String roles;
}
