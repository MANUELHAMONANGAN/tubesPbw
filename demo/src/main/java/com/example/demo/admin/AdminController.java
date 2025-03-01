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

import com.example.demo.aspect.RequiredRole;
import com.example.demo.etalase.EtalaseRepository;
import com.example.demo.laporan.LaporanService;
import com.example.demo.laporan.ScreenshootRequest;
import com.example.demo.laporan.TopGenre;
import com.example.demo.laporan.WeeklySales;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
    @Autowired
    private LaporanService laporanService;

    @Autowired
    private HttpSession session;
    
    @Autowired
    private EtalaseRepository repository;
    
    private static final int maxPage = 5;

    @Autowired
    private AdminRepository repo;

    @RequiredRole("Admin")
    @GetMapping("/admin/")
    public String index(Model model){
        if (session.getAttribute("idUser") == null) {
            return "redirect:/signin";
        }
        
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

    @RequiredRole("Admin")
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
    
    @RequiredRole("Admin")
    @PostMapping("/genre/")
    public String addGenre(Model model, @RequestParam String genre_name){
        genre_name = genre_name.substring(0, 1).toUpperCase() + genre_name.substring(1, genre_name.length());
        this.repo.addGenre(genre_name);
        return "redirect:/genre/";
    }

    @RequiredRole("Admin")
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

    @RequiredRole("Admin")
    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody ScreenshootRequest request) {
        return this.laporanService.generatePdf(request);
    }

    @RequiredRole("Admin")
    @GetMapping("/aktor/edit/")
    public String editAktor(Model model, @RequestParam int idAktor){
        model.addAttribute("pageSaatIni","aktor");
        List<Aktor> user = this.repo.findAktorById(idAktor);
        model.addAttribute("Aktor", user.getFirst());
        return "admin/editAktor";
    }

    @RequiredRole("Admin")
    @PostMapping("/aktor/edit/")
    public String updateAktor(Model model, @RequestParam int idAktor, @RequestParam String nama , @RequestParam Date tanggal_lahir, @RequestParam String deskripsi_diri, @RequestParam MultipartFile foto) throws Exception{
        repo.update(idAktor, nama, tanggal_lahir, deskripsi_diri);
        
        if(foto != null && !foto.isEmpty()){
            byte[] imageBytes =  foto.getBytes();
            repo.updateGambar(idAktor, imageBytes);
        }
        // return "redirect:/aktor/edit/?idAktor=" +idAktor; klo mau tetep di halaman edit
        return "redirect:/aktor/";
    }

    @RequiredRole("Admin")
    @GetMapping("/aktor/tambah/")
    public String addAktor(Model model){
        model.addAttribute("pageSaatIni","aktor");
        return "admin/addAktor";
    }

    @RequiredRole("Admin")
    @PostMapping("/aktor/tambah/")
    public String postAddAktor(Model model, @RequestParam String nama, @RequestParam int tanggal_lahir, @RequestParam int bulan_lahir, @RequestParam int tahun_lahir, @RequestParam String deskripsi_diri, @RequestParam MultipartFile foto) throws Exception{
        LocalDate localDate = LocalDate.of(tahun_lahir, bulan_lahir, tanggal_lahir);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        repo.addAktor(nama, sqlDate, deskripsi_diri, foto.getBytes());
        return "redirect:/aktor/";
    }


    @RequiredRole("Admin")
    @GetMapping("/koleksi_film/")
    public String koleksi_film(Model model, @RequestParam( defaultValue = "",required = false) String filter,
    @RequestParam(defaultValue = "1", required = false) Integer page){
        model.addAttribute("pageSaatIni","koleksiFilm");
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
        model.addAttribute("pageSaatIni","koleksiFilm");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "admin/listfilm";
    }


    @RequiredRole("Admin")
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
  
    @GetMapping("/koleksi_film/tambah/")
    public String tambah_koleksi(Model model){
        model.addAttribute("pageSaatIni","koleksiFilm");
        List<Genre> listGenre = this.repo.findAllGenre();
        model.addAttribute("genres", listGenre);
        return "admin/addFilm";
    }

    @PostMapping("/koleksi_film/tambah/")
    public String post_tambah_koleksi(Model model,
    @RequestParam String judul, 
    @RequestParam int harga, 
    @RequestParam int stok, 
    @RequestParam int durasi, 
    @RequestParam int tahun_rilis, 
    @RequestParam double rating, 
    @RequestParam(required = false) List<String> genre, 
    @RequestParam String deskripsi_film, 
    @RequestParam MultipartFile foto)throws Exception{

        int idFilm = this.repo.addFilm(judul, stok, foto.getBytes(), harga, deskripsi_film, durasi, tahun_rilis, rating) ;

        for(int i=0;i<genre.size();i++){
            this.repo.addFilmGenre(idFilm, Integer.parseInt(genre.get(i)));
        }
        return "redirect:/koleksi_film/";
    } 

    @GetMapping("/koleksi_film/edit/")
    public String edit_film(Model model, @RequestParam int idFilm){
        model.addAttribute("pageSaatIni","koleksiFilm");
        List<Genre> listGenre = this.repo.findAllGenre();
        model.addAttribute("genres", listGenre);

        List<Genre> listPickedGenre = this.repo.findGenreByIdFilm(idFilm);
        model.addAttribute("pickedGenres", listPickedGenre);

        List<Film> film = this.repo.findFilmById(idFilm);
        model.addAttribute("Film", film.getFirst());
        return "admin/editFilm";
    }

    @PostMapping("/koleksi_film/edit/")
    public String post_edit_film(Model model,
    @RequestParam int idFilm,
    @RequestParam String judul, 
    @RequestParam int harga, 
    @RequestParam int stok, 
    @RequestParam int durasi, 
    @RequestParam int tahun_rilis, 
    @RequestParam double rating, 
    @RequestParam(required = false) List<String> genre, 
    @RequestParam String deskripsi, 
    @RequestParam MultipartFile foto)throws Exception{
        repo.updateFilm(idFilm, judul, stok, harga, deskripsi, durasi, tahun_rilis, rating);;

        //To-Do-List if Possible, Hapus cuman yang di uncheck. Engga delete semua trus insert lagi 
        List<Genre> listPickedGenre = this.repo.findGenreByIdFilm(idFilm);
        for(int i=0;i<listPickedGenre.size();i++){
            this.repo.deleteFilmGenre(idFilm, Integer.parseInt(listPickedGenre.get(i).getIdGenre()));
        }

        for(int i=0;i<genre.size();i++){
            this.repo.addFilmGenre(idFilm, Integer.parseInt(genre.get(i)));
        }

        if(foto != null && !foto.isEmpty()){
            byte[] imageBytes =  foto.getBytes();
            repo.updateGambarFilm(idFilm, imageBytes);
        }    

        return "redirect:/koleksi_film/";
    }
}