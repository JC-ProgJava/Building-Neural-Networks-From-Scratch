window.addEventListener('load', function() {
    if (localStorage.hasOwnProperty('hasRated')) {
        hide();
    }
});

const vals = document.getElementsByClassName("rating")[0].getElementsByTagName("label");
for (var i = 0; i < vals.length; i++) {
    vals[i].addEventListener("click", function () {
      this.submit();
    });
}

function hide() {
    document.getElementsByClassName("rating")[0].style.display = "none";
    localStorage.setItem('hasRated', true);
}
