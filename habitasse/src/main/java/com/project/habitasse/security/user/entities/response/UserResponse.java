package com.project.habitasse.security.user.entities.response;

import com.project.habitasse.domain.offer.entities.Offer;
import com.project.habitasse.security.person.entities.Person;
import com.project.habitasse.security.user.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;
    private String username;
    private String name;
    private String email;
    private String password;
    private List<Offer> offers;
    private String role;
    private Person person;
    private String newPassword;
    private String updateDate;
    private String creationDate;
    private String birthday;
    private String phone;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
    private Integer demandsQuantity;
    private Long remainingDays;
    private Boolean isAccountConfirmed;

    public static UserResponse mapEntityToResponse(User user, Integer demandsQuantity, Long remainingDays) {

        return UserResponse.builder()
                .username(user.getUsernameForDto())
                .id(user.getId())
                .name(user.getPerson().getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .birthday(user.getPerson().getBirthday() != null ? user.getPerson().getBirthday().toString() : null)
                .phone(user.getPerson().getPhone())
                .role(user.getRole().name())
                .demandsQuantity(demandsQuantity > 0 ? demandsQuantity : 0)
                .remainingDays(remainingDays != null ? remainingDays : null)
                .isAccountConfirmed(user.getIsAccountConfirmed())
                .build();
    }
}
