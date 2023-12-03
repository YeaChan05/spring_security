package org.yeachan.spring_security.form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yeachan.spring_security.account.AccountContext;
import org.yeachan.spring_security.account.AccountRepository;
import org.yeachan.spring_security.common.SecurityLogger;

import java.security.Principal;
import java.util.concurrent.Callable;

@Controller
public class SampleController {
    @Autowired
    SampleService sampleService;
    @Autowired
    AccountRepository accountRepository;
    @GetMapping("/")
    public String index(Model model,Principal principal){
        if(principal==null)
            model.addAttribute("message","Hello Spring Security");
        else model.addAttribute("message","Hello, "+principal.getName());
        return "index";
    }

    @GetMapping("/info")
    public String info(Model model){
        model.addAttribute("message","Info");
        return "info";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("message","Hello, "+principal.getName());
        AccountContext.setAccount(accountRepository.findByUsername(principal.getName()));
        sampleService.dashboard();
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model,Principal principal){
        model.addAttribute("message","Hello Admin, "+principal.getName());
        return "admin";
    }
    @GetMapping("/user")
    public String user(Model model,Principal principal){
        model.addAttribute("message","Hello User, "+principal.getName());
        return "user";
    }
    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> asyncHandler(){
        SecurityLogger.log("MVC");
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                SecurityLogger.log("Callable");
                return "Async Handler";
            }
        };
    }
}
