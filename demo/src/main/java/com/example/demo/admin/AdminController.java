package com.example.demo.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.graph.GraphService;
import com.example.demo.graph.TransaksiGraph;

@Controller
public class AdminController {
    @Autowired
    private GraphService graphService;
    
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

        int pageCount = 8;
        model.addAttribute("pageSaatIni","aktor");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "/admin/aktor";
    }

    @GetMapping("/graph/")
    public String graph(Model model){
        List<List<Object>> graphData = new ArrayList<>();
        graphService.getAllGraphData().forEach(data -> {
            List<Object> row = new ArrayList<>();
            row.add(data.getTanggal().toString());
            row.add(data.getTotal().doubleValue());
            graphData.add(row);
        });

        model.addAttribute("graphTitle", "Bulan November");
        model.addAttribute("graphData", graphData);
        return "/graph/graph";
    }
}