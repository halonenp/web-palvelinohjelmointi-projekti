/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author halon
 */
@Controller
public class ChatController {
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    AccountRepository accountRepository;
    
    @GetMapping("/chatpage")
    public String listAllChats(Model model) {
        Pageable pageable = PageRequest.of(0, 15, Sort.by("timeposted").descending());
        model.addAttribute("chats", chatRepository.findAll(pageable));
        return "chatpage";
    }
    
    @PostMapping("/chatpage")
    public String AddAChatToChatpage(@RequestParam String chat) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);
        Chat c = new Chat();
        c.setContent(chat);
        c.setLikes(0);
        c.setWriter(account.getUsername());
        c.setLikers("");
        chatRepository.save(c);
        
        return "redirect:/chatpage";
    }
    
    @PostMapping("/chatpage/{id}")
    public String likeAChat(@PathVariable Long id) {
        Chat msg = chatRepository.getOne(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (!msg.getLikers().contains(username)) {
            msg.setLikers(msg.getLikers() + username + " ");
            msg.setLikes(msg.getLikes() + 1);
        }
        chatRepository.save(msg);
        return "redirect:/chatpage";
    }
    
    @PostMapping("/chatpage/{id}/comments")
    public String addACommentToAChat(@RequestParam String comment, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);
        Chat chat = chatRepository.getOne(id);
        if (chat.getComments().size() > 10) {
            chat.getComments().remove(0);
        }
        Comment com = new Comment();
        com.setWriter(account.getUsername());
        com.setComment(comment);
        com.setChat(chat);
        commentRepository.save(com);
        return "redirect:/chatpage";
    }
}
