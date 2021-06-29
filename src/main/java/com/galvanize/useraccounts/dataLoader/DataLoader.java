package com.galvanize.useraccounts.dataLoader;

import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;

import static org.aspectj.runtime.internal.Conversions.intValue;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    AddressRepository addressRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {

    }

    private void seedUserData() {

        //IF USERS TABLE ALREADY HAS 10 RECORDS, DON'T FILL IT UP
        List l = entityManager.createNativeQuery("SELECT COUNT(id) FROM users").getResultList();
        for (Object c : l) {
            int count = intValue(c);
            if (count >= 10) {
                return;
            }
        }

        //CREATE USER
        List<String> names = new ArrayList<>();
        names.add("Bob Smith");
        names.add("Jane Doe");
        names.add("Andy Li");
        names.add("Rafael Parker");
        names.add("Rob Snyder");
        names.add("Yvonne Spears");
        names.add("Monica Croissant");
        names.add("Peter Isaguy");
        names.add("Elizabeth Swan");
        names.add("Jack Sparrow");

        for (int i = 0; i < names.size(); i++) {
            User userToAdd = new User(Long.valueOf(i),
                    names.get(i).split(" ")[0] + "user",
                    names.get(i).split(" ")[0],
                    names.get(i).split(" ")[1],
                    names.get(i).split(" ")[0] + "@email.com"
            );
            usersRepository.save(userToAdd);
        }

        /*
        for(int i = 0; i < 70; i++) {
            //CREATE TITLE
            String title = loremIpsum.getParagraphs()[(int) Math.floor(Math.random() * 30)].split("\\. ")[0].substring(0, 25);

            //CREATE BODY (every 3rd paragraph will have more than 255 characters)
            String paragraph = loremIpsum.getParagraphs()[(int) Math.floor(Math.random() * 30)];
            String body = i % 3 == 0 ? paragraph.substring(0, 250) + "." : paragraph.substring(0, 400) + ".";

            //CREATE POST
            Post post = new Post((long) Math.ceil(Math.random() * 10), body, title, Post.Visibility_status.PUBLIC);

            //CREATE MEDIA
            for(int j = 0; j < Math.floor(Math.random() * 5); j++) {
                Media media = new Media("https://picsum.photos/id/" + (int) Math.ceil(100 + Math.random() * 50) + "/200", "alt text" + j, Media.Media_Type.IMAGE);
                post.getMediaList().add(media);
            }

            //EVERY 5 POSTS WILL HAVE VIDEO URLS (1 or 2)
            if(i % 5 == 0) {
                for(int j = 0; j < Math.ceil(Math.random() * 2); j++) {
                    Media media = new Media(videos.getVideos()[(int) Math.floor(Math.random() * 16)], "alt text" + i, Media.Media_Type.VIDEO);
                    post.getMediaList().add(media);
                }
            }

            List<Media> mediaList = post.getMediaList();
            for (Media media : mediaList) {
                media.setPost(post);
                mediaRepository.save(media);
            }

            postRepository.save(post);
        }

        //ASSIGN TAGS TO POSTS. FOR SOME REASON I HAD TO PLACE THIS OUTSIDE THE PREVIOUS LOOP
        //KEPT HAVE ISSUES WITH A DUPLICATE ERROR
        for(int i = 0; i < 70; i++) {
            Post post = postRepository.findById((long) i + 1).orElse(null);
            for(int j = 0; j < Math.floor(Math.random() * 5); j++) {
                post.addTag(tags.get((int) Math.floor(Math.random() * 30)));
            }
            postRepository.save(post);*/
    }
}



