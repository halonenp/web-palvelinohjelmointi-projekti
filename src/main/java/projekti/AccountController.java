/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author halon
 */
@Controller
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SkillRepository skillRepository;

    @GetMapping("/accounts")
    public String listAllAccounts(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "login";
    }

    @GetMapping("/registration")
    public String viewRegistration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute Account account, BindingResult bindingResult, @RequestParam String username,
            @RequestParam String password) throws IOException {
        if (accountRepository.findByUsername(username) != null) {
            return "registration";
        }
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        Account user = new Account();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setConnectionsFromThisAccount(new ArrayList<>());
        user.setConnectionsToThisAccount(new ArrayList<>());
        user.setSkills(new ArrayList<>());

        accountRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/profilepage")
    public String viewOwnProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String username = auth.getName();
            Account account = accountRepository.findByUsername(username);
            model.addAttribute("username", username);
            model.addAttribute("skills", account.getSkills());
            List<Connection> connectionsTo = new ArrayList<>();
            List<Connection> connectionsFrom = new ArrayList<>();
            if (account != null) {
                connectionsTo = account.getConnectionsToThisAccount();
                connectionsFrom = account.getConnectionsFromThisAccount();
            }
            List<Connection> unacceptedConnectionsTo = new ArrayList<>();
            List<Connection> unacceptedConnectionsFrom = new ArrayList<>();
            List<Connection> acceptedConnectionsTo = new ArrayList<>();
            List<Connection> acceptedConnectionsFrom = new ArrayList<>();
            if (connectionsTo.isEmpty() && connectionsFrom.isEmpty()) {
                return "profilepage";
            }
            for (Connection connection : connectionsTo) {
                if (!connection.isAccepted()) {
                    unacceptedConnectionsTo.add(connection);

                } else {
                    acceptedConnectionsTo.add(connection);
                }
            }
            for (Connection connection : connectionsFrom) {
                if (connection.isAccepted()) {
                    acceptedConnectionsFrom.add(connection);
                } else {
                    unacceptedConnectionsFrom.add(connection);
                }
            }

            model.addAttribute("account", account);
            model.addAttribute("acceptedConnectionsTo", acceptedConnectionsTo);
            model.addAttribute("acceptedConnectionsFrom", acceptedConnectionsFrom);
            model.addAttribute("unacceptedConnectionsTo", unacceptedConnectionsTo);
            model.addAttribute("unacceptedConnectionsFrom", unacceptedConnectionsFrom);
        }
        return "profilepage";
    }

    public Account getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account currentAccount = accountRepository.findByUsername(auth.getName());
        return currentAccount;
    }

    @PostMapping("/accounts/{id}/skills/{skillId}")
    public String likeSkill(@PathVariable Long id, @PathVariable Long skillId) {
        Skill skill = skillRepository.getOne(skillId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (!skill.getLikers().contains(username)) {
            skill.setLikers(skill.getLikers() + username + " ");
            skill.setLikes(skill.getLikes() + 1);
        }
        skillRepository.save(skill);
        return "redirect:/accounts/{id}";
    }

    @GetMapping("/accounts/{id}")
    public String getOneAccount(Model model, @PathVariable Long id) {
        model.addAttribute("account", accountRepository.getOne(id));
        return "showprofile";
    }

    @PostMapping("/skills")
    public String addSkill(@RequestParam String name
    ) {
        Skill skill = new Skill();
        skill.setSkill(name);
        skill.setLikes(0);
        skill.setLikers("");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);

        skill.setAccount(account);

        skillRepository.save(skill);
        return "redirect:/profilepage";
    }

    @DeleteMapping("/skills/{id}")
    public String removeSkill(@PathVariable Long id) {
        skillRepository.deleteById(id);
        return "redirect:/profilepage";
    }

}
