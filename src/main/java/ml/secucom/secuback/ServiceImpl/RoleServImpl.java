package ml.secucom.secuback.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.secucom.secuback.Repository.RoleRepository;
import ml.secucom.secuback.Service.RoleService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoleServImpl implements RoleService {

    private final RoleRepository roleRepository;
}
