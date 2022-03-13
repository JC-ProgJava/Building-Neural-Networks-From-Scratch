var currentMode = "";

window.onbeforeprint = function(event) {
    if (currentMode === "dark") {
        toggleDarkMode();
    }
};

if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    currentMode = "light";
    toggleDarkMode();
}

window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
    currentMode = e.matches ? "light" : "dark";
    toggleDarkMode();
});

if (localStorage.hasOwnProperty('darkMode')) {
    currentMode = localStorage.getItem('darkMode') === "true" ? "light" : "dark";
    toggleDarkMode();
} else {
    currentMode = "light";
    localStorage.setItem('darkMode', false);
}

window.addEventListener('load', function() {
    if (currentMode === "dark") {
        setTheme("AtomOneDark");
    }
})

function toggleDarkMode() {
    if (currentMode === "light") {
        currentMode = "dark";
        localStorage.setItem('darkMode', true);
        document.documentElement.setAttribute('data-theme', 'dark')
        setTheme("AtomOneDark");
    } else {
        currentMode = "light";
        localStorage.setItem('darkMode', false);
        document.documentElement.setAttribute('data-theme', 'light')
        setTheme("Normal");
    }
}
