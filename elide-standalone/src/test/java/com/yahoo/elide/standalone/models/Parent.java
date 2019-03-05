package com.yahoo.elide.standalone.models;

import com.yahoo.elide.annotation.CreatePermission;
import com.yahoo.elide.annotation.SharePermission;
import com.yahoo.elide.annotation.UpdatePermission;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Getter
@Setter
@UpdatePermission(expression = "Prefab.Role.All")
@CreatePermission(expression = "Prefab.Role.All")
public class Parent {
    @Id
    private Long id;

    @NonNull
    @Column(unique = true, nullable = false)
    @Size(max = 10)
    private String parentName;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Child> childs;

    @Transient
    public boolean isNew() {
        return id == null;
    }
}
