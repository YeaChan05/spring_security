package org.yeachan.spring_security.account;

import ch.qos.logback.classic.spi.ConfiguratorRank;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@ConfiguratorRank
public class AccessDeniedController {
    @GetMapping("/access-denied")
    public String accessDenied(Principal principal, Model model){
        model.addAttribute("name",principal.getName());
        return "access-denied";
    }
}
