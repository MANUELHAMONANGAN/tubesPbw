(function() {
    let rentalBtn = document.querySelector("#tombolRental");
    let popUp = document.querySelector("#popupRental");

    rentalBtn.addEventListener("click", showPopUp => {
        popUp.classList.add("showPopUp");
        console.log("clicked");
    });
    
    let closeBtn = popUp.querySelector("#closeBtn");
    closeBtn.addEventListener("click", closePopUp => {
        popUp.classList.remove("showPopUp");
    });

    let submitBtn = popUp.querySelector("#formDetailRental > input");
    submitBtn.addEventListener("click", closePopUp => {
        popUp.classList.remove("showPopUp");
    });
})();