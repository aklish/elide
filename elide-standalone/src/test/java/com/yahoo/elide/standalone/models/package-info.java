@Include(rootLevel = true)
@DeletePermission(expression = "Prefab.Role.None")
@UpdatePermission(expression = "Prefab.Role.None")
@CreatePermission(expression = "Prefab.Role.None")
package com.yahoo.elide.standalone.models;

import com.yahoo.elide.annotation.CreatePermission;
import com.yahoo.elide.annotation.DeletePermission;
import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.UpdatePermission;
