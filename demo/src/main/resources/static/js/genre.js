const closeButton = document.querySelector(".genre-close-pop-up"); 
closeButton.addEventListener('click', () => {
  const popupcontainer = document.getElementsByClassName("genre-pop-up-container")[0];
  popupcontainer.classList.toggle('hidden');
});

const openAddButton = document.querySelector(".add-genre-button");
openAddButton.addEventListener("click", () => {
  const popupcontainer = document.getElementsByClassName("genre-pop-up-container")[0];
  popupcontainer.classList.toggle('hidden');
  
})

const genreChange = document.querySelector(".genre_name");
genreChange.addEventListener('input', () => {
    const panjangChar = genreChange.value.length;
    const toggleAddButton = document.querySelector(".pop-up-content > button");

    if (panjangChar >= 3) {
        toggleAddButton.disabled = false;
    } else {
        toggleAddButton.disabled = true;
    }
});
