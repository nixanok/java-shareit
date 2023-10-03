package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validation.WithoutSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank.", groups = BasicInfo.class)
    @WithoutSpaces(message = "Name cannot has spaces.", groups = BasicInfo.class)
    private String name;

    @Email(message = "Email should be valid.", groups = EmailInfo.class)
    @NotBlank(message = "Email cannot be blank.", groups = BasicInfo.class)
    private String email;

}
