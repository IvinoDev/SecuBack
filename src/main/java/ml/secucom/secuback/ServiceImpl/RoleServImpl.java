package ml.secucom.secuback.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.secucom.secuback.Model.Role;
import ml.secucom.secuback.Repository.RoleRepository;
import ml.secucom.secuback.Service.RoleService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoleServImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role updateRole(Role role) {
        // TODO Auto-generated method stub
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(long id) {
        // TODO Auto-generated method stub
        roleRepository.deleteById(id);

    }

    @Override
    public List<Role> RolesList() {
        // TODO Auto-generated method stub
        return roleRepository.findAll();
    }
}
