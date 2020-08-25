/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author halon
 */

@Controller
public class ConnectionController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @PostMapping("/accounts/{id}")
    public String addConnection(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account connecter = accountRepository.findByUsername(username);
        Account receiver = accountRepository.getOne(id);
        boolean notFound = true;
        for (Connection connection : connecter.getConnectionsFromThisAccount()) {
            if (connection.getReceiver().getUsername().equals(receiver.getUsername())) {
                notFound = false;
            }
        }
        for (Connection connection : connecter.getConnectionsToThisAccount()) {
            if (connection.getReceiver().getUsername().equals(connecter.getUsername())) {
                notFound = false;
            }
        }
        if (notFound) {
            Connection connection = new Connection(connecter, receiver, false);
            connectionRepository.save(connection);
        }
        return "redirect:/profilepage";
    }

    @PostMapping("/connections/{id}")
    public String acceptConnection(@PathVariable Long id) {
        Connection connection = connectionRepository.getOne(id);
        connection.setAccepted(true);
        connectionRepository.save(connection);
        return "redirect:/profilepage";
    }

    @DeleteMapping("/connections/{id}")
    public String remove(@PathVariable Long id) {
        connectionRepository.deleteById(id);
        return "redirect:/profilepage";
    }

}
