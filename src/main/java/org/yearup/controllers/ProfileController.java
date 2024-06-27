package org.yearup.controllers;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;
import org.yearup.data.*;
import org.yearup.models.*;

import javax.servlet.http.*;
import java.security.*;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public Profile get(Principal principal) {
        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        return profileDao.getById(userId);
    }

    @PutMapping
    public void update(Principal principal, @RequestBody Profile profile) {
        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        profileDao.update(userId, profile);
    }
}
