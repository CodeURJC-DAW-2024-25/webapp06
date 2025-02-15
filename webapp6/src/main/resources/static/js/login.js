const loginButton = document.getElementsByClassName("login")[0];
const registerButton = document.getElementsByClassName("register")[0];
const popupContainer = document.getElementsByClassName("popup_container")[0];
const popupOverlay = document.getElementsByClassName("overlay")[0];
const popupTitle = document.getElementsByClassName("login-register_options_title")[0];
const popupLogin = document.getElementsByClassName("login_div")[0];

// todos los elementos con la clase "button_text"
const popupButtonTexts = document.getElementsByClassName("button_text");

// texto inicial de cada "button_text"
const initialPopupButtons = Array.from(popupButtonTexts).map(btn => btn.innerHTML);

loginButton.addEventListener("click", function () {
    popupContainer.classList.add("active");
    popupTitle.innerHTML = "Choose your login method";
    Array.from(popupButtonTexts).forEach((btn, index) => {
        btn.innerHTML = "Login " + initialPopupButtons[index];
    });
});

registerButton.addEventListener("click", function () {
    popupContainer.classList.add("active");
    popupTitle.innerHTML = "Choose your register method";
    Array.from(popupButtonTexts).forEach((btn, index) => {
        btn.innerHTML = "Register " + initialPopupButtons[index];
    });
});

popupOverlay.addEventListener("click", function () {
    popupContainer.classList.remove("active");
    Array.from(popupButtonTexts).forEach((btn, index) => {
        btn.innerHTML = initialPopupButtons[index];
    });
});