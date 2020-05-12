package cn.zzzzbw.tiny.filemanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }


    @GetMapping("/")
    public String listUploadedFiles(Model model) {
        List<FileInfo> all = managerService.loadAll();
        all.forEach(fileInfo -> fileInfo.setDownloadPath(MvcUriComponentsBuilder.fromMethodName(ManagerController.class,
                "serveFile", fileInfo.getPath().getFileName().toString()).build().toUri().toString()));
        model.addAttribute("files", all);

        return "index";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = managerService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        managerService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping("/login")
    public String handleLogin(String key, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (managerService.login(key)) {
            HttpSession session = request.getSession();
            session.setAttribute("login", "ok");
        } else {
            redirectAttributes.addFlashAttribute("message",
                    "Login error!");
        }
        return "redirect:/";
    }

    @ExceptionHandler(ManagerException.class)
    public String handleManagerException(ManagerException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message",
                exc.getMessage());
        return "redirect:/";
    }

}
