if (localStorage.hasOwnProperty('darkMode')) {
    currentMode = localStorage.getItem('darkMode') === "true" ? "light" : "dark";
    toggleDarkMode();
} else {
    var currentMode = "light";
    localStorage.setItem('darkMode', false);
}
function toggleDarkMode() {
    if (currentMode === "light") {
        currentMode = "dark";
        localStorage.setItem('darkMode', true);
        document.documentElement.setAttribute('data-theme', 'dark')
    } else {
        currentMode = "light";
        localStorage.setItem('darkMode', false);
        document.documentElement.setAttribute('data-theme', 'light')
    }
}
