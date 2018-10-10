package com.example.demo.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotBlank(message = "id不能为空")
    private Long id;
    @Length(max = 10, message = "name长度不能超过10")
    private String name;

}
