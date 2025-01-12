(function() {
    let dropdownBtns = document.querySelectorAll(".dropdown");
    dropdownBtns.forEach(dropdownBtn => {
        let dropdownItems = dropdownBtn.querySelector(".items");
        let p = dropdownBtn.querySelector("p");
        dropdownBtn.addEventListener("mouseover", show =>{
            dropdownItems.classList.add("showDropDown");

            dropdownItems.addEventListener("mouseover", () => {
                p.classList.add("hover");
            });

            dropdownItems.addEventListener("mouseout", () => {
                dropdownItems.classList.remove("showDropDown");
                p.classList.remove("hover");
            });

            p.classList.add("hover");
        });

        dropdownBtn.addEventListener("mouseout", hide => {
            dropdownItems.classList.remove("showDropDown");
            p.classList.remove("hover");
        });
    });
})();