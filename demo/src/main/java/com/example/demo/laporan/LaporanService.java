package com.example.demo.laporan;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

@Service
public class LaporanService {
    @Autowired
    private TransaksiGraphRepository transaksiGraphRepository;

    @Autowired
    private WeeklySalesRepository weeklySalesRepository;

    @Autowired
    private FilmDisewaRepository filmDisewaRepository;

    @Autowired
    private TopGenreRepository topGenreRepository;

    @Autowired
    private TopFilmRepository topFilmRepository;

    public List<List<Object>> getGraphDataThisMonth(){
        List<List<Object>> graphData = new ArrayList<>();
        this.transaksiGraphRepository.getGraphDataThisMonth().forEach(data -> {
            List<Object> row = new ArrayList<>();
            row.add(data.getTanggal().toString());
            row.add(data.getTotal().doubleValue());
            graphData.add(row);
        });

        return graphData;
    }

    public WeeklySales getWeeklySalesThisMonth(){
        Optional<WeeklySales> weeklySales = weeklySalesRepository.getWeeklySalesThisMonth();
        if(weeklySales.isPresent()){
            return weeklySales.get();
        }else{
            return null;
        }
    }

    public Optional<FilmDisewa> getFilmDisewaThisMonth(){
        return this.filmDisewaRepository.getFilmDisewaThisMonth();
    }

    public TopGenre getTopGenreThisMonth(){
        Optional<TopGenre> topGenre = this.topGenreRepository.getTopGenreThisMonth();
        if(topGenre.isPresent()){
            return topGenre.get();
        }else{
            return null;
        }
    }

    public List<TopFilm> getTop5BestFilmThisMonth(){
        return this.topFilmRepository.getTop5BestFilmThisMonth();
    }

    public List<TopFilm> getTop5WorstFilmThisMonth(){
        return this.topFilmRepository.getTop5WorstFilmThisMonth();
    }

    public List<TopFilm> getListOutOfStockThisMonth(){
        return this.topFilmRepository.getListOutOfStockThisMonth();
    }

    public List<TopGenre> getTop5GenreThisMonth(){
        return this.topGenreRepository.getTop5GenreThisMonth();
    }

    //-------------------------------------------------------------------------------
    //FILTER TANGGAL
    public List<List<Object>> getGraphDataFilterTanggal(String tanggalAwal, String tanggalAkhir){
        List<List<Object>> graphData = new ArrayList<>();
        this.transaksiGraphRepository.getGraphDataFilterTanggal(tanggalAwal, tanggalAkhir).forEach(data -> {
            List<Object> row = new ArrayList<>();
            row.add(data.getTanggal().toString());
            row.add(data.getTotal().doubleValue());
            graphData.add(row);
        });

        return graphData;
    }

    public WeeklySales getWeeklySalesFilterTanggal(String tanggalAwal, String tanggalAkhir){
        Optional<WeeklySales> weeklySales = weeklySalesRepository.getWeeklySalesFilterTanggal(tanggalAwal, tanggalAkhir);
        if(weeklySales.isPresent()){
            return weeklySales.get();
        }else{
            return null;
        }
    }

    public Optional<FilmDisewa> getFilmDisewaFilterTanggal(String tanggalAwal, String tanggalAkhir){
        return this.filmDisewaRepository.getFilmDisewaFilterTanggal(tanggalAwal, tanggalAkhir);
    }

    public TopGenre getTopGenreFilterTanggal(String tanggalAwal, String tanggalAkhir){
        Optional<TopGenre> topGenre = this.topGenreRepository.getTopGenreFilterTanggal(tanggalAwal, tanggalAkhir);
        if(topGenre.isPresent()){
            return topGenre.get();
        }else{
            return null;
        }
    }

    public List<TopFilm> getTop5BestFilmFilterTanggal(String tanggalAwal, String tanggalAkhir){
        return this.topFilmRepository.getTop5BestFilmFilterTanggal(tanggalAwal, tanggalAkhir);
    }

    public List<TopFilm> getTop5WorstFilmFilterTanggal(String tanggalAwal, String tanggalAkhir){
        return this.topFilmRepository.getTop5WorstFilmFilterTanggal(tanggalAwal, tanggalAkhir);
    }

    public List<TopGenre> getTop5GenreFilterTanggal(String tanggalAwal, String tanggalAkhir){
        return this.topGenreRepository.getTop5GenreFilterTanggal(tanggalAwal, tanggalAkhir);
    }

    public ResponseEntity<byte[]> generatePdf(@RequestBody ScreenshootRequest request){
        try {
            // Decode Base64 gambar
            String base64Screenshot = request.getScreenshot().split(",")[1]; // Ambil data Base64 setelah prefix
            byte[] imageBytes = Base64.getDecoder().decode(base64Screenshot);

            // Siapkan output stream untuk PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Buat dokumen PDF
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Tambahkan gambar ke PDF
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image image = new Image(imageData);
            image.setAutoScale(true); // Agar gambar menyesuaikan ukuran halaman
            document.add(image);

            // Tutup dokumen
            document.close();

            // Kirim PDF sebagai response
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=laporan.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
