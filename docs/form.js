window.addEventListener('load', function() {
    const vals = document.getElementsByClassName("rating")[0].getElementsByTagName("label");
    for (var i = 0; i < vals.length; i++) {
        vals[i].addEventListener("click", function () {
          document.getElementsByClassName("rating")[0].submit();
        });
    }

    if (localStorage.hasOwnProperty('hasRated')) {
        hide();
    }
});

function hide() {
    document.getElementsByClassName("rating")[0].style.display = "none";
    localStorage.setItem('hasRated', true);
}
