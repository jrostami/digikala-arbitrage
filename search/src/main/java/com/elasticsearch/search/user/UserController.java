package com.elasticsearch.search.user;

import javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("user")
public class UserController {


    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("register")
    public void registerUser(@RequestBody UserDto userDto) {
        userService.registerUser(userDto.getEmail(), userDto.getPassword(), new HashSet<>(userDto.getRoles()));
    }

    @GetMapping("me")
    public UserDto getMe() throws NotFoundException {
        User me = userService.getMe();
        if (me == null) {
            throw new AccessDeniedException("Not Logged In");
        }
        return new UserDto(me);
    }

    @PostMapping("login")
    public void loginUser(@RequestBody UserDto userDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userService.storeSecurityContextInSession();
    }

    @PostMapping("saveSearch")
    public SaveSearch save(@RequestBody SaveSearch saveSearch) {
        User me = userService.getMe();
        if (me == null)
            throw new AccessDeniedException("");
        saveSearch.setUserId(me.getId());
        return userService.save(saveSearch);
    }

    @GetMapping("saveSearch")
    public Page<SaveSearch> saveSearches(Pageable pageable) {
        return userService.getSaveSearches(pageable);
    }
}
