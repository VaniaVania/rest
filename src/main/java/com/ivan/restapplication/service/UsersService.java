package com.ivan.restapplication.service;


import com.ivan.restapplication.dto.UserDTO;
import com.ivan.restapplication.models.*;
import com.ivan.restapplication.repository.UsersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UsersService{

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public void save(@RequestBody User user){
        Follower follower = user.getFollowers();
        follower.setUser(user);

        ExplicitContent explicitContent = user.getExplicit_content();
        explicitContent.setUser(user);


        ExternalUrl externalUrl = user.getExternal_urls();
        externalUrl.setUser(user);

        List<Image> images = user.getImages();
        images.forEach(image -> image.setUser(user));

        user.setCreatedAt(LocalDateTime.now());

        if(usersRepository.findById(user.getId()).isEmpty()){
            usersRepository.save(user);
        }

    }


}
