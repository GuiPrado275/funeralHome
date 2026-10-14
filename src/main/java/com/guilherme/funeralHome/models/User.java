package com.guilherme.funeralHome.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guilherme.funeralHome.models.enums.ProfileEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = User.TABLE_NAME) //tabela no banco de dados
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    public static final String TABLE_NAME = "users";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id gerado pelo banco (auto incremento)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @NotBlank(message = "O nome não pode estar em branco.")
    private String name;

    @Email(message = "O e-mail deve ser válido!")
    @Column(name = "email", length = 50, nullable = false, unique = true)
    @Size(min = 5, max = 50, message = "O e-mail deve ter entre 5 e 50 caracteres.")
    @NotBlank(message = "O e-mail não pode estar em branco.")
    private String email;

    //só dígitos; obrigatório para clientes (liga o titular ao plano), opcional para o admin inicial
    @CPF(message = "O CPF deve ser válido!")
    @Column(name = "cpf", length = 11, unique = true)
    private String cpf;

    @Column(name = "phone", length = 20)
    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
    private String phone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 60, message = "A senha deve ter entre 8 e 60 caracteres.")
    @NotBlank(message = "A senha não pode estar em branco.")
    private String password;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "profile", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER) //carrega os perfis
    @CollectionTable(name = "user_profile")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Integer> profiles = new HashSet<>(); //lista sem valores repetidos

    public Set<ProfileEnum> getProfiles() {
        //converte os códigos salvos (1, 2, 3) de volta em ProfileEnum (ADMIN, AGENT, USER)
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profileEnum) {
        this.profiles.add(profileEnum.getCode());
    }
}
