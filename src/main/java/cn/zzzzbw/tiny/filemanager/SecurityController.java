package cn.zzzzbw.tiny.filemanager;

import cn.zzzzbw.tiny.filemanager.security.SecurityService;
import cn.zzzzbw.tiny.filemanager.storage.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author by zzzzbw
 * @since 2020/5/10 20:52
 */
@Controller
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/login")
    public String login() {
        return "loginForm";
    }

    @PostMapping("/login")
    public String handleLogin(String key, HttpServletRequest request) {
        if (securityService.login(key)) {
            HttpSession session = request.getSession();
            session.setAttribute("login", "ok");
        } else {
            throw new SecurityException("验证失败");
        }
        return "redirect:/";
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException exc) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
