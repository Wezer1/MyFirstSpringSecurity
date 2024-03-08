package net.proselyte.springsecuritydemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data// включает аннотации @Getter, @Setter, @ToString, @EqualsAndHashCode
@AllArgsConstructor//конструктор со всеми полями
public class Developer {
    private Long id;
    private String firstName;
    private String lastName;
}
