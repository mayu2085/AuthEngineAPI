package com.sm.engine.service;

import com.sm.engine.domain.LdapAttribute;
import com.sm.engine.repository.LdapAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The service provides operations on LdapAttribute document.
 */
@Service
public class LdapAttributeService {

    /**
     * The repository instance.
     */
    @Autowired
    private LdapAttributeRepository ldapAttributeRepository;

    /**
     * Gets the names of all LDAP attributes which are enabled.
     *
     * @return names of all LDAP attributes which are enabled
     */
    public List<String> getAllNames() {
        LdapAttribute ldapAttributeExample = new LdapAttribute();
        ldapAttributeExample.setEnabled(true);

        Example<LdapAttribute> example = Example.of(ldapAttributeExample);

        List<LdapAttribute> results = ldapAttributeRepository.findAll(example);
        return results.stream().map(LdapAttribute::getName).collect(Collectors.toList());
    }
}
