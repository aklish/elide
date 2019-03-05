package com.yahoo.elide.standalone.models;

import com.yahoo.elide.annotation.CreatePermission;
import com.yahoo.elide.annotation.UpdatePermission;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Getter
@Setter
@UpdatePermission(expression = "Prefab.Role.All")
@CreatePermission(expression = "Prefab.Role.All")
public class Child {
    @Id
    private UUID id;
    @Size(max = 200)
    private String childName;
    @ManyToOne
    private Parent parent;

    public Child() {
    }

    @Transient
    public boolean isNew() {
        return id == null;
    }
}