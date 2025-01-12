package com.example.demo.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; 
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.example.demo.laporan.LaporanService;
import com.example.demo.laporan.ScreenshootRequest;
import com.example.demo.laporan.TopGenre;
import com.example.demo.laporan.WeeklySales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AdminController {
    @Autowired
    private LaporanService laporanService;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private static final int maxPage = 5;

    @Autowired
    private AdminRepository repo;

    @GetMapping("/admin/")
    public String index(Model model){
        model.addAttribute("pageSaatIni","home");

        //HOME ISINYA LAPORAN BULAN INI
        //WEEKLY SALES
        WeeklySales weeklySales = this.laporanService.getWeeklySalesThisMonth();
        if(weeklySales == null){
            model.addAttribute("weeklySales", "Belum ada Penyewaan");
        }else{
            String weeklySalesRupiah = this.laporanService.formatRupiah(weeklySales.getWeeklySales());
            model.addAttribute("weeklySales", weeklySalesRupiah);
        }

        //JUMLAH FILM DISEWA
        model.addAttribute("jumlahPenyewaan", this.laporanService.getFilmDisewaThisMonth().get().getJumlahPenyewaan());

        //TOP GENRE
        TopGenre topGenre = this.laporanService.getTopGenreThisMonth();
        if(topGenre == null){
            model.addAttribute("topGenre", "Belum ada Penyewaan");
        }else{
            model.addAttribute("topGenre", topGenre.getNamaGenre());
        }

        //GRAPH
        model.addAttribute("graphTitle", "Bulan Ini");
        model.addAttribute("graphData", this.laporanService.getGraphDataThisMonth());

        //TOP 5 FILM PALING LAKU (PALING BANYAK DIPESAN)
        model.addAttribute("top5BestFilm", this.laporanService.getTop5BestFilmThisMonth());

        //TOP 5 FILM PALING GA LAKU (PALING SEDIKIT PENYEWAAN)
        model.addAttribute("top5WorstFilm", this.laporanService.getTop5WorstFilmThisMonth());

        //LIST OUT OF STOCK
        model.addAttribute("listOutOfStock", this.laporanService.getListOutOfStockThisMonth());

        //TOP 5 GENRE PALING LAKU (PALING BANYAK DIPESAN)
        model.addAttribute("top5BestGenre", this.laporanService.getTop5GenreThisMonth());
        return "/admin/dashboard";
    }

    @GetMapping("/genre/")
    public String genre(Model model){
        List<Genre> listGenre = this.repo.findAllGenre();

        model.addAttribute( "genreList", listGenre);
        if(!listGenre.isEmpty()){
            model.addAttribute("genre_available", true);
        } 
        model.addAttribute("pageSaatIni","genre");
        return "/admin/genre";
    }
    
    @PostMapping("/genre/")
    public String addGenre(Model model, @RequestParam String genre_name){
        this.repo.addGenre(genre_name);
        return "redirect:/genre/";
    }

    @GetMapping("/aktor/")
    public String aktor(Model model, @RequestParam( defaultValue = "",required = false) String filter,
     @RequestParam(defaultValue = "1", required = false) Integer page){

        List<Aktor> user;
        int pageCount;

        if (filter != null && !filter.isEmpty()){
            user = this.repo.findAktorByName(filter, maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountAktorFilter(filter)/maxPage); 
        } else {
            user = this.repo.findAllAktor(maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountAktor()/maxPage); 

        }

        model.addAttribute("Aktor", user);
        model.addAttribute("pageSaatIni","aktor");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "/admin/aktor";
    }
 
    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody ScreenshootRequest request) {
        return this.laporanService.generatePdf(request);
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

    @GetMapping("/aktor/tambah/")
    public String addAktor(Model model){
        return "admin/addAktor";
    }

    @PostMapping("/aktor/tambah/")
    public String postAddAktor(Model model, @RequestParam String nama, @RequestParam int tanggal_lahir, @RequestParam int bulan_lahir, @RequestParam int tahun_lahir, @RequestParam String deskripsi_diri, @RequestParam MultipartFile foto) throws Exception{
        LocalDate localDate = LocalDate.of(tahun_lahir, bulan_lahir, tanggal_lahir);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        repo.addAktor(nama, sqlDate, deskripsi_diri, foto.getBytes());
        return "redirect:/aktor/";
    }

    @GetMapping("/aktor/koleksi_film/")
    public String koleksi_film(Model model, @RequestParam( defaultValue = "",required = false) String filter,
    @RequestParam(defaultValue = "1", required = false) Integer page){
        List<Film> film;
        int pageCount;

        if (filter != null && !filter.isEmpty()){
            film = this.repo.findFilmByName(filter, maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountFilmFilter(filter)/maxPage); 
        } else {
            film = this.repo.findAllFilm(maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountFilm()/maxPage); 

        }

        model.addAttribute("listFilm", film);
        model.addAttribute("pageSaatIni","koleksi_film");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "admin/listfilm";
    }

    @PostMapping("/admin/laporanBulanan")
    public String laporanBulanan(Model model, @RequestParam(name = "tanggalAwal", required = true) String tanggalAwal, @RequestParam(name = "tanggalAkhir", required = true) String tanggalAkhir) {
        model.addAttribute("judulHalaman", "Laporan Bulanan " + tanggalAwal + " / " + tanggalAkhir);
        model.addAttribute("tanggalAwal", tanggalAwal);
        model.addAttribute("tanggalAkhir", tanggalAkhir);
        
        //LAPORAN BULAN FILTER TANGGAL
        //WEEKLY SALES
        WeeklySales weeklySales = this.laporanService.getWeeklySalesFilterTanggal(tanggalAwal, tanggalAkhir);
        if(weeklySales == null){
            model.addAttribute("weeklySales", "Belum ada Penyewaan");
        }else{
            String weeklySalesRupiah = this.laporanService.formatRupiah(weeklySales.getWeeklySales());
            model.addAttribute("weeklySales", weeklySalesRupiah);
        }

        //JUMLAH FILM DISEWA
        model.addAttribute("jumlahPenyewaan", this.laporanService.getFilmDisewaFilterTanggal(tanggalAwal, tanggalAkhir).get().getJumlahPenyewaan());

        //TOP GENRE
        TopGenre topGenre = this.laporanService.getTopGenreFilterTanggal(tanggalAwal, tanggalAkhir);
        if(topGenre == null){
            model.addAttribute("topGenre", "Belum ada Penyewaan");
        }else{
            model.addAttribute("topGenre", topGenre.getNamaGenre());
        }

        //GRAPH
        model.addAttribute("graphTitle", "Bulan " + tanggalAwal + " / " + tanggalAkhir);
        model.addAttribute("graphData", this.laporanService.getGraphDataFilterTanggal(tanggalAwal, tanggalAkhir));

        //TOP 5 FILM PALING LAKU (PALING BANYAK DIPESAN)
        model.addAttribute("top5BestFilm", this.laporanService.getTop5BestFilmFilterTanggal(tanggalAwal, tanggalAkhir));

        //TOP 5 FILM PALING GA LAKU (PALING SEDIKIT PENYEWAAN)
        model.addAttribute("top5WorstFilm", this.laporanService.getTop5WorstFilmFilterTanggal(tanggalAwal, tanggalAkhir));

        //TOP 5 GENRE PALING LAKU (PALING BANYAK DIPESAN)
        model.addAttribute("top5BestGenre", this.laporanService.getTop5GenreFilterTanggal(tanggalAwal, tanggalAkhir));

        return "/admin/laporan";
    }
}