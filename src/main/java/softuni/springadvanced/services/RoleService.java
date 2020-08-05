package softuni.springadvanced.services;


import softuni.springadvanced.models.entity.Role;
import softuni.springadvanced.models.service.RoleServiceModel;

import java.util.List;
import java.util.Set;

public interface RoleService {

    Role getRoleByAuthority(String authority);

    void saveRoleTypesInDatabase();

    Set<Role> getAllAuthorities();

    Set<RoleServiceModel> getAllAuthoritiesAsServiceModels();

}
