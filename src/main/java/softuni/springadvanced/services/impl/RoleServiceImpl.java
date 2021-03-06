package softuni.springadvanced.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Role;
import softuni.springadvanced.models.entity.Roles;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.repositories.RoleRepository;
import softuni.springadvanced.services.RoleService;
import softuni.springadvanced.services.UserService;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
//@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, UserService userService, ModelMapper modelMapper, ModelMapper modelMapper1) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper1;
    }

    @Override
    public Role getRoleByAuthority(String authority) {
        return this.roleRepository.findByAuthority(authority);
    }

    @Override
    public void saveRoleTypesInDatabase() {

        Role admin = new Role("ROLE_ADMIN");
        Role user = new Role("ROLE_USER");

        this.roleRepository.save(admin);
        this.roleRepository.save(user);

    }

    @Override
    public Set<Role> getAllAuthorities() {
        return this.roleRepository.findAllAuthorities();
    }

    @Override
    public Set<RoleServiceModel> getAllAuthoritiesAsServiceModels() {
//        Set<Role> all = this.roleRepository.findAllAuthorities();
//        Set<RoleServiceModel> serviceModels = new LinkedHashSet<>();
//
//        for (Role role : all) {
//            RoleServiceModel roleServiceModel = this.modelMapper.map(role, RoleServiceModel.class);
//            serviceModels.add(roleServiceModel);
//        }
//
//        return serviceModels;
        return this.getAllAuthorities().stream().map(role -> this.modelMapper
                .map(role, RoleServiceModel.class)).collect(Collectors.toSet());
    }


}
