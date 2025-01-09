document.querySelectorAll(".dropdown").forEach(element => {
  element.addEventListener('click', () => {
    const childWrapper = element.nextElementSibling;
    if (childWrapper && childWrapper.classList.contains('child-wrapper')) {
      childWrapper.classList.toggle('hidden');
    }
    
    const arrowSpan = element.querySelector('span:last-child');
    if(arrowSpan.innerHTML.match("^")){
      arrowSpan.classList.toggle('rotated');
    }
  });
});

document.querySelector(".iconSearch").addEventListener('click', () =>{
  const formSubmit = document.getElementsByClassName("formAktor")[0];
  formSubmit.submit();
})