if (localStorage.hasOwnProperty('hasRated')) {
    hide();
}

function hide() {
    document.getElementsByClassName("rating")[0].style.display = "none";
    localStorage.setItem('hasRated', true);
}
