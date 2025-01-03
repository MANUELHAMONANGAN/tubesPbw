package com.example.demo.admin;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.user.UserRepository;

@Controller
public class AdminController {
    private static final int maxPage = 5;

    @Autowired
    private AdminRepository repo;

    @GetMapping("/admin/")
    public String index(Model model){
        model.addAttribute("pageSaatIni","home");
        return "/admin/dashboard";
    }

    @GetMapping("/genre/")
    public String genre(Model model){
        model.addAttribute("pageSaatIni","genre");
        return "/admin/genre";
    }
    
    @GetMapping("/aktor/")
    public String aktor(Model model, @RequestParam( defaultValue = "",required = false) String filter,
     @RequestParam(defaultValue = "1", required = false) Integer page){

        List<Aktor> user;
        int pageCount;

        if (filter != null && !filter.isEmpty()){
            user = this.repo.findAktorByName(filter, maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountFilter(filter)/maxPage); 
        } else {
            user = this.repo.findAllAktor(maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCount()/maxPage); 

        }

        model.addAttribute("Aktor", user);
        model.addAttribute("pageSaatIni","aktor");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "/admin/aktor";
    }

    @GetMapping("/aktor/edit/")
    public String editAktor(Model model, @RequestParam int idAktor){
        List<Aktor> user = this.repo.findAktorById(idAktor);
        model.addAttribute("Aktor", user.getFirst());
        return "admin/editAktor";
    }

    @PostMapping("/aktor/edit/")
    public String updateAktor(Model model, @RequestParam int idAktor, @RequestParam String nama , @RequestParam Date tanggal_lahir, @RequestParam String deskripsi_diri, @RequestParam MultipartFile foto) throws Exception{
        repo.update(idAktor, nama, tanggal_lahir, deskripsi_diri);
        
        if(foto != null && !foto.isEmpty()){
            byte[] imageBytes =  foto.getBytes();
            repo.updateGambar(idAktor, imageBytes);
        }
        // return "redirect:/aktor/edit/?idAktor=" +idAktor;
        return "redirect:/aktor/";
    }
}