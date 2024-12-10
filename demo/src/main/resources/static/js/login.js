(function() {
    let signUpBtn = document.querySelector("#overlay_sign_up>input");
    let signInBtn = document.querySelector("#overlay_sign_in>input");

    let signUpForm = document.querySelector("#sign_up");
    let signInForm = document.querySelector("#sign_in");
    let overlaySignIn = document.querySelector("#overlay_sign_in");
    let overlaySignUp = document.querySelector("#overlay_sign_up");

    signUpBtn.addEventListener("click", showSignUpForm => {
        signUpForm.classList.add("showRight");
        overlaySignIn.classList.add("showLeft");
        signInForm.classList.add("hideRight");
        overlaySignUp.classList.add("hideLeft");
    });

    signInBtn.addEventListener("click", hideSignUpForm => {
        signUpForm.classList.remove("showRight");
        overlaySignIn.classList.remove("showLeft");
        signInForm.classList.remove("hideRight");
        overlaySignUp.classList.remove("hideLeft");
    });
    
})();