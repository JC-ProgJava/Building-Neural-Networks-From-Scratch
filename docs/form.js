if (!localStorage.hasOwnProperty('hasRated')) {
    localStorage.setItem('hasRated', false);
}

window.addEventListener('load', function() {
    if (localStorage.hasOwnProperty('hasRated') && localStorage.getItem('hasRated') === "false") {
        document.getElementsByClassName("rating")[0].style.display = "block";
    }
    
    const vals = document.getElementsByClassName("rating")[0].getElementsByTagName("label");
    for (var i = 0; i < vals.length; i++) {
        vals[i].addEventListener("click", function () {
          document.getElementsByClassName("rating")[0].submit();
        });
    }
});

function hide() {
    document.getElementsByClassName("rating")[0].style.display = "none";
    localStorage.setItem('hasRated', true);
}
