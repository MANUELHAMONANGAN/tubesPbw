

function initDoB(month_el){
    const listBulan = [
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    ];

    for (let i=0;i<12;i++){
        let month = document.createElement('option');
        month.value = `${i+1}`;
        month.textContent = listBulan[i];
        month_el.appendChild(month);
    }
}

(function(){
    initDoB(document.querySelector(".bulan_lahir_dropdown"));
})();