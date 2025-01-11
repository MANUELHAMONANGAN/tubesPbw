package com.example.demo.etalase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EtalaseController {

    @GetMapping("/etalase")
    public String etalaseView(Model model, @RequestParam(required = false) String page) {
        model.addAttribute("pageCount", 4);
        model.addAttribute("currentPage", 1);
        if(!(page == null)) {
            if(page.equals("2")) {
                model.addAttribute("currentPage", page);
                return "/etalase/contohPage2";
            }else if(page.equals("3")) {
                model.addAttribute("currentPage", page);
                return "/etalase/contohPage3";
            }else if(page.equals("4")) {
                model.addAttribute("currentPage", page);
                return "/etalase/contohPage4";
            }else {
                model.addAttribute("currentPage", 1);
                return "/etalase/index";
            }
        }
        return "/etalase/index";
    }
}
