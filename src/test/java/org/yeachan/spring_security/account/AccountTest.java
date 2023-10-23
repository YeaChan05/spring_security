package org.yeachan.spring_security.account;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Test
    @WithAnonymousUser
    public void index_anonymous() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/").with(anonymous()))
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "yeachan",roles = "USER")
    public void index_user() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/").with(user("yeachan").roles("USER")))
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "yeachan",roles = "USER")
    public void admin_user() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/admin").with(user("yeachan").roles("USER")))
        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void admin_admin() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/admin").with(user("admin").roles("ADMIN")))
        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void login_success() throws Exception {
        String username="yeachan";
        String password = "123";
        Account user=createUser(username,password);
        mockMvc.perform(formLogin().user(username).password(password))
                .andExpect(authenticated());
    }

    @Test
    @Transactional
    public void login_success2() throws Exception {
        String username="yeachan";
        String password = "123";
        Account user=createUser(username,password);
        mockMvc.perform(formLogin().user(username).password(password))
                .andExpect(authenticated());
    }

    @Test
    @Transactional
    public void login_fail() throws Exception {
        String username="yeachan";
        String password = "123";
        Account user=createUser(username,password);
        mockMvc.perform(formLogin().user(user.getUsername()).password("12345"))
                .andExpect(unauthenticated());
    }

    private Account createUser(String username, String password) {
        Account account =new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole("USER");
        return accountService.createNew(account);
    }
}