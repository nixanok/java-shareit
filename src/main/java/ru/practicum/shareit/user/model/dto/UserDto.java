package ru.practicum.shareit.user.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validation.WithoutSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank.", groups = BasicInfo.class)
    @WithoutSpaces(message = "Name cannot has spaces.", groups = { BasicInfo.class, PatchInfo.class })
    private String name;

    @Email(message = "Email should be valid.", groups = { EmailInfo.class, PatchInfo.class} )
    @NotNull(message = "Email cannot be null.", groups = EmailInfo.class)
    private String email;

}
