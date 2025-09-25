package com.elasticsearch.search.user;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    SaveSearchRepository saveSearchRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;


    }

    public User registerUser(String username, String password, Set<String> roles) {
        User user = this.findByUsername(username);
        if(user == null) {
            user = new User();
            user.setUsername(username);
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User getMe(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = this.findByUsername(username);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findByUsername(username);
        return user;
    }
    public void storeSecurityContextInSession() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // Check if request attributes are available
        if (servletRequestAttributes == null) {

            return;
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpSession session = request.getSession(true);

        // Store the security context in the session
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
    public SaveSearch save(SaveSearch saveSearch){
        if(saveSearch == null)
            return null;
        if(saveSearch.id  == null && saveSearch.getName() != null){
            SaveSearch byName = saveSearchRepository.findByName(saveSearch.getName());
            if(byName != null)
                saveSearch.setId(byName.id);
        }
        saveSearch.setCreated(new Date());
        return saveSearchRepository.save(saveSearch);
    }

    public Page<SaveSearch> getSaveSearches(Pageable pageable) {
        User me = getMe();
        if(me == null)
            throw new AccessDeniedException("");

        return saveSearchRepository.findByUserId(me.getId(), pageable);
    }
}
