package com.serviya.msservicerequest.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserClientResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Boolean activo;
}
