//CEK TANGGAL
var form = document.getElementById("formTanggal");
var tanggalAwal = document.getElementById("tanggalAwal");
var tanggalAkhir = document.getElementById("tanggalAkhir");
var tombolSubmit = document.getElementById("submitTanggal");

form.addEventListener("submit", function (event) {
    var tanggalAwalValue = new Date(tanggalAwal.value);
    var tanggalAkhirValue = new Date(tanggalAkhir.value);

    // Validasi agar tanggal awal tidak lebih besar dari tanggal akhir
    if (tanggalAwalValue > tanggalAkhirValue) {
        alert("Tanggal Awal tidak boleh lebih besar dari Tanggal Akhir!");
        event.preventDefault();
    }

    var selisihHari = (tanggalAkhirValue - tanggalAwalValue) / (1000 * 60 * 60 * 24); // Selisih dalam hari

    // Validasi agar rentang hari tidak boleh lebih dari 30 hari
    if (selisihHari > 30) {
        alert("Rentang tanggal tidak boleh lebih dari 30 hari!");
        event.preventDefault();
    }
});


//DOWNLOAD LAPORAN
var downloadButton = document.getElementById("downloadButton");
downloadButton.addEventListener("click", () => {
    console.log("Button clicked!");
    const elementToCapture = document.getElementById("contentToCapture");
    if (!elementToCapture) {
        console.error("Element to capture not found!");
        return;
    }

    html2canvas(elementToCapture)
        .then((canvas) => {
            console.log("Screenshot taken");
            const screenshotData = canvas.toDataURL("image/png");

            fetch("/generate-pdf", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ screenshot: screenshotData }),
            })
                .then((response) => {
                    if (!response.ok) throw new Error("PDF generation failed!");
                    return response.blob();
                })
                .then((blob) => {
                    console.log("PDF generated and returned");
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement("a");
                    a.href = url;
                    a.download = "LaporanBulanan.pdf";
                    a.click();
                })
                .catch((err) => console.error("Error generating PDF:", err));
        })
        .catch((err) => console.error("Error capturing screenshot:", err));
});