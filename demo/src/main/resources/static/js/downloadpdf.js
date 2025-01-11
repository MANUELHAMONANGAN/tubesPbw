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
                    a.download = "laporan.pdf";
                    a.click();
                })
                .catch((err) => console.error("Error generating PDF:", err));
        })
        .catch((err) => console.error("Error capturing screenshot:", err));
});