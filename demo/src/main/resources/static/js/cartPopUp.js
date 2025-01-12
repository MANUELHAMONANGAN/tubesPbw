(function() {
    const popup = document.getElementById('popupRental');
    const overlay = document.getElementById('overlay');
    const pesanBtn = document.getElementById('pesan-sekarang-btn');
    const closeBtn = document.getElementById('closeBtn');
    const confirmBtn = document.getElementById('confirmBtn');
    const metodePembayaran = document.getElementById('metodePembayaran');
    const orderForm = document.getElementById('orderForm');
            
    function showPopup() {
        popup.style.display = 'block';
        overlay.style.display = 'block';
        document.getElementById('selectedPayment').textContent = metodePembayaran.value;
    }
            
    function hidePopup() {
        popup.style.display = 'none';
        overlay.style.display = 'none';
    }
            
    pesanBtn.addEventListener('click', showPopup);
    closeBtn.addEventListener('click', hidePopup);
            
    confirmBtn.addEventListener('click', function() {
        // Submit form data
        const formData = new FormData(orderForm);
        
        fetch('/transaksi/process', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.text();
        })
        .then(data => {
            console.log("Transaksi berhasil dibuat:", data);
            alert("Pesanan Anda telah berhasil dibuat dan sedang menunggu persetujuan admin");
            window.location.href = "/"; // Redirect ke halaman utama
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Terjadi kesalahan: " + error.message);
        })
        .finally(() => {
            hidePopup();
        });
    });
})();            
