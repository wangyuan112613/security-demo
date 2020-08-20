package com.example.securitydemo.model;

import com.example.securitydemo.model.enumeration.AuthType;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * 权限实体表
 *
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"id","name","code","remark","type"})
@EqualsAndHashCode
@Table(name = "authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 25)
    private String name;

    private String      code;
    private String      remark;
    private AuthType    type;

    @ManyToMany(fetch = FetchType.LAZY)//懒加载   快速查询 不会查询role表
    @JoinTable(
        name = "role_authority",
        joinColumns = {@JoinColumn(name = "authority_id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Roles> roles;

//    @ManyToMany(fetch = FetchType.LAZY)//懒加载   快速查询 不会查询menu表
//    @JoinTable(
//        name = "menu_authority",
//        joinColumns = {@JoinColumn(name = "authority_id")},
//        inverseJoinColumns = {@JoinColumn(name = "menu_id")})
//    private List<Menu> menus;

    @ManyToMany(fetch = FetchType.LAZY)//懒加载   快速查询 不会查询resource表
    @JoinTable(
        name = "operation_authority",
        joinColumns = {@JoinColumn(name = "auth_id")},
        inverseJoinColumns = {@JoinColumn(name = "oper_id")})
    private List<Operation> operations;
}
